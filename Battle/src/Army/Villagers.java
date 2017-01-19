package Army;

import Map.*;
import Schemes.States;

import java.awt.*;
import java.util.ArrayList;

public class Villagers {
    private ArrayList<SquadVillagers> squads;
    private int state;  //0-loss, 1-win, 2-fight

    private Village village;
    private Terrain map;

    private ArrayList<SquadVikings> enemies;

    public Villagers(Terrain map, Village village){
        // Initializing
        this.squads = new ArrayList<>();
        this.state = States.FIGHT;

        this.village = village;
        this.map = map;

        // Generating squads
        for (Building i:village.getBuildings()){
            squads.add(new SquadVillagers(map, village, i, squads));
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

    // OTHER FUNCTIONS
    public void estimateState(){
        // Update state of squads
        for (SquadVillagers i : squads) i.estimateState();

        int lost = 0, looted = 0, defeated = 0;
        // Losing statement
        for (SquadVillagers i : squads) {
            if (i.getState() == 0) lost ++;
        }
        for (Building i : village.getBuildings()) {
            if (i.getLoot() == 0) looted ++;
        }
        if (lost == squads.size() || looted == village.getBuildings().size()) {
            state = States.LOSS;
            return;
        }

        // Winning statement
        for (SquadVikings i : enemies) {
            if (i.getState() == 0 || i.getState() == 2) defeated ++;
        }
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
        for (SquadVillagers i : squads) {
            if (state == States.FIGHT) i.action();
        }
    }

    // Drawing
    public void draw(Graphics g){
        for (SquadVillagers i:squads) i.draw(g);
    }

    public int getState() {
        return state;
    }
}
