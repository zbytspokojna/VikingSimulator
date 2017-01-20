package Army;

import Armament.Shield;
import Armament.Weapon;
import Map.Building;
import Map.Terrain;
import Map.Village;
import Schemes.Colors;
import Schemes.Directions;
import Schemes.States;
import Schemes.Weapons;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static Colision.Distance.distanceC;
import static Colision.Moving.*;
import static Colision.Moving.moveUpLeft;
import static java.lang.Math.*;
import static java.lang.Math.sin;

public class Villager {
    // Stats for battle
    private int health;
    private int moral;
    private int moralThreshold;
    private int defense;
    private int accuracy;
    private int dodge;
    private int loot;
    private int state;
    private boolean inForest;
    private int targeted;

    // Stats for locations and targets
    private Point speed;
    private Point currentLocation;
    private Point previousLocation;
    private Building targetLocation;
    private Viking targetEnemy;
    private int vector;
    private int direction;

    // Stats for armament
    private Weapon primeWeapon;
    private Shield shield;
    private int shieldDirection;

    // Drawing
    private int size;
    private Color color;

    // Map information
    private Terrain map;
    private Village village;

    // Other agents
    private ArrayList<SquadVillagers> allies;
    private ArrayList<SquadVikings> enemies;

    public Villager(Point location, Terrain map, Village village, Building targetLocation, Color color, int size, ArrayList<SquadVillagers> allies){
        Random r = new Random();
        // Stats for battle
        this.health = 100;
        this.moral = r.nextInt(21) + 40;
        this.moralThreshold = r.nextInt(11) + 20;
        this.defense = r.nextInt(3) + 1;
        this.accuracy = r.nextInt(31) + 30;
        this.dodge = r.nextInt(21) + 10;
        this.loot = 0;
        this.state = 1;
        this.inForest = false;
        this.targeted = 0;

        // Stats for locations and targets
        this.speed = new Point(1,1);
        this.currentLocation = new Point(location);
        this.previousLocation = new Point(location);
        this.targetLocation = targetLocation;
        this.targetEnemy = null;
        this.vector();

        // Stats for armament
        this.primeWeapon = Weapons.ARSENAL[r.nextInt(Weapons.ARSENAL.length)];
        if ( r.nextInt(101) > 50 ) this.shield = new Shield();
        else this.shield = null;
        this.shieldDirection = - (r.nextInt(61) + 30);

        // Drawing
        this.size = size;
        this.color = color;

        // Map information
        this.map = map;
        this.village = village;

        // Other agents
        this.allies = allies;
    }

    // Setters
    public void setEnemies(ArrayList<SquadVikings> enemies) {
        this.enemies = enemies;
    }

    public void setTargetLocation(Building targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void setTargeted() {
        targeted ++;
    }

    public void unsetTargeted() {
        targeted--;
    }

    // Getters
    public Point getCurrentLocation() {
        return currentLocation;
    }

    public int getState() {
        return state;
    }

    public int getHealth() {
        return health;
    }

    public int getLoot() {
        return loot;
    }

    public int getTargeted() {
        return targeted;
    }

    public int getDodge() {
        return dodge;
    }

    public boolean getInForest() {
        return inForest;
    }

    // Moral
    public void updateMoral() {
        int ally = 0, enemy = 0, difference;
        // count allies in area
        if (!inForest) {
            for (SquadVillagers i : allies)
                for (Villager j : i.getVillagers())
                    if (j != this)
                        if (distanceC(currentLocation.x, j.getCurrentLocation().x, currentLocation.y, j.getCurrentLocation().y) < 25  && j.getHealth() > 0)
                            ally++;
            // count enemies in ares
            for (SquadVikings i : enemies)
                for (Viking j : i.getVikings())
                    if (distanceC(currentLocation.x, j.getCurrentLocation().x, currentLocation.y, j.getCurrentLocation().y) < 25 && j.getHealth() > 0)
                        enemy++;
            // update moral
            difference = enemy - ally;
            if (difference > 0) decreaseMoral((difference * difference) / 4);
            else increaseMoral((difference * difference) / 4);
            if (state == States.RETREAT) increaseMoral(0.025);
        }
    }

    private boolean moralCheck(){
        updateMoral();
        return moral > moralThreshold;
    }







    public void damage(int damage, int penetration) {
        int def = defense;
        if (shield != null ) def += shield.getDefense();
        def = defense - penetration;
        if (def < 0) def = 0;
        if (def >= damage) return;
        health -= (damage - def);
        if (health < 0) health = 0;
    }

    // OTHER FUNTIONS

    public void estimateState() {
    }

    public void increaseMoral(double increase) {
        moral += increase;
        if (moral > 100) moral = 100;
    }

    public void decreaseMoral(double decrease) {
        moral -= decrease;
        if (moral < 0) moral= 0;
    }



    private void vector(){              // TODO: 12.01.17 make it based on type of target  
        vector = -(int) (atan2(currentLocation.x - targetLocation.getLocation().x, currentLocation.y - targetLocation.getLocation().y)*(180/PI));
    }


    // MOVING TEMP!!!
    public void move() {
        if (direction == Directions.UP) Up();
        if (direction == Directions.UPRIGHT) UpRight();
        if (direction == Directions.RIGHT) Right();
        if (direction == Directions.DOWNRIGHT) DownRight();
        if (direction == Directions.DOWN) Down();
        if (direction == Directions.DOWNLEFT) DownLeft();
        if (direction == Directions.LEFT) Left();
        if (direction == Directions.UPLEFT) UpLeft();
    }

    private boolean checkB(){
        for (Building i : village.getBuildings()){
            if (distanceC(currentLocation.x, i.getLocation().x, currentLocation.y, i.getLocation().y) < size + i.getHeight()/2 ) return false;
        }
        return true;
    }

    private boolean checkM(){
        // Is in previous location
        if(currentLocation.x == previousLocation.x && currentLocation.y == previousLocation.y) {
            return false;
        }
        // Is outside the border
        if(currentLocation.x < size || currentLocation.y < size || currentLocation.x > map.numRows - size || currentLocation.y > map.numCols - size) {
            return false;
        }
        // Is on the land
        double angle2 = 0;
        while (angle2 < 6.3) {
            if (map.getTerrainGrid()[currentLocation.x + (int) (size/2 * cos(angle2))][currentLocation.y + (int) (size/2 * sin(angle2))] == Colors.OCEAN) {
                return false;
            }
            angle2 += 0.3;
        }
        return true;
    }

    private boolean check(){
        return (/*checkB() &&*/ checkM());
    }

    private void setPreviousLocation(int x, int y){
        previousLocation.x = x;
        previousLocation.y = y;
    }


    private void Left() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryLeft(x,y))
            if (!tryDownLeft(x,y))
                if (!tryDown(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryUpLeft(x,y))
                            if (!tryUp(x,y))

                                if (!tryUpRight(x,y))
                                    tryRight(x,y);
    }

