package Fleet;

import Colision.Direction;
import Map.Building;
import Schemes.*;
import Army.*;
import Map.Terrain;

import java.awt.*;
import java.util.ArrayList;

import static Colision.Direction.direction;
import static Colision.Distance.*;
import static Colision.Moving.*;
import static java.lang.Math.*;

public class Boat {
    // Dimentions
    private int width;
    private int length;

    // Seats
    private int size;
    private ArrayList<Viking> vikings;

    // Locations and targets
    private Point startLocation;
    private Point currentLocation;
    private Point previousLocation;
    private Point targetLocation;
    private Building targetBuilding;

    // Moving
    private int vector;
    private int direction;
    private int tryDirection;
    private int stuck;
    private int speed;

    // Terrain and other boats
    private Terrain map;
    private ArrayList<Boat> boats;

    // States
    private int state;
    private boolean atDestination;


    public Boat(Terrain map, Point location, int width, int length, int size, ArrayList<Boat> boats){
        // Dimentions
        this.width = width;
        this.length = length;
        // Seates
        this.size = size;
        this.vikings = new ArrayList<>();
        // Locations and targets
        this.startLocation = new Point(location);
        this.targetLocation = new Point(location);
        this.previousLocation = new Point(location);
        this.currentLocation = new Point(location);
        this.targetLocation = new Point();
        // Moving
        this.vector();
        this.speed = 1;
        this.stuck = 0;
        // Terrain and other boats
        this.map = map;
        this.boats = boats;
        // States
        this.state = 0;
        this.atDestination = false;
    }

    // Setters
    public void setTarget(Target target) {
        int tx, ty;                                         //temporary variable for location
        double interval = 1.7;                              //base interval from coast and frame
        boolean noColision;

        for (double k = interval; k > 0.5; k -= 0.1) {
            double radius = length/k;
            // generating points in certain radius
            for (double angle = 0; angle < 6.3; angle += 0.3) {
                tx = target.getTarget().x + (int) (radius * cos(angle));
                ty = target.getTarget().y + (int) (radius * sin(angle));
                // if not outside the borders
                if (tx - length/interval > 0 && ty-length/interval > 0 && tx < map.numRows && ty < map.numCols){
                    // checking for terrain
                    noColision = true;
                    for (double angle2 = 0; angle2 < 6.3 && noColision; angle2 += 0.3)
                        if (map.getTerrainGrid()[tx + (int) (length/1.9 * cos(angle2))][ty + (int) (length/1.9 * sin(angle2))] != Colors.OCEAN || map.getTerrainGrid()[tx + (int) (length/4 * cos(angle2))][ty + (int) (length/4 * sin(angle2))] != Colors.OCEAN)
                            noColision = false;
                    // if no colision detected
                    if (noColision){
                        this.targetLocation.x = tx;
                        this.targetLocation.y = ty;
                        this.targetBuilding = target.getBuilding();
                        this.vector();
                        return;
                    }
                }
            }
        }
    }

    // Getters
    public int getWidth() {
        return width;
    }
    public int getLength() {
        return length;
    }
    public int getState() {
        return state;
    }
    public Point getCurrentLocation() {
        return currentLocation;
    }
    public Building getTargetBuilding() {
        return targetBuilding;
    }
    public int getSize() {
        return size;
    }
    public ArrayList<Viking> getVikings() {
        return vikings;
    }
    public Point getTargetLocation() {
        return targetLocation;
    }
    public int getDirection() {
        return direction;
    }

    // OTHER FUNCTIONS
    public void returnToBase() {
        targetLocation = startLocation;
    }

    public void addViking(Viking viking){
        vikings.add(viking);
    }

    public void estimateState(){
        if ((currentLocation.x == targetLocation.x && currentLocation.y == targetLocation.y) ||(currentLocation.x == startLocation.x && currentLocation.y == startLocation.y) )
            atDestination = true;
        else
            atDestination = false;
        int counted = 0;
        for (Viking i : vikings)
            if (i.getInBoat() || i.getState() == States.WAITING || i.getState() == States.DEAD) counted++;
        if (counted == vikings.size()) state = 1;
        else state = 0;
    }

    private boolean allDead(){
        int allDead = 0;
        for (Viking viking : vikings)
            if (viking.getState() == States.DEAD)
                allDead++;
        if (allDead == vikings.size())
            return true;
        else
            return false;
    }

    // MOVING
    // Calculate vector
    private void vector(){
        vector = -(int) (atan2(currentLocation.x - targetLocation.x, currentLocation.y - targetLocation.y)*(180/PI));
        direction = direction(vector);
    }

