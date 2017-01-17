package Map;

import Schemes.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Village {
    private ArrayList<Building> buildings;
    private Point center;

    // Constructor
    public Village(Terrain map) {
        // Variables for generating
        Random r = new Random();
        int size = map.numCols /30;
        int border = map.numCols /40;
        int max1 = map.numRows /17;
        int min1 = map.numRows /27;
        int max2 = map.numCols /20;
        int min2 = map.numCols /25;
        boolean inBound;

        //Initializing
        buildings = new ArrayList<>();
        center = new Point(0,0);

        // Genereating village
        for (int i = 0; i < map.numRows; i++) {
            for (int j = 0; j < map.numCols; j++) {
                if (map.getTerrainGrid()[i][j] == Colors.CITY && i > border && j > border) {
                    inBound = true;
                    for (int x = i - border; x < i + border + size; x++) {
                        for (int y = j - border; y < j + border + size; y++) {
                            if (map.getTerrainGrid()[x][y] != Colors.CITY) inBound = false;
                        }
                    }
                    if (inBound) {
                        buildings.add(new Building(new Point(i,j), size, size, r.nextInt(6-3+1)+3));
                        i += r.nextInt(max1 - min1 + 1) + min1;
                        j += r.nextInt(max2 - min2 + 1) + min2;
                    }
                }
            }
        }

        // Generating center
        for (int i = 0; i < buildings.size(); i ++){
            center.x += buildings.get(i).getLocation().x + size/2;
            center.y += buildings.get(i).getLocation().y + size/2;
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