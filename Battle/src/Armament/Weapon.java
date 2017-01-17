package Armament;

import Schemes.Colors;

import java.awt.*;

import static java.lang.Math.toRadians;

public class Weapon {
    private int range;
    private int accuracy;
    private int damage;
    private int penetration;

    public Weapon(int range, int accuracy, int damage, int penetration ){
        this.range = range;
        this.accuracy = accuracy;
        this.damage = damage;
        this.penetration = penetration;
    }

    public void draw(Graphics g, Point location, int size, int vector){
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Colors.WEAPON);
        g2d.rotate(toRadians(vector + 90), location.x, location.y);
        g2d.fillRect((int) (location.x - size/1.4), (int) (location.y - size/1.8), (int) (size/1.1), size/5);
    }

    public int getRange() {
        return range;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getDamage() {
        return damage;
    }

    public int getPenetration() {
        return penetration;
    }
}