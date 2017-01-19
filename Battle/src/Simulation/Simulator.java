package Simulation;

import javax.swing.*;
import java.awt.*;

public class Simulator extends JPanel {
    JPanel simulator;

    private Generator generator;
    private Stats stats;
    private int rows, cols;
    private boolean state;
    private int queue;

    public Simulator(int rows, int cols, int seeds){
        this.rows = rows;
        this.cols = cols;
        this.state = false;
        this.simulator = new JPanel();
        this.generator = new Generator(rows,cols,seeds);
        this.stats = new Stats(generator);
        this.queue = 0;
    }

    public void simulation(){
        if (state) {
            if (queue == 0) {
                generator.getVikings().action();
                generator.getVillagers().action();
                queue = 1;
            }
            else {
                generator.getVillagers().action();
                generator.getVikings().action();
                queue = 0;
            }
                stats.estimate();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // Important to call super class method
        super.paintComponent(g);
        generator.draw(g);
        stats.draw(g);
        repaint();
    }

    // Getters and setters
    public Generator getGenerator() {
        return generator;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}