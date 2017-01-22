package Fleet;

import Map.Building;

import java.awt.*;

public class Target {
    private Point target;
    private Building building;
    private boolean used;

    public Target(Point target, Building building){
        this.target = new Point(target);
        this.building = building;
        this.used = false;
    }

    public void use(){
        this.used = true;
    }

    // Drawing
    public void draw(Graphics g){
        g.setColor(Color.RED);
        g.fillRect(target.x, target.y, 3, 3);
    }

    // Getters
    public Point getTarget() {
        return target;
    }
    public Building getBuilding() {
        return building;
    }
}
