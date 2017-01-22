package Colision;

import Schemes.Directions;

import java.awt.*;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;

/**
 * Created by anka on 18.01.17.
 */
public class Direction {
    // Calculating vector
    public static int vector(Point start, Point end){
        return  -(int) (atan2(start.x - end.x, start.y - end.y)*(180/PI));
    }

    // Setting direction
    public static int direction(int vector){
        if (vector < 22.5 && vector >= -22.5) return Directions.UP;
        if (vector < 67.5 && vector >= 22.5) return Directions.UPRIGHT;
        if (vector < 112.5 && vector >= 67.5) return Directions.RIGHT;
        if (vector < 157.5 && vector >= 112.5) return Directions.DOWNRIGHT;
        if (vector < -157.5 || vector >= 157.5) return Directions.DOWN;
        if (vector < -112.5 && vector >= -157.5) return Directions.DOWNLEFT;
        if (vector < -67.5 && vector >= -112.5) return Directions.LEFT;
        if (vector < -22.5 && vector >= -67.5) return Directions.UPLEFT;
        else return -1;
    }
}
