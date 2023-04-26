package edu.utd.graphics.shape;

import java.awt.*;

public class Circle extends Shape {
    public Circle(int startX, int startY) {
        super(startX, startY);
    }

    @Override
    public void draw(Graphics g) {
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int diameter = Math.max(Math.abs(startX - endX), Math.abs(startY - endY));


        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setStroke(getStroke());
        }

        g.setColor(getColor());
        g.drawOval(x, y, diameter, diameter);
    }
}
