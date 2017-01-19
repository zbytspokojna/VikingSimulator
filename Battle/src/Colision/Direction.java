package Colision;

import Schemes.Directions;

/**
 * Created by anka on 18.01.17.
 */
public class Direction {
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
