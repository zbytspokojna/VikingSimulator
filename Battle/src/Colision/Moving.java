package Colision;

import java.awt.*;

/**
 * Created by anka on 18.01.17.
 */
public class Moving {
    // Changing location based on direction
    public static void moveUp(Point currentLocation){
        currentLocation.y -= 1;
    }

    public static void moveDown(Point currentLocation){
        currentLocation.y += 1;
    }

    public static void moveRight(Point currentLocation){
        currentLocation.x += 1;
    }

    public static void moveLeft(Point currentLocation){
        currentLocation.x -= 1;
    }

    public static void moveUpRight(Point currentLocation) {
        currentLocation.x += 1;
        currentLocation.y -= 1;
    }

    public static void moveUpLeft(Point currentLocation){
        currentLocation.x -= 1;
        currentLocation.y -= 1;
    }

    public static void moveDownRight(Point currentLocation){
        currentLocation.x += 1;
        currentLocation.y += 1;
    }

    public static void moveDownLeft(Point currentLocation){
        currentLocation.x -= 1;
        currentLocation.y += 1;
    }
}
