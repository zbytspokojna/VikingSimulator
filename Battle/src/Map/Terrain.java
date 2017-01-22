package Map;

import Schemes.Colors;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import static Colision.Distance.*;

public class Terrain {
    // Number of seeds and size
    private int seeds;
    public int numRows;
    public int numCols;

    // Grid of terrain
    private Color[][] terrainGrid;
    private ArrayList<Point> coastH;
    private ArrayList<Point> coastP;

    // Constructor
    public Terrain(int rows, int cols, int seed){
        // Initializing
        this.numRows = rows;
        this.numCols = cols;
        this.seeds = seed;
        this.terrainGrid = new Color[numRows][numCols];
        this.coastH = new ArrayList<>();
        this.coastP = new ArrayList<>();

        // Local variables
        Random r = new Random();
        // List of seeds
        int[] px = new int[seeds];
        int[] py = new int[seeds];
        // Color for seeds
        Color[] color = new Color[seeds];

        double radiusOfVillage = numCols*0.16;
        double radiusOfVikingIsland = numCols*0.33;
        double radiusOfVillagersIsland = numCols*0.8;
        double radiusOfForest = numCols*0.1;

        // Generate seeds
        for (int i = 0; i < seeds; i++) {
            // Random seed location
            px[i] = r.nextInt(numRows);
            py[i] = r.nextInt(numCols);

            // Forest
            if ((px[i] < numRows/35 && py[i] < numRows/3) || (px[i] < numRows/3 && py[i] < numRows/35) || distanceC(0, px[i], 0, py[i]) < radiusOfForest)
                color[i] = Colors.FOREST;
            // City
            else if (distanceC(numRows*0.25, px[i], numCols*0.25, py[i]) < radiusOfVillage)
                color[i] = Colors.CITY;
            // Viking island
            else if ((distanceC(numRows, px[i], numCols, py[i]) < radiusOfVikingIsland))
                color[i] = Colors.HILLS;
            // Main island
            else if (distanceC(0, px[i], 0, py[i]) < radiusOfVillagersIsland)
                color[i] = Colors.PLAINS;
            // Ocean
            else color[i] = Colors.OCEAN;
        }

        // Generate terrain
        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numCols; y++) {
                 int n = 0;
                for (int i = 0; i < seeds; i++) {
                    if (distanceV(px[i], x, py[i], y) < distanceV(px[n], x, py[n], y)) n = i;
                }
                terrainGrid[x][y] = color[n];
            }
        }

        // Generating coasts
        for (int i = 1; i < numRows-1; i++) {
            for (int j = 1; j < numCols-1; j++) {
                // CoastH
                if (terrainGrid[i][j] == Colors.HILLS)
                    if (checkForOcean(i,j))
                        coastH.add(new Point(i, j));
                // CoastP
                if (terrainGrid[i][j] == Colors.PLAINS)
                    if (checkForOcean(i,j))
                        coastP.add(new Point(i, j));
            }
        }
    }

    private boolean checkForOcean(int i, int j){
        return ((terrainGrid[i - 1][j] == Colors.OCEAN || terrainGrid[i][j - 1] == Colors.OCEAN) || (terrainGrid[i + 1][j] == Colors.OCEAN || terrainGrid[i][j + 1] == Colors.OCEAN));
    }

    // Drawing
    public void draw(Graphics g) {
        // Landscape
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                Color terrainColor = terrainGrid[i][j];
                g.setColor(terrainColor);
                g.fillRect(i, j, 1, 1);
            }
        }
        // Coasts
        g.setColor(Color.MAGENTA);
        for (Point i:coastH) g.fillRect(i.x, i.y, 1, 1);
        g.setColor(Color.ORANGE);
        for (Point i:coastP) g.fillRect(i.x, i.y, 1, 1);
    }

    // Getters
    public Color[][] getTerrainGrid() {
        return terrainGrid;
    }
    public ArrayList<Point> getCoastH() {
        return coastH;
    }
    public ArrayList<Point> getCoastP() {
        return coastP;
    }
}