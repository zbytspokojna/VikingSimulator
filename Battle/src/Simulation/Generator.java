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
    private Terrain map;
    private Village village;
    private Building base;
    private Fleet fleet;
    private Vikings vikings;
    private Villagers villagers;

    public Generator(int villageSize, Point vikingsSize, Point villagersSize){
        map = new Terrain(1000,1000,1000);
        village = new Village(map, villageSize);
        base = new Building(new Point(900, 900), 50, 50 ,0);
        fleet = new Fleet(map, village, vikingsSize);
        vikings = new Vikings(map, village, fleet, base, vikingsSize);
        villagers = new Villagers(map, village, villagersSize);
        vikings.setEnemies(villagers.getSquads());
        villagers.setEnemies(vikings.getSquads());
    }

    // Drawing
    public void draw(Graphics g){
        map.draw(g);
        village.draw(g);
        base.draw(g);
        fleet.draw(g);
        vikings.draw(g);
        villagers.draw(g);
    }

    // Getters
    public Vikings getVikings() {
        return vikings;
    }
    public Villagers getVillagers() {
        return villagers;
    }
    public Village getVillage() {
        return village;
    }
    public Terrain getMap() {
        return map;
    }
}
