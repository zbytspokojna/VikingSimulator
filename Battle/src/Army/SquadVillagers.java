package Army;

import Map.Building;
import Map.Terrain;
import Map.Village;
import Schemes.Colors;
import Schemes.States;

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

    public SquadVillagers(Terrain map, Village village, Building target, ArrayList<SquadVillagers> allies, Point minMax){
        // Variables for generation
        Random r = new Random();
        boolean generated, noColision;
        Point location = new Point();
        Color color;
        int size = map.numCols/120;
        double radius = sqrt((target.getWidth()*target.getWidth()) + target.getHeight()*target.getHeight())/2 + size;

        // Stats for squad
        this.size = r.nextInt(minMax.y - minMax.x + 1) + minMax.x;
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

    public void setReAttack() {
        if (state != States.DEAD) state = States.FIGHT;
        for (Villager i : villagers)
            i.setReAttack();
    }

    public void setLoss() {
        if (state != States.DEAD) state = States.LOSS;
        for (Villager i : villagers)
            i.setLoss();
    }

    public void setWin() {
        if (state != States.DEAD) state = States.WIN;
        for (Villager i : villagers)
            i.setWin();
    }

    // Getters
    public int getState() {
        return state;
    }

    public ArrayList<Villager> getVillagers() {
        return villagers;
    }

    public int getSize() {
        return size;
    }

    // OTHER FUNTIONS
    public void estimateState(){
        // Update state of squads
        for (Villager i : villagers)
            i.estimateState();
        int dead = 0, retreated = 0, fighting = 0;
        switch (state){
            case States.DEAD:
                break;
            case States.FIGHT:
                for (Villager i : villagers) {
                    if (i.getState() == States.RETREAT) retreated++;
                    if (i.getState() == States.DEAD) dead ++;
                }
                if (dead == villagers.size()) state = States.DEAD;
                if (retreated != 0 && (dead + retreated) == villagers.size()) state = States.RETREAT;
                break;
            case States.RETREAT:
                for (Villager i : villagers) {
                    if (i.getState() == States.FIGHT)
                        fighting++;
                }
                if (fighting > 0) state = States.FIGHT;
                break;
            case States.LOSS:
                break;
            case States.WIN:
                break;
        }
    }

    // Updates
    public void updateTargetLocation(){
        boolean found = false;
        if (target.getLoot() == 0){
            for (Building i : village.getBuildings()){
                if (i.getLoot() != 0){
                    target = i;
                    for (Villager j:villagers) {
                        j.setFighting();
                        j.setTargetBuilding(i);
                    }
                    found = true;
                }
                if (found) break;
            }
        }
    }

    public void action() {
        updateTargetLocation();
        for (Villager i : villagers) i.action();
    }

    // Drawing
    public void draw(Graphics g) {
        for( Villager i : villagers) i.draw(g);
    }
}
