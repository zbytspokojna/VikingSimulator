package Army;

import Map.Building;
import Map.Terrain;
import Map.Village;
import Schemes.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static Colision.Distance.distanceC;
import static java.lang.Math.*;

public class SquadVillagers {
    // Stats for squad
    private int size;
    private ArrayList<Villager> villagers;
    private Building target;
    private int state;  //0-all_dead, 1-fight, 2-retreat

    // Map information
    private Terrain map;
    private Village village;

    // Other agents
    private ArrayList<SquadVikings> enemies;
    private ArrayList<SquadVillagers> allies;

    public SquadVillagers(Terrain map, Village village, Building target, ArrayList<SquadVillagers> allies){
        // Variables for generation
        Random r = new Random();
        boolean generated, noColision;
        Point location = new Point();
        Color color;
        int size = map.numCols/120;
        double radius = sqrt((target.getWidth()*target.getWidth()) + target.getHeight()*target.getHeight())/2 + size;

        // Stats for squad
        this.size = r.nextInt(3) + 8;
        this.villagers = new ArrayList<>();
        this.target = target;
        this.state = 1;

        // Map information
        this.map = map;
        this.village = village;

        // Other agents
        this.allies = allies;

        // Generating squad
        for (int i = 0; i < this.size; i++){
            generated = false;
            while (!generated) {

                // generating random point in radius of a building
                double angle = toRadians(random() * 360);
                location.x = target.getLocation().x + (int) (radius * cos(angle));
                location.y = target.getLocation().y + (int) (radius * sin(angle));

                // checking for villagers
                noColision = true;
                double spread = 1.1;
                for (SquadVillagers j : this.allies) {
                    for (Villager k : j.villagers) {
                        double distance = distanceC(location.x, k.getCurrentLocation().x, location.y, k.getCurrentLocation().y);
                        if (distance < size * spread) {
                            noColision = false;
                        }
                    }
                }
                for (Villager m : villagers){
                    double distance = distanceC(location.x, m.getCurrentLocation().x, location.y, m.getCurrentLocation().y);
                    if (distance < size * spread) {
                        noColision = false;
                    }
                }

                // adding villager to squad
                if (noColision) {
                    if (villagers.size() == 0) color = Colors.VILLAGER_LEADER;                              // toDo make leader a boss! good stats ect :)
                    else color = Colors.VILLAGER;
                    villagers.add(new Villager(location, map, village, target, color, size, allies));
                    generated = true;
                }
            }
        }
    }

    // Setters
    public void setEnemies(ArrayList<SquadVikings> enemies) {
        this.enemies = enemies;
        for (Villager i : villagers){
            i.setEnemies(enemies);
        }
    }

    // Getters
    public int getState() {
        return state;
    }

    public ArrayList<Villager> getVillagers() {
        return villagers;
    }

    // OTHER FUNTIONS
    public void estimateState(){
        // Update state of squads
        for (Villager i : villagers) i.estimateState();

        int dead = 0, counted = 0, retreated = 0;
        // All_dead
        for (Villager i : villagers) {
            if (i.getState() == 0) dead ++;
        }
        if (dead == villagers.size()) {
            state = 0;
            return;
        }

        // Looting
        for (SquadVikings i : enemies) {
            for (Viking j : i.getVikings()) {
                if (distanceC(target.getLocation().x, j.getCurrentLocation().x, target.getLocation().y, j.getCurrentLocation().y) < map.numCols / 25) counted++;
            }
        }
        if (counted < 2) {
            state = 3;
            return;
        }

        // Retreated
        for (Villager i : villagers){
            if (i.getState() == 4) retreated ++;
        }
        if (retreated == villagers.size() && target.getLoot() != 0){
            state = 2;
            return;
        }

        // Else figth
        state = 1;
        return;
    }


    public void action() {                                  //toDo action functions
    }

    public void celebrate() {
    }

    public void surrender() {
    }

    // Updates
    public void updateTargetLocation(){
        boolean found = false;
        if (target.getLoot() == 0){
            for (Building i : village.getBuildings()){
                if (i.getLoot() != 0){
                    target = i;
                    for (Villager j:villagers) j.setTargetLocation(i);
                    found = true;
                }
                if (found) break;
            }
        }
    }

    // Drawing
    public void draw(Graphics g) {
        for( Villager i : villagers) i.draw(g);
    }
}
