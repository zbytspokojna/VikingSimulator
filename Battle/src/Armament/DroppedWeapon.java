package Armament;

import java.awt.*;

/**
 * Created by anka on 12.01.17.
 */
public class DroppedWeapon {
    private Weapon weapon;
    private Point location;

    public DroppedWeapon(Weapon weapon, Point location) {
        this.weapon = weapon;
        this.location = new Point(location);
    }

    public void draw(Graphics g){

    }
}
