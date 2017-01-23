package Army;

import Map.*;
import Schemes.States;

import java.awt.*;
import java.util.ArrayList;

public class Villagers {
    private ArrayList<SquadVillagers> squads;
    private int state;

    private Village village;
    private Terrain map;

    private ArrayList<SquadVikings> enemies;

    public Villagers(Terrain map, Village village, Point minMax){
        // Initializing
        this.squads = new ArrayList<>();
        this.state = States.FIGHT;

        this.village = village;
        this.map = map;

        // Generating squads
        for (Building i:village.getBuildings()){
            squads.add(new SquadVillagers(map, village, i, squads, minMax));
        }
    }

    // Setters
    public void setEnemies(ArrayList<SquadVikings> enemies) {
        this.enemies = enemies;
        for (SquadVillagers i:squads){
            i.setEnemies(enemies);
        }
    }

    // Getters
    public ArrayList<SquadVillagers> getSquads() {
        return squads;
    }

    public int getState() {
        return state;
    }


    // OTHER FUNCTIONS
    public void estimateState(){
        // Update state of squads
        for (SquadVillagers i : squads)
            i.estimateState();

        int lost = 0, retreated = 0, defeated = 0, looted = 0;

        // Losing statement
        for (Building i : village.getBuildings())
            if (i.getLoot() == 0)
                looted ++;

        for (SquadVillagers i : squads) {
            if (i.getState() == States.DEAD) lost ++;
            if (i.getState() == States.RETREAT) retreated++;
        }

        if (lost == squads.size() || looted == village.getBuildings().size()) {
            state = States.LOSS;
            return;
        }

        // Regruping or retreating
        if (retreated != 0 && lost + retreated == squads.size()){
            int size = 0, alive = 0, alive2 = 0, inForest = 0;
            for (SquadVillagers i : squads) {
                size += i.getSize();
                if (i.getState() == States.RETREAT)
                    for (Villager j : i.getVillagers())
                        if (j.getState() != States.DEAD) alive++;
            }
            for (SquadVikings i : enemies)
                for (Viking j : i.getVikings())
                    if (j.getState() != States.DEAD)
                        alive2++;

            // Reattack
            if (alive >= size/2 || alive2 <= alive) {
                state = States.FIGHT;
                for (SquadVillagers i : squads)
                    i.setReAttack();
                return;
            }
            // Loss
            else {
                state = States.LOSS;
                return;
            }
        }

        // Winning statement
        for (SquadVikings i : enemies)
            if (i.getState() == States.DEAD || i.getState() == States.LOSS)
                defeated ++;
        if (defeated == enemies.size()) {
            state = States.WIN;
            return;
        }

        // Else figth
        state = States.FIGHT;
    }

    // Actions based on state
    public void action(){
        estimateState();

        switch (state){
            case States.LOSS :
                for (SquadVillagers i : squads) i.setLoss();
                break;
            case States.WIN :
                for (SquadVillagers i : squads) i.setWin();
                break;
            case States.FIGHT :
                break;
        }

        for (SquadVillagers i : squads) {
            i.action();
        }
    }

    // Drawing
    public void draw(Graphics g){
        for (SquadVillagers i:squads) i.draw(g);
    }
}
