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
import static Colision.Direction.*;
import static Colision.Moving.*;
import static java.lang.Math.*;

public class Viking {
    // Stats for battle
    private int health;
    private double moral;
    private double moralThreshold;
    private int defense;
    private int accuracy;
    private int dodge;
    private int loot;
    private int maxLoot;
    private int state;
    private boolean inBoat;
    private int targeted;
    private boolean leader;
    private int idleCounter;

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
    private int shieldAtBack;

    // Drawing
    private int size;
    private Color color;

    // Map information
    private Terrain map;
    private Village village;
    private Fleet fleet;
    private Building base;
    private Loot lostLoot;

    // Other agents
    private ArrayList<SquadVikings> allies;
    private ArrayList<SquadVillagers> enemies;

    // CONSTRUCTOR
    public Viking(Point location, Terrain map, Village village, Fleet fleet, Building targetBuilding, Building base, Color color, int size, ArrayList<SquadVikings> allies) {
        Random r = new Random();
        // Stats for battle
        if (color == Colors.VIKING_LEADER) leader = true;
        if (!leader) {
            this.health = 100;
            this.moralThreshold = r.nextInt(11) + 20;
            this.defense = r.nextInt(3) + 1;
            this.accuracy = r.nextInt(31) + 30;
            this.dodge = r.nextInt(21) + 10;
        } else {
            this.health = 200;
            this.moralThreshold = r.nextInt(6) + 5;
            this.defense = r.nextInt(3) + 2;
            this.accuracy = r.nextInt(31) + 40;
            this.dodge = r.nextInt(21) + 20;
        }
        this.moral = 100;
        this.loot = 0;
        this.maxLoot = 1;
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
        if (r.nextInt(101) > 50) this.shield = new Shield();
        else this.shield = null;
        this.shieldDirection = -(r.nextInt(61) + 30);
        this.shieldAtBack = -180;

        // Drawing
        this.size = size;
        this.color = color;

        // Map information
        this.map = map;
        this.village = village;
        this.fleet = fleet;
        this.base = base;
        this.lostLoot = null;

        // Other agents
        this.allies = allies;
        // TODO: 17.01.17
// HERE IS A BUG!!!!!!!!
        // Setting targetBoat
        boolean found = false;
        for (Boat i : fleet.getBoats()) {
            if (i.getTargetBuilding() == targetBuilding)
                if (i.getSize() > i.getVikings().size()) {
                    this.targetBoat = i;
                    i.addViking(this);
                    found = true;
                }
            if (found) break;
        }
// HERE IS A BUG!!!!!!!!

        //  Setting currentTarget to boat
        this.currentTarget = targetBoat.getCurrentLocation();

        vector = vector(currentLocation, currentTarget);
        direction = direction(vector);
    }


