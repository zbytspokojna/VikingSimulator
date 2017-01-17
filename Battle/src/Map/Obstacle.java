package Map;

import Schemes.Colors;

import java.awt.*;

// May not be implemented
public class Obstacle {

    private int x, y, width, height;

    public Obstacle (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g) {
        g.setColor(Colors.OBSTACLES);
        g.fillRect(x, y, width, height);

    }
}
