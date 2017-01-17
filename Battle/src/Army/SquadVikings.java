package Army;

import Fleet.Fleet;
import Map.*;
import Schemes.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static Colision.Distance.distanceC;
import static java.lang.Math.*;

public class SquadVikings {
    // Stats for squad
    private int size;
    private ArrayList<Viking> vikings;
    private Building target;
    private int state;  //0-all_dead, 1-fight, 2-retreat, 3-looting

    // Map information
    private Terrain map;
    private Village village;
    private Fleet fleet;

    // Other agents
    private ArrayList <SquadVikings> allies;
    private ArrayList <SquadVillagers> enemies;

    public SquadVikings(Terrain map, Village village, Fleet fleet, Building target, Point center, ArrayList<SquadVikings> allies){
        // Variables for generation
        Random r = new Random();
        boolean generated, noColision;
        Point location = new Point();
        Color color;
        int size = map.numCols/120;

        // Stats for squad
        this.size = r.nextInt(3) + 8;
        this.vikings = new ArrayList<>();
        this.target = target;
        this.state = 1;

        // Map information
        this.map = map;
        this.village = village;
        this.fleet = fleet;

        // Other agents
        this.allies = allies;

        // Generating squad
        for (int i = 0; i < this.size; i++){
            generated = false;
            while (!generated) {

                // generating random point in radius of a building
                double angle = toRadians(random() * 360);
                double radius = r.nextDouble() + r.nextInt(26) + size;
                location.x = center.x + (int) (radius * cos(angle));
                location.y = center.y + (int) (radius * sin(angle));

                // checking for vikings
                noColision = true;
                double spread = 1.1;
                for (SquadVikings j : this.allies) {
                    for (Viking k : j.vikings) {
                        double distance = distanceC(location.x, k.getCurrentLocation().x, location.y, k.getCurrentLocation().y);
                        if (distance < size * spread) {
                            noColision = false;
                        }
                    }
                }
                for (Viking m : vikings){
                    double distance = distanceC(location.x, m.getCurrentLocation().x, location.y, m.getCurrentLocation().y);
                    if (distance < size * spread) {
                        noColision = false;
                    }
                }

                // adding viking to squad
                if (noColision) {
                    if (vikings.size() == 0) color = Colors.VIKING_LEADER;                              // toDo make leader a boss! good stats ect :)
                    else color = Colors.VIKING;
                    vikings.add(new Viking(location, map, village, fleet, target, color, size, allies));
                    generated = true;
                }
            }
        }
    }

    // Setters
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
        for (Viking i : vikings){
            i.setEnemies(enemies);
        }
    }

    // Getters
    public int getState() {
        return state;
    }

    public ArrayList<Viking> getVikings() {
        return vikings;
    }

    public int getSize() {
        return size;
    }

    // OTHERS FUNCTIONS
    public void estimateState(){
        // Update state of squads
        for (Viking i : vikings) i.estimateState();

        int dead = 0, counted = 0, retreated = 0;
        // All_dead
        for (Viking i : vikings) {
            if (i.getState() == 0) dead ++;
        }
        if (dead == vikings.size()) {
            state = 0;
            return;
        }

        // Looting
        for (SquadVillagers i : enemies) {
            for (Villager j : i.getVillagers()) {
                if (j.getHealth() > 0) {
                    if (distanceC(target.getLocation().x, j.getCurrentLocation().x, target.getLocation().y, j.getCurrentLocation().y) < 50)
                        counted++;
                }
            }
        }
        if (counted < 2) {
            state = 3;
            return;
        }

        // Retreated
        for (Viking i : vikings){
            if (i.getState() == 4) retreated ++;
        }
        if (retreated == vikings.size() && target.getLoot() != 0){
            state = 2;
            return;
        }

        // Else figth
        state = 1;
        return;
    }

    public void action() {
        updateTargetLocation();         //// TODO: 17.01.17 if not dead and only 3 of them
        if (state == 3) for (Viking i : vikings) i.setState(3);
        for (Viking i : vikings) i.action();
    }

    // Updates
    public void updateTargetLocation(){
        boolean found = false;
        if (target.getLoot() == 0){
            for (Building i : village.getBuildings()){
                if (i.getLoot() != 0){
                    target = i;
                    for (Viking j:vikings) j.setTargetBuilding(i);
                    found = true;
                }
                if (found) break;
            }
        }
    }

    // Drawing
    public void draw(Graphics g) {
        for (Viking i: vikings) i.draw(g);
    }

    public void setState(int state) {
        this.state = state;
        for (Viking i : vikings) i.setState(2);
    }
}
