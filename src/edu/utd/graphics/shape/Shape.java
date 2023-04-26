package edu.utd.graphics.shape;

import java.awt.*;

public abstract class Shape {
    public int startX, startY, endX, endY;
    private Color color = Color.BLACK; // default color
    private Stroke stroke = new BasicStroke(1); // default stroke

    public Shape(int startX, int startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        if (stroke != null) {
            this.stroke = stroke;
        }
    }

    public abstract void draw(Graphics g);
}
