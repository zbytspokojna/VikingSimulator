package Simulation;

import javax.swing.*;
import java.awt.*;

public class Simulator extends JPanel {

    private Generator generator;
    private Stats stats;
    private boolean state;
    private int queue;

    public Simulator(int villageSize, Point vikingsSize, Point villagersSize){
        this.state = false;
        this.queue = 0;

        this.generator = new Generator(villageSize,vikingsSize,villagersSize);
        this.stats = new Stats(generator);
    }

    public void simulation(){
        if (state) {
            if (queue == 0) {
                generator.getVikings().action();
                generator.getVillagers().action();
                stats.estimate();
                queue = 1;
            }
            else {
                generator.getVillagers().action();
                generator.getVikings().action();
                stats.estimate();
                queue = 0;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        generator.draw(g);
        stats.draw(g);
        repaint();
    }

    // Getters and setters
    public void setState(boolean state) {
        this.state = state;
    }
    public boolean getState() {
        return state;
    }
}