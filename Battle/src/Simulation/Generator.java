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
    private Fleet fleet;
    private Vikings vikings;
    private Villagers villagers;

    public Generator(int villageSize, Point vikingsSize, Point villagersSize){
        mapa = new Terrain(1000,1000,1000);
        village = new Village(mapa, villageSize);
        base = new Building(new Point(900, 900), 50, 50 ,0);
        fleet = new Fleet(mapa, village);
        vikings = new Vikings(mapa, village, fleet, base, vikingsSize);
        villagers = new Villagers(mapa, village, villagersSize);
        vikings.setEnemies(villagers.getSquads());
        villagers.setEnemies(vikings.getSquads());
    }

    public void draw(Graphics g){
        mapa.draw(g);
        village.draw(g);
        base.draw(g);
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
