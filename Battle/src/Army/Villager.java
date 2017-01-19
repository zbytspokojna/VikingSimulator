package Army;

import Armament.Shield;
import Armament.Weapon;
import Map.Building;
import Map.Terrain;
import Map.Village;
import Schemes.Weapons;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.toRadians;

public class Villager {
    // Stats for battle
    private int health;
    private int moral;
    private int defense;
    private int accuracy;
    private int dodge;
    private int loot;
    private int state;              // 0-dead, 1-fight, 2-retreat
    private int targeted;

    // Stats for locations and targets
    private Point speed;
    private Point currentLocation;
    private Point previousLocation;
    private Building targetLocation;
    private Viking targetEnemy;
    private int vector;

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
        this.defense = r.nextInt(3) + 1;
        this.accuracy = r.nextInt(31) + 30;
        this.dodge = r.nextInt(21) + 10;
        this.loot = 0;
        this.state = 1;
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

    private void vector(){              // TODO: 12.01.17 make it based on type of target  
        vector = -(int) (atan2(currentLocation.x - targetLocation.getLocation().x, currentLocation.y - targetLocation.getLocation().y)*(180/PI));
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
