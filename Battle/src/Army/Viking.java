package Army;

import Armament.Shield;
import Armament.ThrownWeapon;
import Armament.Weapon;
import Fleet.*;
import Map.*;
import Schemes.Colors;
import Schemes.Weapons;

import java.awt.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import static Colision.Distance.distanceC;
import static java.lang.Math.*;
import static java.lang.Math.sin;

public class Viking {
    // Stats for battle
    private int health;
    private int moral;
    private int moralThreshold;
    private int defense;
    private int accuracy;
    private int dodge;
    private int loot;
    private int state;      // 0-dead, 1-fight, 2-retreat, 3-looting, 4-inBoat
    private int targeted;

    // Stats for locations and targets
    private int speed;
    private Point currentLocation;
    private Point previousLocation;
    private Building targetBuilding;
    private Villager targetEnemy;
    private Boat targetBoat;
    private Point currentTarget;
    private int vector;

    // Stats for armament
    private Weapon primeWeapon;
    private ArrayList<ThrownWeapon> thrownWeapons;
    private Shield shield;
    private int shieldDirection;

    // Drawing
    private int size;
    private Color color;

    // Map information
    private Terrain map;
    private Village village;
    private Fleet fleet;

    // Other agents
    private ArrayList<SquadVikings> allies;
    private ArrayList<SquadVillagers> enemies;

    // Constructor
    public Viking(Point location, Terrain map, Village village, Fleet fleet, Building targetBuilding, Color color, int size, ArrayList<SquadVikings> allies) {
        Random r = new Random();
        // Stats for battle
        this.health = 100;
        this.moral = 100;
        this.moralThreshold = r.nextInt(11) + 20;
        this.defense = r.nextInt(3) + 1;
        this.accuracy = r.nextInt(31) + 30;
        this.dodge = r.nextInt(21) + 10;
        this.loot = 0;
        this.state = 1;
        this.targeted = 0;

        // Stats for locations and targets
        this.speed = 1;
        this.currentLocation = new Point(location);
        this.previousLocation = new Point(location);
        this.targetBuilding = targetBuilding;
        this.targetEnemy = null;
        this.targetBoat = null;

        // Stats for armament
        this.primeWeapon = Weapons.ARSENAL[r.nextInt(Weapons.ARSENAL.length)];
        this.thrownWeapons = new ArrayList<>();
        for (int i = 0; i < r.nextInt(6) + 5; i++) thrownWeapons.add(new ThrownWeapon());
        if ( r.nextInt(101) > 50 ) this.shield = new Shield();
        else this.shield = null;
        this.shieldDirection = - (r.nextInt(61) + 30);

        // Drawing
        this.size = size;
        this.color = color;

        // Map information
        this.map = map;
        this.village = village;
        this.fleet = fleet;

        // Other agents
        this.allies = allies;
// HERE IS A BUG!!!!!!!!
        // Setting targetBoat
        boolean found = false;
        for (Boat i : fleet.getBoats()){
            if (i.getTargetBuilding() == targetBuilding)
                if (i.getSize() > i.getVikings().size()){
                    this.targetBoat = i;
                    i.addWarrior(this);
                    found = true;
                }
            if (found) break;
        }
// HERE IS A BUG!!!!!!!!
        //  Setting currentTarget to boat
        this.currentTarget = targetBoat.getCurrentLocation();
        vector();
    }

