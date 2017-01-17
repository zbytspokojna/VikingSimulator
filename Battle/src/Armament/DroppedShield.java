package Armament;

import java.awt.*;

/**
 * Created by anka on 12.01.17.
 */
public class DroppedShield {
    private Shield shield;
    private Point location;

    public DroppedShield(Shield shield, Point location) {
        this.shield = shield;
        this.location = new Point(location);
    }

    public void draw(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();

    }
}
