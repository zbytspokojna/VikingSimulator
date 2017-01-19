package Army;

import Fleet.Fleet;
import Map.*;
import Schemes.Colors;
import Schemes.States;

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
    private int state;

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
        this.state = States.FIGHT;

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

    public void setLoss() {
        if (state != States.DEAD) state = States.LOSS;
        for (Viking i : vikings)
            i.setLoss();
    }

    public void setWin() {
        if (state != States.DEAD) state = States.WIN;
        for (Viking i : vikings)
            i.setWin();
    }

    public void setReAttack() {
        if (state != States.DEAD) state = States.FIGHT;
        for (Viking i : vikings)
            i.setReAttack();
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

    // State
    public void estimateState(){
        // Update state of squads
        for (Viking i : vikings) {
            i.estimateState();
            //System.out.print(i.getState());
        }

        int dead = 0, retreated = 0, fighting = 0;
        switch (state){
            case States.DEAD:
                break;
            case States.FIGHT:
                for (Viking i : vikings) {
                    if (i.getState() == States.RETREAT || (i.getState() == States.INBOAT && i.getCurrentLocation() == i.getTargetBoat().getTargetLocation())) retreated++;
                    if (i.getState() == States.DEAD) dead ++;
                }
                if (dead == vikings.size()) state = States.DEAD;
                if (retreated != 0 && (dead + retreated) == vikings.size()) state = States.RETREAT;
                break;
            case States.RETREAT:
                for (Viking i : vikings) {
                    if (i.getState() == States.FIGHT || i.getState() == States.LOOTING)
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
                    for (Viking j:vikings) j.setTargetBuilding(i);
                    found = true;
                }
                if (found) break;
            }
        }
    }

    // // TODO: 18.01.17 make WIN case
    public void action() {
        updateTargetLocation();
        int looting = 0, counted = 0;
        double radius = sqrt((target.getWidth()*target.getWidth()) + target.getHeight()*target.getHeight());

        switch (state){
            case States.DEAD:
                break;
            case States.LOSS:
                break;
            case States.RETREAT:
                break;
            case States.FIGHT:
                // If building has loot
                if (target.getLoot() > 0) {
                    for (SquadVillagers i : enemies)
                        for (Villager j : i.getVillagers()) {
                            if (j.getHealth() > 0)
                                // If enemy is alive and in range
                                if (distanceC(target.getLocation().x, j.getCurrentLocation().x, target.getLocation().y, j.getCurrentLocation().y) < radius)
                                    counted++;
                        }
                    // If found less then 2 enemies set some vikings to loot
                    if (counted == 0) {
                        for (Viking i : vikings){
                            if (i.getState() != States.DEAD && i.getState() != States.INBOAT && i.getState() != States.RETREAT && i.getLoot() < 2){
                                i.setLooting();
                                looting++;
                            }
                            if (looting > 2) break;
                        }
                    }
                }
                break;
            case States.WIN:
                // TODO: 18.01.17 so they loot what is left
        }
        if (state == States.LOOTING) {
            for (Viking i : vikings) {
                if (i.getLoot() == 2)
                    i.unsetLooting();
                else
                    if (i.setLooting()) looting++;
                if (looting > 3) break;
            }
        }

        for (Viking i : vikings) i.action();
    }


    // Drawing
    public void draw(Graphics g) {
        for (Viking i: vikings) i.draw(g);
    }
}
