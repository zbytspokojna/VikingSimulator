package Army;

import Fleet.Fleet;
import Map.*;
import Schemes.States;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.*;

public class Vikings {
    private ArrayList<SquadVikings> squads;
    private int state;

    private Terrain map;
    private Village village;
    private Fleet fleet;
    private Building base;

    private ArrayList<SquadVillagers> enemies;

    public Vikings(Terrain map, Village village, Fleet fleet, Building base, Point minMax){
        // Variables for generating
        double angle = 2.2;
        double radius = 1.1 * sqrt((base.getWidth()*base.getWidth()) + base.getHeight()*base.getHeight());
        Point location = new Point();

        // Initializing
        this.squads = new ArrayList<>();
        this.state = States.FIGHT;

        this.map = map;
        this.village = village;
        this.fleet = fleet;
        this.base = base;

        // Generating squads
        for (Building building:village.getBuildings()){
            angle += 0.5;
            location.x = base.getLocation().x + (int) (radius * cos(angle));
            location.y = base.getLocation().y + (int) (radius * sin(angle));
            squads.add(new SquadVikings(map, village, fleet, building, new Point(location), base, squads, minMax));
        }

        double loot = 0, size = 0;
        int maxLoot;
        for (Building building : village.getBuildings()){
            loot += building.getLoot();
        }
        for (SquadVikings squadVikings : squads)
            for (Viking viking : squadVikings.getVikings())
                size ++;

        maxLoot = (int) ceil(loot/size) + 1;
        setMaxLoot(maxLoot);
    }

    // Setters
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
        for (SquadVikings i:squads){
            i.setEnemies(enemies);
        }
    }

    public void setMaxLoot(int maxLoot){
        for (SquadVikings i : squads)
            i.setMaxLoot(maxLoot);
    }

    // Getters
    public ArrayList<SquadVikings> getSquads() {
        return squads;
    }

    public int getState() {
        return state;
    }


    // OTHER FUNCTIONS
    public void estimateState(){
        // Update state of squads
        for (SquadVikings i : squads)
            i.estimateState();

        int lost = 0, retreated = 0, looted = 0, defeated = 0;

        // Losing statement
        for (SquadVikings i : squads) {
            if (i.getState() == States.DEAD) lost ++;
            if (i.getState() == States.RETREAT) retreated++;
        }
        if (lost == squads.size()) {
            state = States.LOSS;
            return;
        }

        // Regruping or retreating
        if (retreated != 0 && lost + retreated == squads.size()){
            int size = 0, alive = 0, inBoat = 0;
            for (SquadVikings i : squads) {
                size += i.getSize();
                if (i.getState() == States.RETREAT) {
                    for (Viking j : i.getVikings()) {
                        if (j.getState() != States.DEAD) alive++;
                        if (j.getInBoat()) inBoat++;
                    }
                }
            }
            if (inBoat == alive)
                if (alive < size/2){
                    state = States.LOSS;
                    return;
                }
                else {
                    state = States.FIGHT;
                    for (SquadVikings i : squads)
                        i.setReAttack();
                    return;
                }
        }

        // Winning statement
        for (Building i : village.getBuildings()) if (i.getLoot() == 0) looted ++;
        for (SquadVillagers i : enemies) if (i.getState() == States.DEAD) defeated ++;
        if (looted == village.getBuildings().size()) {
            state = States.WIN;
            return;
        }
        if (defeated == enemies.size() && looted != village.getBuildings().size()) {
            System.out.println("Get the rest of the loot");
            state = States.FIGHT;
            for (SquadVikings i : squads)
                i.setReAttack();
            return;
        }
        if (defeated == enemies.size() && looted == village.getBuildings().size()) {
            state = States.WIN;
            return;
        }


        // Else figth
        state = States.FIGHT;
    }

    // Actions based on state
    public void action() {
        estimateState();
        fleet.estimateState();

        switch (state){
            case States.LOSS :
                fleet.returnToBase();
                for (SquadVikings i : squads) i.setLoss();
                break;
            case States.WIN :
                fleet.returnToBase();
                for (SquadVikings i : squads) i.setWin();
                break;
            case States.FIGHT :

                break;
        }

        fleet.action();
        for (SquadVikings i : squads) {
            i.action();
        }
    }

    // Drawing
    public void draw(Graphics g){
        for (SquadVikings i:squads) i.draw(g);
    }

}
