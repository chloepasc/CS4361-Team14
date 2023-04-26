package edu.utd.graphics.shape;

import java.awt.*;

public class Rectangle extends Shape {

    public Rectangle(int startX, int startY) {
        super(startX, startY);
    }

    @Override
    public void draw(Graphics g) {
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        int width = Math.abs(startX - endX);
        int height = Math.abs(startY - endY);

        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setStroke(getStroke());
        }

        g.setColor(getColor());
        g.drawRect(x, y, width, height);
    }
}
