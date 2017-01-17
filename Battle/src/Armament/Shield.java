package Armament;

import Schemes.Colors;

import java.awt.*;
import java.util.Random;

import static java.lang.Math.toRadians;

/**
 * Created by anka on 12.01.17.
 */
public class Shield {
    Random r = new Random();
    private int defense = r.nextInt(2) + 1;

    public Shield(){
    }

    public void draw(Graphics g, Point location, int size, int vector){
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Colors.SHIELD);
        g2d.rotate(toRadians(vector), location.x, location.y);
        g2d.fillRect(location.x - size / 4, location.y - size / 2, size / 2, size / 4);
    }

    public int getDefense() {
        return defense;
    }
}
