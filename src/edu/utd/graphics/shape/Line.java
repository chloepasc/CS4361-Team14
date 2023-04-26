package edu.utd.graphics.shape;

import java.awt.*;

public class Line extends Shape {

    public Line(int startX, int startY) {
        super(startX, startY);
    }

    @Override
    public void draw(Graphics g) {
        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setStroke(getStroke());
        }

        g.setColor(getColor());
        g.drawLine(startX, startY, endX, endY);
    }
}
