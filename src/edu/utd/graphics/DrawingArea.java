package edu.utd.graphics;

import edu.utd.graphics.shape.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DrawingArea extends Canvas {

    private final List<Shape> shapes = new ArrayList<>();

    private Shape currentShape;

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (currentShape != null)
            currentShape.draw(g);

        for (Shape shape : shapes) {
            shape.draw(g);
        }
    }

    public void draw(Shape shape) {
        currentShape = shape;
        repaint();
    }

    public void save(Shape shape) {
        currentShape = null;
        shapes.add(shape);
        repaint();
    }
}
