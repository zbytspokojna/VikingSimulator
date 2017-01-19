package Colision;


import Map.Terrain;

import java.awt.*;

public class Distance {

    // Function for Voronoi
    public static double distanceV(double x1, double x2, double y1, double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // Function for area of sphere
    public static double distanceC(double x1, double x2, double y1, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static boolean distanceM(Point location, int length){
        if (location.y - length < 0) return false;
        if (location.y + length > 1000) return false;
        if (location.x - length < 0) return false;
        if (location.x + length > 100) return false;
        return true;
    }
}
