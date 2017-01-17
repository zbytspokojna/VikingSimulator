package Fleet;

import Army.Viking;
import Map.*;
import Schemes.Colors;

import static Colision.Distance.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.awt.*;
import java.util.ArrayList;

public class Fleet {
    private ArrayList<Boat> boats;
    private ArrayList<Target> targets;
    private ArrayList<Point> coastH;
    private ArrayList<Point> coastP;
    private int state;                  // 0-waiting, 1-swimming

    // Constructor
    public Fleet(Terrain map, Village village) {
        // Variables for generating
        int number = 2;                                         // number of boats per building
        int width = map.numCols / 130;                          // width of a boat
        int length = 5 * map.numCols / 140;                     // length of a boat/number of seats -- now 5
        int amount = village.getBuildings().size() * number;    // amount of boats

        // Initialazing
        this.boats = new ArrayList<>();
        this.targets = new ArrayList<>();
        this.coastH = map.getCoastH();
        this.coastP = map.getCoastP();
        this.state = 0;

        // Generating boats
        Point location = new Point();
        boolean noColision = true;
        int generated = 0;                                      // number of generated boats

        for (int i = 0; i < coastH.size() && generated < amount; i++) {
            location.x = coastH.get(i).x;
            location.y = coastH.get(i).y;

            // generating starting points
            int tx, ty;                                         //temporary variable for location
            double interval = 1.8;                              //base interval from coast and frame

            for (double k = interval; k > 1.4; k -= 0.1) {
                double angle = 0;
                double radius = length/k;
                noColision = false;
                    // generating points in certain radius
                    while (angle < 6.3 && !noColision) {
                        tx = location.x - (int) (radius * cos(angle));
                        ty = location.y - (int) (radius * sin(angle));
                        // if not outside the borders
                        if (tx > 0 && ty > 0 && tx + length / interval < map.numRows && ty + length / interval < map.numCols) {
                            // checking for terrain
                            noColision = true;
                            double angle2 = 0;
                            while (angle2 < 6.3 && noColision) {
                                if (map.getTerrainGrid()[tx + (int) (length/2 * cos(angle2))][ty + (int) (length/2 * sin(angle2))] != Colors.OCEAN || map.getTerrainGrid()[tx + (int) (length/4 * cos(angle2))][ty + (int) (length/4 * sin(angle2))] != Colors.OCEAN) {
                                    noColision = false;
                                }
                                angle2 += 0.3925;
                            }
                            // checking for boats
                            if (noColision){
                                noColision = true;
                                double spread = 1.1;

                                for (Boat j : boats) {
                                    double distance = distanceC((tx + (width / 2)), (j.getCurrentLocation().x + (j.getWidth() / 2)), (ty + (length / 2)), (j.getCurrentLocation().y + (j.getLength() / 2)));
                                    if (distance < length * spread) {
                                        noColision = false;
                                    }
                                    if (!noColision) break;
                                }
                            }
                            // if there are no colisions set location
                            if (noColision){
                                location.x = tx;
                                location.y = ty;
                            }
                        }
                        angle += 0.3925;
                    }
                if (noColision) break;
            }

            // adding boats
            if (noColision && generated < amount) {
                boats.add(new Boat(map, location, width, length, 5, boats));
                generated++;
            }
        }

        // Generating targets for boats
        Point target = new Point();
        double spread = 2.5;

        for (Building i : village.getBuildings()) {
            Point building = new Point(i.getLocation().x, i.getLocation().y);
            int targeted = 0;
            while (targeted < 2) {
                int min = Integer.MAX_VALUE;
                for (Point coast : coastP) {
                    // distance from building
                    double distance = distanceC(coast.x, building.x, coast.y, building.y);
                    if (distance < min){
                        // distance from other targets
                        noColision = true;
                        for (Target t:targets) {
                            if (distanceC(t.getTarget().x, coast.x, t.getTarget().y, coast.y) < length*spread){
                                noColision = false;
                            }
                        }
                        // setting new mininum and set target;
                        if (noColision) {
                            min = (int) distance;
                            target.x = coast.x;
                            target.y = coast.y;
                        }
                    }
                }
                targets.add(new Target(target, i));
                targeted++;
            }
        }

        // Giving boats targets -- temporary function for checking -- may leave it not so bad :)
            for (int i = 0; i < boats.size(); i++) {
                boats.get(i).setTarget(targets.get(i));
                targets.get(i).use();
            }
        System.out.println(generated + ":" + amount);
    }

    //Getters
    public ArrayList<Boat> getBoats() {
        return boats;
    }

    // OTHER FUNTIONS
    public void returnToBase() {
        for (Boat i : boats){
            i.returnToBase();
        }
    }

    public void estimateState(){
        int ready = 0, size = 0, onLand = 0;
        for (Boat i : boats){
            i.estimateState();
            if (i.getState() == 1) ready++;
            // if they have state inBoat but are on land
            size += i.getVikings().size();
            for (Viking j : i.getVikings()){
                if (j.onLand()) onLand++;
            }
        }
        if (onLand == size){
            state = 0;
            for (Boat i : boats){
                for (Viking j : i.getVikings()){
                    j.setState(1);
                }
            }
        }
        if (ready == boats.size() && onLand != size) state = 1;
        else state = 0;
    }
    
    public void action(){
        estimateState();
        if (state == 1){
            for (Boat i : boats) i.move();
        }
    }

    // Drawing
    public void draw(Graphics g){
        for (Boat i:boats) i.draw(g);
        for (Target i:targets) i.draw(g);
    }

    public void setState(int state) {
        this.state = state;
    }


}