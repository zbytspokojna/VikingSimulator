package Map;

import Schemes.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static Colision.Distance.distanceC;

public class Village {
    private ArrayList<Building> buildings;
    private Point center;

    // Constructor
    public Village(Terrain map, int villageSize) {
        // Variables for generating
        Random r = new Random();
        int size = map.numCols /30;
        int border = map.numCols /30;
        boolean inBound;
        int generated = 0;

        //Initializing
        buildings = new ArrayList<>();
        center = new Point(0,0);

        // Genereating village
        while (generated != villageSize){
            int x = r.nextInt(map.numCols/2);
            int y = r.nextInt(map.numCols/2);
            if (map.getTerrainGrid()[x][y] == Colors.CITY) {
                inBound = true;
                // check if in borders of city
                int tx,ty;
                double angle2 = 0;
                while (angle2 < 6.3 && inBound) {
                    tx = x + (int) (border * cos(angle2));
                    ty = y + (int) (border * sin(angle2));
                    if(tx > 0 && ty > 0 && tx < map.numRows && ty < map.numCols)
                        if (map.getTerrainGrid()[tx][ty] != Colors.CITY)
                            inBound = false;
                    angle2 += 0.3;
                }
                // check if not on other buildings
                if (inBound)
                    for (Building building : buildings){
                        double radius = sqrt((building.getWidth()*building.getWidth()) + building.getHeight()*building.getHeight());
                        double spread = 1.5;
                        if (distanceC(x, building.getLocation().x, y, building.getLocation().y) < radius*spread)
                            inBound = false;
                    }
                if (inBound) {
                    buildings.add(new Building(new Point(x, y), size, size, r.nextInt(4) + 3));
                    generated++;
                }
            }
        }

        // Generating center
        for (Building building : buildings) {
            center.x += building.getLocation().x + size / 2;
            center.y += building.getLocation().y + size / 2;
        }
        center.x = center.x/buildings.size();
        center.y = center.y/buildings.size();
    }

    // Drawing
    public void draw(Graphics g) {
        for (Building i:buildings) i.draw(g);
        g.setColor(Color.BLUE);
        g.drawOval(center.x - 3, center.y - 3, 6, 6);
        g.fillOval(center.x, center.y, 1, 1);
    }

    public ArrayList<Building> getBuildings() {
        return buildings;
    }

    public Point getCenter() {
        return center;
    }
}