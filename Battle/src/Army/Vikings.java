package Army;

import Fleet.Fleet;
import Map.*;

import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.*;

public class Vikings {
    private ArrayList<SquadVikings> squads;
    private int state;                          //0-loss, 1-win, 2-fight

    private Terrain map;
    private Village village;
    private Fleet fleet;

    private ArrayList<SquadVillagers> enemies;

    public Vikings(Terrain map, Village village, Fleet fleet, Building base){
        // Variables for generating
        double angle = 2.2;
        double radius = 1.1 * sqrt((base.getWidth()*base.getWidth()) + base.getHeight()*base.getHeight());
        Point location = new Point();

        // Initializing
        this.squads = new ArrayList<>();
        this.state = 2;

        this.map = map;
        this.village = village;
        this.fleet = fleet;

        // Generating squads
        for (Building building:village.getBuildings()){
            angle += 0.5;
            location.x = base.getLocation().x + (int) (radius * cos(angle));
            location.y = base.getLocation().y + (int) (radius * sin(angle));
            squads.add(new SquadVikings(map, village, fleet, building, new Point(location), squads));
        }
    }

    // Setters
    public void setEnemies(ArrayList<SquadVillagers> enemies) {
        this.enemies = enemies;
        for (SquadVikings i:squads){
            i.setEnemies(enemies);
        }
    }

    // Getters
    public ArrayList<SquadVikings> getSquads() {
        return squads;
    }

    // OTHER FUNCTIONS
    public void estimateState(){
        // Update state of squads
        for (SquadVikings i : squads) i.estimateState();

        int lost = 0, retreated = 0, looted = 0, defeated = 0;
        // Losing statement
        for (SquadVikings i : squads) {
            if (i.getState() == 0) lost ++;
            if (i.getState() == 2) retreated++;
        }
        if (lost == squads.size()) {
            state = 0;
            return;
        }

        // Fight or loss
        if (retreated != 0 && lost + retreated == squads.size()){
            int size = 0, alive = 0;
            for (SquadVikings i : squads) {
                size += i.getSize();
                if (i.getState() == 2) {
                    for (Viking j : i.getVikings()) {
                        if (j.getState() != 0) alive++;
                    }
                }
            }
            if (alive < size/2){
                state = 0;
                return;
            }
            // TODO: 16.01.17 make else to set that after regrouping they attack
        }

        // Winning statement
        for (Building i : village.getBuildings()) if (i.getLoot() == 0) looted ++;
        // todo check if vikings are away from village
        for (SquadVillagers i : enemies) if (i.getState() == 0) defeated ++;
        if (looted == village.getBuildings().size() || defeated == enemies.size()) {
            state = 1;
            return;
        }

        // Else figth
        state = 2;
        return;
    }

    // Actions based on state
    public void action() {
        estimateState();
        switch (state){
            case 0 : fleet.returnToBase();
                break;
            case 1 : fleet.returnToBase();
                for (SquadVikings i : squads) i.setState(2);
                break;
            case 2 :
                break;
        }
        fleet.action();
        for (SquadVikings i : squads) i.action();
    }

    // Drawing
    public void draw(Graphics g){
        for (SquadVikings i:squads) i.draw(g);
    }

    public int getState() {
        return state;
    }
}