    private void Right() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryRight(x,y))
            if (!tryDownRight(x,y))
                if (!tryUpRight(x,y))
                    if (!tryDown(x,y))
                        if (!tryUp(x,y))
                            if (!tryDownLeft(x,y))
                                if (!tryUpLeft(x,y))
                                    tryLeft(x,y);
    }

    private void Up() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryUp(x,y))
            if (!tryUpRight(x,y))
                if (!tryRight(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryUpLeft(x,y))
                            if (!tryLeft(x,y))
                                if (!tryDownLeft(x,y))
                                    tryDown(x,y);
    }

    private void Down() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDown(x,y))
            if (!tryDownRight(x,y))
                if (!tryDownLeft(x,y))
                    if (!tryRight(x,y))
                        if (!tryLeft(x,y))
                            if (!tryUpRight(x,y))
                                if (!tryUpLeft(x,y))
                                    tryUp(x,y);
    }

    private void UpRight() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryUpRight(x,y))
            if (!tryRight(x,y))
                if (!tryUp(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryUpLeft(x,y))
                            if (!tryDown(x,y))
                                if (!tryLeft(x,y))
                                    tryDownLeft(x,y);
    }

    private void DownRight() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDownRight(x,y))
            if (!tryDown(x,y))
                if (!tryRight(x,y))
                    if (!tryDownLeft(x,y))
                        if (!tryUpRight(x,y))
                            if (!tryLeft(x,y))
                                if (!tryUp(x,y))
                                    tryUpLeft(x,y);
    }

    private void UpLeft() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryUpLeft(x,y))
            if (!tryLeft(x,y))
                if (!tryDownLeft(x,y))
                    if (!tryDown(x,y))
                        if (!tryDownRight(x,y))
                            if (!tryRight(x,y))
                                if (!tryUpRight(x,y))
                                    tryUp(x,y);
    }

    private void DownLeft() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDownLeft(x,y))
            if (!tryDown(x,y))
                if (!tryLeft(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryUpLeft(x,y))
                            if (!tryRight(x,y))
                                if (!tryUp(x,y))
                                    tryUpRight(x,y);
    }


    private boolean tryUp(int x, int y){
        moveUp(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDown(currentLocation);
        return false;
    }

    private boolean tryDown(int x, int y){
        moveDown(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUp(currentLocation);
        return false;
    }

    private boolean tryLeft(int x, int y){
        moveLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveRight(currentLocation);
        return false;
    }

    private boolean tryRight(int x, int y){
        moveRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveLeft(currentLocation);
        return false;
    }

    private boolean tryUpLeft(int x, int y){
        moveUpLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDownRight(currentLocation);
        return false;
    }

    private boolean tryUpRight(int x, int y){
        moveUpRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDownLeft(currentLocation);
        return false;
    }

    private boolean tryDownLeft(int x, int y){
        moveDownLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUpRight(currentLocation);
        return false;
    }

    private boolean tryDownRight(int x, int y){
        moveDownRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUpLeft(currentLocation);
        return false;
    }

    // Drawing
    public void draw(Graphics g) {
        if (health > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            // Villager
            g2d.setColor(color);
            g2d.rotate(toRadians(vector), currentLocation.x, currentLocation.y);
            g2d.fillOval(currentLocation.x - size / 2, currentLocation.y - size / 2, size, size);
            // Weapon
            primeWeapon.draw(g, currentLocation, size, vector);
            // Shield
            if (shield != null) shield.draw(g, currentLocation, size, vector + shieldDirection);
        }
    }
}
