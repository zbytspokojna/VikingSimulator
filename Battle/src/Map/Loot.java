package Map;

import Schemes.Colors;

import java.awt.*;

/**
 * Created by anka on 19.01.17.
 */

public class Loot {
    Point location;
    int amount;

    public Loot(int loot, Point location) {
        this.location = new Point(location);
        this.amount = loot;
    }

    public void draw(Graphics g){
        // Location
        g.setColor(Colors.LOOT);
        g.fillOval(location.x, location.y, 3, 3);
        // Loot
        g.setColor(Colors.TEXT);
        g.drawString(String.valueOf(this.amount), location.x, location.y);
    }
}
