package Simulation;

import javax.swing.*;
import java.awt.*;

/**
 * Created by anka on 15.01.17.
 */
public class Controller extends JPanel {
    private int Village;
    private Point Vikings = new Point();
    private Point Villagers = new Point();


    public int getVillage() {
        return Village;
    }

    public Point getVikings() {
        return Vikings;
    }

    public Point getVillagers() {
        return Villagers;
    }

    public void setVillage(int village) {
        Village = village;
    }

    public void setVikings(Point vikings) {
        Vikings = vikings;
    }

    public void setVillagers(Point villagers) {
        Villagers = villagers;
    }
}
