package Simulation;

import Army.Vikings;
import Army.Villagers;
import Fleet.Fleet;
import Map.*;

import java.awt.*;

/**
 * Created by anka on 10.01.17.
 */
public class Generator {
    private Terrain mapa;
    private Village village;
    private Building base;
//    public ArrayList<Obstacle> obstacles;
    private Fleet fleet;
    private Vikings vikings;
    private Villagers villagers;

    public Generator(int rows, int cols, int seeds){
        mapa = new Terrain(rows,cols,seeds);
        village = new Village(mapa);
        base = new Building(new Point((int)(rows*0.9), (int)(cols*0.9)),rows/20,cols/20,0);
//        obstacles = new ArrayList<Obstacle>();
        fleet = new Fleet(mapa, village);
        vikings = new Vikings(mapa, village, fleet, base);
        villagers = new Villagers(mapa, village);
        vikings.setEnemies(villagers.getSquads());
        villagers.setEnemies(vikings.getSquads());
    }

    public void draw(Graphics g){
        mapa.draw(g);
        village.draw(g);
        base.draw(g);
//        obstacles.draw(g);
        fleet.draw(g);
        vikings.draw(g);
        villagers.draw(g);
    }

    public Fleet getFleet() {
        return fleet;
    }

    public Vikings getVikings() {
        return vikings;
    }

    public Villagers getVillagers() {
        return villagers;
    }

    public Village getVillage() {
        return village;
    }
}