    // SETTERS
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
    }
    public void setTargetBuilding(Building targetBuilding) {
        this.targetBuilding = targetBuilding;
    }
    public void setMaxLoot(int maxLoot){
        this.maxLoot = maxLoot;
    }

    // Changing states
    public boolean setLooting() {
        if (state != States.DEAD && state != States.RETREAT && !inBoat && loot < maxLoot ) {
            state = States.LOOTING;
            return true;
        }
        else return false;
    }
    public void unsetLooting() {
        if (state == States.LOOTING)
            state = States.FIGHT;
    }
    public void setLoss() {
        if (state != States.DEAD && state != States.WAITING)
            state = States.LOSS;
        moral = 100;
    }
    public void setWin() {
        if (state != States.DEAD && state != States.WAITING)
            state = States.WIN;
        moral = 100;
    }
    public void setReAttack() {
        if (state != States.DEAD) {
            state = States.FIGHT;
            moral = 100;
        }
    }
    public void setFighting() {
        if (state != States.DEAD && state != States.LOSS && state != States.WIN && state != States.RETREAT)
            state = States.FIGHT;
    }

    // Changing targeted and moral
    public void setTargeted() {
        targeted ++;
    }
    public void unsetTargeted() {
        targeted--;
    }
    public void increaseMoral(double increase) {
        moral += increase;
        if (moral > 100) moral = 100;
    }
    public void decreaseMoral(double decrease) {
        moral -= decrease;
        if (moral < 0) moral= 0;
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
    public boolean getInBoat() {
        return inBoat;
    }
    public int getDodge() {
        return dodge;
    }
    public int getTargeted() {
        return targeted;
    }



    // OTHER FUNCTIONS

    // Moral
    public void updateMoral() {
        int ally = 0, enemy = 0, difference;
        // count allies in area
        if (!inBoat) {
            for (SquadVikings i : allies)
                for (Viking j : i.getVikings())
                    if (j != this)
                        if (distanceC(currentLocation.x, j.getCurrentLocation().x, currentLocation.y, j.getCurrentLocation().y) < 25  && j.getHealth() > 0)
                            ally++;
            // count enemies in ares
            for (SquadVillagers i : enemies)
                for (Villager j : i.getVillagers())
                    if (distanceC(currentLocation.x, j.getCurrentLocation().x, currentLocation.y, j.getCurrentLocation().y) < 25 && j.getHealth() > 0)
                        enemy++;
            // update moral
            difference = enemy - ally;
            if (difference > 0) decreaseMoral((difference * difference) / 5);
            else increaseMoral((difference * difference) / 5);
            if (state == States.RETREAT) increaseMoral(0.01);
        }
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
                if (targetBuilding.getLoot() == 0 || loot == maxLoot)
                    state = States.FIGHT;
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
            case States.IDLE:
                if (targetEnemy != null ) state = States.FIGHT;
                else {
                    idleCounter ++;
                    if (idleCounter > 200) state = States.RETREAT;
                }
        }
    }

    // Updating currentTarget based on state
    private void updateCurrentTarget(){
        switch (state){
            case States.DEAD:
                currentTarget = currentLocation;
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
            case States.WAITING:
                currentTarget = targetBuilding.getLocation();
                break;
            case States.LOSS:
                if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS)
                    currentTarget = base.getLocation();
                else
                    currentTarget = targetBoat.getCurrentLocation();
                break;
            case States.WIN:
                if (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS)
                    currentTarget = base.getLocation();
                else
                    currentTarget = targetBoat.getCurrentLocation();
                break;
        }
        if (inBoat) currentTarget = targetBoat.getTargetLocation();
    }
    public void findTargetEnemy(int radius) {
        boolean found = false;
        // If fighting or idling find target
        if ((state == States.FIGHT || state == States.IDLE) && (targetEnemy == null || targetEnemy.getHealth() == 0)) {
            if (targetEnemy != null && (targetEnemy.getState() == States.DEAD || targetEnemy.getState() == States.RETREAT || targetEnemy.getState() == States.LOSS)) {
                targetEnemy.unsetTargeted();
                targetEnemy = null;
            }
            double building = sqrt((targetBuilding.getWidth()*targetBuilding.getWidth()) + targetBuilding.getHeight()*targetBuilding.getHeight());
            for (SquadVillagers i : enemies) {
                for (Villager j : i.getVillagers())
                    if (j.getHealth() > 0)
                        if (distanceC(targetBuilding.getLocation().x, j.getCurrentLocation().x, targetBuilding.getLocation().y, j.getCurrentLocation().y) < building*radius) {
                            if (j.getTargeted() < 3 && (j.getState() != States.RETREAT || j.getState() != States.LOSS)) {
                                targetEnemy = j;
                                j.setTargeted();
                                state = States.FIGHT;
                                found = true;
                            }
                    if (found) return;
                }
                if (found) return;
            }
            return;
        }
        // If not figthing loose target
        if (state != States.FIGHT && state != States.IDLE && targetEnemy != null){
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

    // Actions
    public void action() {
        // update target
        findTargetEnemy(2);
        updateCurrentTarget();

//        System.out.println(state + ":" + inBoat);

        // update vectors
        vector = vector(currentLocation, currentTarget);
        direction = direction(vector);
        double radius = sqrt((targetBuilding.getWidth()*targetBuilding.getWidth()) + targetBuilding.getHeight()*targetBuilding.getHeight());

        // if dead
        if (state == States.DEAD){
            if (loot != 0)
                dropLoot();
            return;
        }

        // if should be going to boat
        if ( ((state == States.RETREAT || state == States.WIN || state == States.LOSS) && !inBoat && map.getTerrainGrid()[currentLocation.x][currentLocation.y] != Colors.HILLS) || (map.getTerrainGrid()[currentLocation.x][currentLocation.y] == Colors.HILLS && state == States.FIGHT)){
            if (distanceFromTargetBoat() < targetBoat.getLength()*2) {
                currentLocation.x = targetBoat.getCurrentLocation().x;
                currentLocation.y = targetBoat.getCurrentLocation().y;
                inBoat = true;
                return;
            }
            else {
                move();
                return;
            }
        }

        // if fighting // TODO: 20.01.17 implement behaviour for bow
        if (state == States.FIGHT && !inBoat){
            if (targetEnemy == null){
                if (distanceFromTargetBuilding() > radius)
                    move();
                else
                    state = States.IDLE;
                return;
            }
            else if (distanceFromTargetEnemy() <= size + primeWeapon.getRange()) {
                attack();
                return;
            }
            else if (distanceFromTargetBuilding() > radius*3){
                targetEnemy.unsetTargeted();
                targetEnemy = null;
                currentTarget = targetBuilding.getLocation();
                move();
                return;
            }
            else {
                move();
                return;
            }
        }

        // if looting
        if (state == States.LOOTING){
            if (distanceFromTargetBuilding() <= targetBuilding.getHeight()/5){
                loot += targetBuilding.removeLoot();
                return;
            }
            else {
                move();
                return;
            }
        }

        if ( (state == States.LOSS || state == States.WIN || inBoat) && state != States.RETREAT ){
            if (currentLocation.x == targetBoat.getTargetLocation().x && currentLocation.y == targetBoat.getTargetLocation().y) {
                exit();
                return;
            }
            else {
                if (!inBoat)
                    move();
                return;
            }
        }

        if (state == States.WAITING){
            // TODO: 21.01.17 Idle move from Kaśka
        }
        if (state == States.IDLE){
            if (targetEnemy == null) findTargetEnemy(5);
            if (targetBuilding.getLoot() != 0 && loot == maxLoot) changeTargetBuilding();
            updateCurrentTarget();
        }
    }

    private void changeTargetBuilding() {
        for (Building building : village.getBuildings()){
            if (building != targetBuilding && building.getLoot() > 0 ){
                targetBuilding = building;
                state = States.FIGHT;
            }
        }
    }

    private void dropLoot() {
        if (loot != 0)
            lostLoot = new Loot(loot, currentLocation);
        loot = 0;
    }
    private void exit(){
        Random r = new Random();
        boolean generated = false, noColision;
        Point location = new Point();
        // generating random point in radius of a boat
        while (!generated) {
            double angle = toRadians(random() * 360);
            double radius = r.nextInt((targetBoat.getLength()*2));
            location.x = currentLocation.x + (int) (radius * cos(angle));
            location.y = currentLocation.y + (int) (radius * sin(angle));
            // if in bounds and on land
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
    private boolean checkExit(Point toCheck){
        if(toCheck.x < size || toCheck.y < size || toCheck.x > map.numRows - size || toCheck.y > map.numCols - size)
            return false;
        Point location;
        double angle2 = 0;
        while (angle2 < 6.3) {
            location = new Point(toCheck);
            location.x += (int) (size * cos(angle2));
            location.y += (int) (size * sin(angle2));
            if(location.x > 0 && location.y > 0 && location.x < map.numRows && location.y < map.numCols)
                if (map.getTerrainGrid()[location.x ][location.y] == Colors.OCEAN)
                    return false;
            angle2 += 0.3;
        }
        return true;
    }
    private void attack() {
        Random r = new Random();
        if (r.nextInt(101) <= accuracy + primeWeapon.getAccuracy()){
            if (r.nextInt(101) > targetEnemy.getDodge()) {
                int damage = r.nextInt(8) + 1;
                targetEnemy.damage(damage + primeWeapon.getDamage(), primeWeapon.getPenetration());
                // if got a hit
                moral += damage/30;
                for (SquadVikings squadVikings : allies)
                    for (Viking viking : squadVikings.getVikings())
                        if (viking != this)
                            if (distanceC(currentLocation.x, viking.getCurrentLocation().x, currentLocation.y, viking.getCurrentLocation().y) <= 20)
                                viking.increaseMoral(0.01);
            }
        }
        // If he missed
        else moral -= 0.03;
    }

    // TODO: 20.01.17 make him and people around loss moral
    public void damage(int damage, int penetration) {
        int def = defense;
        if (shield != null ) def += shield.getDefense();
        def = defense - penetration;
        if (def < 0) def = 0;
        if (def >= damage) return;
        health -= (damage - def);
        if (health < 0) health = 0;
        if (leader)
            color = new Color(color.getRed() + (damage-def)/2, color.getGreen() + (damage-def)/2, color.getBlue());
        else
            color = new Color(color.getRed() + (damage-def)*2, color.getGreen() + (damage-def)*2 , color.getBlue());
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
            // Viking
            g2d.setColor(color);
            g2d.rotate(toRadians(vector), currentLocation.x, currentLocation.y);
            g2d.fillOval(currentLocation.x - size / 2, currentLocation.y - size / 2, size, size);
            // Weapon
            primeWeapon.draw(g, currentLocation, size, vector);
            // Shield
            if (shield != null) {
                if (state == States.RETREAT || state == States.WAITING || state == States.LOOTING || state == States.LOSS || inBoat)
                    shield.draw(g, currentLocation, size, vector + shieldAtBack);
                else
                    shield.draw(g, currentLocation, size, vector + shieldDirection);
            }
        }
        else {
            // Loot
            if (lostLoot != null)
                lostLoot.draw(g);
        }
    }
}