    public void move() {
        if ((currentLocation.x != targetLocation.x || currentLocation.y != targetLocation.y) && vikings.size() > 0 && !allDead()) {
            this.vector();

            if (direction == Directions.UP) {
                if (Up()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.UPRIGHT) {
                if (UpRight()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.RIGHT) {
                if (Right()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.DOWNRIGHT) {
                if (DownRight()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.DOWN) {
                if (Down()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.DOWNLEFT) {
                if (DownLeft()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.LEFT) {
                if (Left()) stuck = 0;
                else stuck++;
            }
            if (direction == Directions.UPLEFT) {
                if (UpLeft()) stuck = 0;
                else stuck++;
            }

            if (stuck > 200) {
                stuck = 0;
                currentLocation = targetLocation;
            }

            for (Viking i : vikings){
                i.getCurrentLocation().x = currentLocation.x;
                i.getCurrentLocation().y = currentLocation.y;
            }
        }
    }

    private boolean checkB() {
        for (Boat i:boats) {
            if (i != this) {
                double distance = distanceC((currentLocation.x +(width/2)),(i.currentLocation.x +(i.width/2)), (currentLocation.y +(length/2)), (i.currentLocation.y +(i.length/2)));
                if (distance <= length/2 + i.getLength()/2)
                    if (i.getDirection() < direction - 1 && !i.atDestination) return false;
            }
        }
        return true;
    }
    private boolean checkM(){
        double interval = 2.5;
        // Is in previous location
        if(currentLocation.x == previousLocation.x && currentLocation.y == previousLocation.y) return false;
        // Is outside the border
        if(currentLocation.x < length/interval || currentLocation.y < length/interval || currentLocation.x > map.numRows - length/interval || currentLocation.y > map.numCols - length/interval) return false;
        // Is on the land
        double angle2 = 0;
        while (angle2 < 6.3) {
            if (map.getTerrainGrid()[currentLocation.x + (int) (length/interval * cos(angle2))][currentLocation.y + (int) (length/interval * sin(angle2))] != Colors.OCEAN) {
                return false;
            }
            angle2 += 0.3;
        }
        return true;
    }
    private boolean check(){
        return (checkM() && checkB());
    }

    private void setPreviousLocation(int x, int y){
        previousLocation.x = x;
        previousLocation.y = y;
    }

    // good for going to Plains bad for comming back
    private boolean Left() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryLeft(x,y))
            if (!tryDownLeft(x,y))
                if (!tryDown(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryRight(x,y))
                            return false;
        return true;
    }
    private boolean UpLeft() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (targetLocation.x < 500) {
            if (!tryUpLeft(x, y))
                if (!tryLeft(x, y))
                    if (!tryDownLeft(x, y))
                        if (!tryDown(x, y))
                            if (!tryDownRight(x, y))
                                return false;
            return true;
        }
        else {
            if (!tryUpLeft(x,y))
                if (!tryUp(x,y))
                    if (!tryUpRight(x,y))
                        if (!tryRight(x,y))
                            if (!tryDownRight(x,y))
                                return false;
            return true;
        }
    }
    private boolean DownLeft() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDownLeft(x,y))
            if (!tryDown(x,y))
                if (!tryDownRight(x,y))
                    if (!tryRight(x,y))
                        if (!tryUpRight(x,y))
                            return false;
        return true;
    }
    private boolean Up() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryUp(x,y))
            if (!tryUpRight(x,y))
                if (!tryRight(x,y))
                    if (!tryDownRight(x,y))
                        if (!tryDown(x,y))
                            return false;
        return true;
    }
    private boolean UpRight() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryUpRight(x,y))
            if (!tryRight(x,y))
                if (!tryDownRight(x,y))
                    if (!tryDown(x,y))
                        if (!tryDownLeft(x,y))
                            return false;
        return true;
    }

    // still to make better
    private boolean Right() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryRight(x,y))
            if (!tryDownRight(x,y))
                if (!tryUpRight(x,y))
                    if (!tryDown(x,y))
                        if (!tryUp(x,y))
                            if (!tryDownLeft(x,y))
                                if (!tryUpLeft(x,y))
                                        return false;
        return true;
    }
    private boolean Down() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDown(x,y))
            if (!tryDownRight(x,y))
                if (!tryDownLeft(x,y))
                    if (!tryRight(x,y))
                        if (!tryLeft(x,y))
                            if (!tryUpRight(x,y))
                                if (!tryUpLeft(x,y))
                                        return false;
        return true;
    }
    private boolean DownRight() {
        int x = currentLocation.x;
        int y = currentLocation.y;
        if (!tryDownRight(x,y))
            if (!tryDown(x,y))
                if (!tryRight(x,y))
                    if (!tryDownLeft(x,y))
                        if (!tryUpRight(x,y))
                            if (!tryLeft(x,y))
                                if (!tryUp(x,y))
                                       return false;
        return true;
    }

    private boolean tryUp(int x, int y){
        tryDirection = Directions.UP;
        moveUp(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDown(currentLocation);
        return false;
    }
    private boolean tryDown(int x, int y){
        tryDirection = Directions.DOWN;
        moveDown(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUp(currentLocation);
        return false;
    }
    private boolean tryLeft(int x, int y){
        tryDirection = Directions.LEFT;
        moveLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveRight(currentLocation);
        return false;
    }
    private boolean tryRight(int x, int y){
        tryDirection = Directions.RIGHT;
        moveRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveLeft(currentLocation);
        return false;
    }
    private boolean tryUpLeft(int x, int y){
        tryDirection = Directions.UPLEFT;
        moveUpLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDownRight(currentLocation);
        return false;
    }
    private boolean tryUpRight(int x, int y){
        tryDirection = Directions.UPRIGHT;
        moveUpRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveDownLeft(currentLocation);
        return false;
    }
    private boolean tryDownLeft(int x, int y){
        tryDirection = Directions.DOWNLEFT;
        moveDownLeft(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUpRight(currentLocation);
        return false;
    }
    private boolean tryDownRight(int x, int y){
        tryDirection = Directions.DOWNRIGHT;
        moveDownRight(currentLocation);
        if (check()) {
            setPreviousLocation(x,y);
            return true;
        }
        moveUpLeft(currentLocation);
        return false;
    }

    //Drawing
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        // Boat
        g2d.setColor(Colors.BOAT);
        g2d.rotate(toRadians(vector), currentLocation.x, currentLocation.y);
        g2d.fillRect(currentLocation.x - width/2, currentLocation.y - length/2, width, length);
        // Center
        g.setColor(Colors.LOCATION);
        g.fillRect(currentLocation.x, currentLocation.y, 1, 1);
        // Target
        g.fillRect(targetLocation.x, targetLocation.y, 3, 3);
    }

}
