package Army;

import Armament.Shield;
import Armament.ThrownWeapon;
import Armament.Weapon;
import Fleet.*;
import Map.*;
import Schemes.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static Colision.Distance.distanceC;
import static Colision.Direction.direction;
import static Colision.Moving.*;
import static java.lang.Math.*;

public class Viking {
    // Stats for battle
    private int health;
    private double moral;
    private int moralThreshold;
    private int defense;
    private int accuracy;
    private int dodge;
    private int loot;
    private int state;
    private boolean inBoat;
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
    private int direction;

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

    // CONSTRUCTOR
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
        this.state = States.FIGHT;
        this.inBoat = false;
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
        // TODO: 17.01.17
// HERE IS A BUG!!!!!!!!
        // Setting targetBoat
        boolean found = false;
        for (Boat i : fleet.getBoats()){
            if (i.getTargetBuilding() == targetBuilding)
                if (i.getSize() > i.getVikings().size()){
                    this.targetBoat = i;
                    i.addViking(this);
                    found = true;
                }
            if (found) break;
        }
// HERE IS A BUG!!!!!!!!

        //  Setting currentTarget to boat
        this.currentTarget = targetBoat.getCurrentLocation();

        vector();
    }


    // SETTERS
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
    }

    public void setTargetBuilding(Building targetBuilding) {
        this.targetBuilding = targetBuilding;
    }

    // Changing states
    public boolean setLooting() {
        if (state != States.DEAD && state != States.INBOAT && state != States.RETREAT) {
            state = States.LOOTING;
            return true;
        }
        else return false;
    }

    public void unsetLooting() {
        if (state == States.LOOTING) state = States.FIGHT;
    }

    public void setLoss() {
        if (state != States.DEAD && state != States.WAITING) state = States.LOSS;
    }

    public void setWin() {
        if (state != States.DEAD && state != States.WAITING) state = States.WIN;
    }

    public void setReAttack() {
        if (state != States.DEAD) {
            state = States.FIGHT;
            moral = 100;
        }
    }

    public void setFighting() {
        if (state != States.DEAD && state != States.LOSS && state != States.WIN) state = States.FIGHT;
    }

    public void setComeBack() {
        if (state != States.DEAD) state = States.WIN;
    }

    // Changing state of being targeted
    public void setTargeted() {
        targeted ++;
    }

    public void unsetTargeted() {
        targeted--;
    }


    // GETTERS
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

    public Boat getTargetBoat() {
        return targetBoat;
    }

    public boolean getInBoat() {
        return inBoat;
    }

    // OTHER FUNCTIONS

    // Moral
    public void updateMoral() {
        // TODO: 14.01.17 make it based on situations and stuff
    }
    private boolean moralCheck(){
        updateMoral();
        return moral > moralThreshold;
    }


    // State
    public void estimateState() {
        // No matter what state check if not dead or retreated
        if (health == 0) {
            state = States.DEAD;
            return;
        }
        if (!moralCheck()) {
            state = States.RETREAT;
            return;
        }

        switch (state){
            case States.LOOTING :
                if (targetBuilding.getLoot() == 0 || loot == 2)
                    state = States.FIGHT;
                break;
            case States.INBOAT :
                if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] != Colors.OCEAN)
                    state = States.WAITING;
                break;
            case States.WAITING:
                break;
            case States.WIN:
                break;
            case States.LOSS:
                break;
            case States.DEAD:
                break;
            case States.RETREAT:
                if (moralCheck()) state = States.FIGHT;
                break;
            case States.FIGHT:
                if (!moralCheck()) state = States.RETREAT;
                break;
        }
    }

    public boolean onLand() {
        estimateState();
        return (state == States.WAITING);
    }

    // Updating currentTarget based on state // TODO: 19.01.17  
    private void updateCurrentTarget(){
        switch (state){
            case States.DEAD:
                currentTarget = null;
                break;
            case States.FIGHT:
                if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS)
                    currentTarget = targetBoat.getCurrentLocation();
                else if (targetEnemy != null)
                    currentTarget = targetEnemy.getCurrentLocation();
                else
                    currentTarget = targetBuilding.getLocation();
                break;
            case States.RETREAT:
                currentTarget = targetBoat.getCurrentLocation();
                break;
            case States.LOOTING:
                currentTarget = targetBuilding.getLocation();
                break;
            case States.INBOAT:
                currentTarget = targetBoat.getTargetLocation();
                break;
            case States.WAITING:
                currentTarget = targetBuilding.getLocation();
                break;
            case States.LOSS:
                if (inBoat)
                    currentTarget = targetBoat.getTargetLocation();
                else if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS)
                    currentTarget = new Point(900,900);
                else
                    currentTarget = targetBoat.getCurrentLocation();
                break;
            case States.WIN:
                if (inBoat)
                    currentTarget = targetBoat.getTargetLocation();
                else if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS)
                    currentTarget = new Point(900,900);
                else
                    currentTarget = targetBoat.getCurrentLocation();
                break;
        }
    }

    public void findTargetEnemy() {
        boolean found = false;
        // If fighting
        if (state == States.FIGHT && (targetEnemy == null || targetEnemy.getHealth() == 0)) {
            if (targetEnemy != null && targetEnemy.getHealth() == 0) {
                targetEnemy.unsetTargeted();
                targetEnemy = null;
            }
            if (distanceC(currentLocation.x, targetBuilding.getLocation().x, currentLocation.y, targetBuilding.getLocation().y) < 200) {
                for (SquadVillagers i : enemies) {
                    for (Villager j : i.getVillagers()) {
                        if (j.getHealth() > 0) {
                            if (distanceC(targetBuilding.getLocation().x, j.getCurrentLocation().x, targetBuilding.getLocation().y, j.getCurrentLocation().y) < targetBuilding.getHeight() + 30) {
                                if (j.getTargeted() < 2) {
                                    targetEnemy = j;
                                    j.setTargeted();
                                    found = true;
                                }
                            }
                        }
                        if (found) return;
                    }
                    if (found) return;
                }
            }
            return;
        }
        // If not figthing
        if (state != States.FIGHT && targetEnemy != null){
            targetEnemy.unsetTargeted();
            targetEnemy = null;
        }
    }

    // Distance from targets
    private double distanceFromTargetBuilding(){
        return distanceC(currentLocation.x, targetBuilding.getLocation().x, currentLocation.y, targetBuilding.getLocation().y);
    }

    private double distanceFromTargetEnemy() {
        return distanceC(currentLocation.x, targetEnemy.getCurrentLocation().x, currentLocation.y, targetEnemy.getCurrentLocation().y);
    }

    private double distanceFromTargetBoat(){
        return distanceC(currentLocation.x, targetBoat.getCurrentLocation().x, currentLocation.y, targetBoat.getCurrentLocation().y);
    }


    public void action() {
        findTargetEnemy();
        updateCurrentTarget();
        vector();
        // if dead
        if (state == States.DEAD){
            return;
        }

        // if should be going to boat
        if ( ((state == States.RETREAT || state == States.WIN || state == States.LOSS) && !inBoat && map.getTerrainGrid()[currentLocation.x][currentLocation.y] != Colors.HILLS) || (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS && state == States.FIGHT)){
            if (distanceFromTargetBoat() < targetBoat.getLength()*2) {
                currentLocation.x = targetBoat.getCurrentLocation().x;
                currentLocation.y = targetBoat.getCurrentLocation().y;
                state = States.INBOAT;
                inBoat = true;
                return;
            }
            else {
                move();
                return;
            }
        }

        // if fighting
        if (state == States.FIGHT){
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
        if (state == States.LOOTING){
            if (distanceFromTargetBuilding() <= targetBuilding.getHeight()/3 + size && loot < 2){
                loot += targetBuilding.removeLoot();
                return;
            }
            else {
                move();
                return;
            }
        }

        if ( (state == States.LOSS || state == States.WIN || state == States.INBOAT) ){
            if (currentLocation.x == targetBoat.getTargetLocation().x && currentLocation.y == targetBoat.getTargetLocation().y) {
                exit();
                return;
            }
            else {
                if (!inBoat) move();
                return;
            }
        }

        if (state == States.WAITING){
//            System.out.println("I am waiting");
        }
    }
    private void exit(){
        Random r = new Random();
        boolean generated = false, noColision;
        Point location = new Point();
        // generating random point in radius of a boat
        while (!generated) {
            double angle = toRadians(random() * 360);
            double radius = r.nextInt((targetBoat.getLength())) + targetBoat.getLength()/2;
            location.x = currentLocation.x + (int) (radius * cos(angle));
            location.y = currentLocation.y + (int) (radius * sin(angle));
            // if in bounds
            if (location.x - (radius+size+1) > 0 && location.y - (radius+size+1) > 0 && location.x < map.numRows + (radius+size+1) && location.y < map.numCols + (radius+size+1)) {
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
                        inBoat = false;
                        state = States.WAITING;
                        generated = true;
                    }
                }
            }
        }
    }

    private boolean checkExit(Point location){
        double angle2 = 0;
        while (angle2 < 6.3) {
            if (map.getTerrainGrid()[location.x + (int) (size/2 * cos(angle2))][location.y + (int) (size/2 * sin(angle2))] == Colors.OCEAN) {
                return false;
            }
            angle2 += 0.3;
        }
        return true;
    }

    // TODO: 19.01.17  implement moral increase for viking and his friends in a certain radius if enemy is hit
    private void attack() {
        Random r = new Random();
        if (r.nextInt(101) < accuracy + primeWeapon.getAccuracy()){
            if (r.nextInt(101) >= targetEnemy.getDodge())
                targetEnemy.damage(r.nextInt(6) + 1 + primeWeapon.getDamage(), primeWeapon.getPenetration());
        }
    }


    // MOVING TEMP!!!
    // Vector based on current target
    private void vector(){
        vector = -(int) (atan2(currentLocation.x - currentTarget.x, currentLocation.y - currentTarget.y)*(180/PI));
        direction = direction(vector);
    }

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
                if (!tryUpLeft(x,y))
                    if (!tryDown(x,y))
                        if (!tryUp(x,y))
                            if (!tryDownRight(x,y))
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
                if (!tryUpLeft(x,y))
                    if (!tryRight(x,y))
                        if (!tryLeft(x,y))
                            if (!tryDownRight(x,y))
                                if (!tryUpLeft(x,y))
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
                if (!tryUp(x,y))
                    if (!tryDownLeft(x,y))
                        if (!tryUpRight(x,y))
                            if (!tryDown(x,y))
                                if (!tryRight(x,y))
                                    tryDownRight(x,y);
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