    // Setters
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
    }

    public void setTargeted() {
        targeted ++;
    }

    public void unsetTargeted() {
        targeted--;
    }

    public void setTargetBuilding(Building targetBuilding) {
        this.targetBuilding = targetBuilding;
    }

    public void setCurrentLocation(Point currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setState(int state) {
        this.state = state;
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


    // OTHER FUNCTIONS
    public void updateMoral() {
        // TODO: 14.01.17 make it based on situations and stuff
    }

    private boolean moralCheck(){
        updateMoral();
        return moral > moralThreshold;
    }

    public void estimateState() {
        if (health == 0) state = 0;
        if (!moralCheck()) state = 2;
        if (state == 3) state = 3;
        if (state == 4) state = 4;
        else state = 1;
    }

    // updating currentTarget based on state
    private void updateCurrentTarget(){
        if (state == 0) {
            currentTarget = null;
            return;
        }
        if (state == 1)
            if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS) {
                currentTarget = targetBoat.getCurrentLocation();
                return;
            }
            else if (targetEnemy != null) {
                currentTarget = targetEnemy.getCurrentLocation();
                return;
            }
            else {
                currentTarget = targetBuilding.getLocation();
                return;
            }
        if (state == 2) {
            currentTarget = targetBoat.getCurrentLocation();
            return;
        }
        if (state == 3) {
            currentTarget = targetBuilding.getLocation();
            return;
        }
        if (state == 4) {
            currentTarget = targetBoat.getTargetLocation();
            return;
        }
    }

    private double distanceFromTargetBuilding(){
        return distanceC(currentLocation.x, targetBuilding.getLocation().x, currentLocation.y, targetBuilding.getLocation().y);
    }

    private double distanceFromTargetEnemy() {
        return distanceC(currentLocation.x, targetEnemy.getCurrentLocation().x, currentLocation.y, targetEnemy.getCurrentLocation().y);
    }

    private double distanceFromTargetBoat(){
        return distanceC(currentLocation.x, targetBoat.getCurrentLocation().x, currentLocation.y, targetBoat.getCurrentLocation().y);
    }

    // Vector based on current target
    private void vector(){
        vector = -(int) (atan2(currentLocation.x - currentTarget.x, currentLocation.y - currentTarget.y)*(180/PI));
    }

    public void findTargetEnemy() {
        boolean found = false;
        if (state == 1 && (targetEnemy == null || targetEnemy.getHealth() == 0)) {
            if (distanceC(currentLocation.x, targetBuilding.getLocation().x, currentLocation.y, targetBuilding.getLocation().y) < 200) {
                for (SquadVillagers i : enemies) {
                    for (Villager j : i.getVillagers()) {
                        if (distanceC(targetBuilding.getLocation().x, j.getCurrentLocation().x, targetBuilding.getLocation().y, j.getCurrentLocation().y) < 40) {
                            if (j.getTargeted() < 2) {
                                targetEnemy = j;
                                j.setTargeted();
                                found = true;
                            }
                        }
                        if (found) return;
                    }
                    if (found) return;
                }
            }
        }
    }

    public void action() {
        findTargetEnemy();
        updateCurrentTarget();
        vector();

        // if dead
        if (state == 0){
            return;
        }

        // if retreating or being on hills
        if (state == 2 || map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS){
            if (distanceFromTargetBoat() < targetBoat.getLength()*2) {
                currentLocation.x = targetBoat.getCurrentLocation().x;
                currentLocation.y = targetBoat.getCurrentLocation().y;
                state = 4;
                return;
            }
            else {
                move();
                return;
            }
        }

        // if fighting
        if (state == 1){
            if (targetEnemy == null){
                move();
                return;
            }
            else if (distanceFromTargetEnemy() <= size + primeWeapon.getRange()) {
                attack();
                return;
            }
            else {
                move();
                return;
            }

        }

        // if looting
        if (state == 3){
            if (distanceFromTargetBuilding() <= targetBuilding.getHeight()/2 + size && loot < 3){
                loot += targetBuilding.removeLoot();
                System.out.println("looting");
                return;
            }
            else {
                move();
                return;
            }
        }

        // if sailing
        if (state == 4){
            // get out of boat
            if (currentLocation.x == currentTarget.x && currentLocation.y == currentTarget.y){
                Random r = new Random();
                boolean generated = false, noColision;
                Point location = new Point();
                // generating random point in radius of a boat
                while (!generated) {
                    double angle = toRadians(random() * 360);
                    double radius = r.nextDouble() + r.nextInt((int) (targetBoat.getLength())) + targetBoat.getLength()/2;
                    location.x = currentLocation.x + (int) (radius * cos(angle));
                    location.y = currentLocation.y + (int) (radius * sin(angle));
                    // if in bounds
                    if (location.x - size > 0 && location.y - size > 0 && location.x < map.numRows + size && location.y < map.numCols + size) {
                        // if on land
                        if (checkExit(location)) {
                            // checking for vikings
                            noColision = true;
                            double spread = 1.1;
                            for (SquadVikings j : this.allies) {
                                for (Viking k : j.getVikings()) {
                                    double distance = distanceC(location.x, k.getCurrentLocation().x, location.y, k.getCurrentLocation().y);
                                    if (distance < size * spread) {
                                        noColision = false;
                                    }
                                }
                            }
                            if (noColision) {
                                currentLocation.x = location.x;
                                currentLocation.y = location.y;
                                generated = true;
                            }
                        }
                    }
                }
            }
        }
    }

    // BUGS WITH FLASHING ENEMIES
    private void attack() {
        // TODO: 16.01.17 Make attack system based on DH RP
        Random r = new Random();
        if (r.nextInt(101) < accuracy + primeWeapon.getAccuracy()){
            if (r.nextInt(101) < targetEnemy.getDodge()){
                return;
            }
            else {
                targetEnemy.damage(r.nextInt(5) + primeWeapon.getDamage(), primeWeapon.getPenetration());
                return;
            }
        }
        else return;

    }

    public boolean onLand() {
        return (state == 4 && map.getTerrainGrid()[currentLocation.x][currentLocation.y] != Colors.OCEAN);
    }

    // MOVING TEMP!!!
    public void move(){
        if (vector < 22.5 && vector >= -22.5) {
            moveUp();
            if (check()) return;
            else moveDown();
        }
        if (vector < 67.5 && vector >= 22.5) {
            moveUpRight();
            if (check()) return;
            else moveDownLeft();
        }
        if (vector < 112.5 && vector >= 67.5) {
            moveRight();
            if (check()) return;
            else moveLeft();
        }
        if (vector < 157.5 && vector >= 112.5) {
            moveDownRight();
            if (check()) return;
            else moveUpLeft();
        }
        if (vector < -157.5 && vector >= 157.5) {
            moveDown();
            if (check()) return;
            else moveUp();
        }
        if (vector < -112.5 && vector >= -157.5) {
            moveDownLeft();
            if (check()) return;
            else moveUpRight();
        }
        if (vector < -67.5 && vector >= -112.5) {
            moveLeft();
            if (check()) return;
            else moveRight();
        }
        if (vector < -22.5 && vector >= -67.5) {
            moveUpLeft();
            if (check()) return;
            else moveDownRight();
        }
    }

    private boolean checkB(){
        for (Building i : village.getBuildings()){
            if (distanceFromTargetBuilding() < size/2 + targetBuilding.getHeight()/2 ) return false;
        }
        return true;
    }

    private boolean checkM(){
        // Is in previous location
        if(currentLocation.x == previousLocation.x && currentLocation.y == previousLocation.y) return false;
        // Is outside the border
        if(currentLocation.x < size || currentLocation.y < size || currentLocation.x > map.numRows - size || currentLocation.y > map.numCols - size) return false;
        // Is on the land
        double angle2 = 0;
        while (angle2 < 6.3) {
            if (map.getTerrainGrid()[currentLocation.x + (int) (size/2 * cos(angle2))][currentLocation.y + (int) (size/2 * sin(angle2))] == Colors.OCEAN) {
                return false;
            }
            angle2 += 0.3925;
        }
        return true;
    }

    private boolean check(){
        return (checkB() && checkM());
    }

    private boolean checkExit( Point location){
        double angle2 = 0;
        while (angle2 < 6.3) {
            if (map.getTerrainGrid()[location.x + (int) (size/2 * cos(angle2))][location.y + (int) (size/2 * sin(angle2))] == Colors.OCEAN) {
                return false;
            }
            angle2 += 0.3925;
        }
        return true;
    }

    private void moveUp(){
        currentLocation.y -= speed;
    }

    private void moveDown(){
        currentLocation.y += speed;
    }

    private void moveRight(){
        currentLocation.x += speed;
    }

    private void moveLeft(){
        currentLocation.x -= speed;
    }

    private void moveUpRight() {
        currentLocation.x += speed;
        currentLocation.y -= speed;
    }

    private void moveUpLeft(){
        currentLocation.x -= speed;
        currentLocation.y -= speed;
    }

    private void moveDownRight(){
        currentLocation.x += speed;
        currentLocation.y += speed;
    }

    private void moveDownLeft(){
        currentLocation.x -= speed;
        currentLocation.y += speed;
    }


    // Drawing
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        // Viking
        g2d.setColor(color);
        g2d.rotate(toRadians(vector), currentLocation.x, currentLocation.y);
        g2d.fillOval(currentLocation.x - size / 2, currentLocation.y - size / 2, size, size);
        // Weapon
        primeWeapon.draw(g, currentLocation, size, vector);
        // Shield
        if (shield != null) shield.draw(g, currentLocation, size, vector + shieldDirection);
    }
}