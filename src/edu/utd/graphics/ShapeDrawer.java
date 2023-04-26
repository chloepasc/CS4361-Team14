package edu.utd.graphics;

import edu.utd.graphics.shape.Circle;
import edu.utd.graphics.shape.Line;
import edu.utd.graphics.shape.Rectangle;
import edu.utd.graphics.shape.Shape;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

public class ShapeDrawer extends JFrame {

    Logger logger = Logger.getLogger(ShapeDrawer.class.getName());

    private static final String[] SHAPES = {"Line", "Rectangle", "Circle"};
    private static final Color[] COLORS = {Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};

    private String selectedShape = "Line";
    private Color selectedColor = Color.BLACK;

    private DrawingArea drawingArea;

    public ShapeDrawer() {
        setTitle("Drawer");

        JComboBox<String> shapes = new JComboBox<>(SHAPES);
        shapes.addActionListener(evt -> this.selectedShape = ((JComboBox) evt.getSource()).getSelectedItem().toString());

        JComboBox<Color> colors = new JComboBox<>(COLORS);
        // just for demo, use color picker
        colors.setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Color val = (Color) value;
                setForeground((Color) value);
                setText(val.toString());
                return this;
            }
        });
        colors.addActionListener(evt -> this.selectedColor = (Color) ((JComboBox) evt.getSource()).getSelectedItem());

        Panel choicePanel = new Panel();
        choicePanel.add(shapes);
        choicePanel.add(colors);

        add(choicePanel, BorderLayout.NORTH);

        drawingArea = new DrawingArea();
        drawingArea.setBackground(Color.WHITE);

        MouseListener listener = new MouseListener();
        drawingArea.addMouseListener(listener);
        drawingArea.addMouseMotionListener(listener);

        add(drawingArea, BorderLayout.CENTER);

        setSize(500, 500);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    class MouseListener extends MouseAdapter {
        private int startX;
        private int startY;
        private int endX;
        private int endY;

        private Shape currentShape;

        @Override
        public void mousePressed(MouseEvent e) {
            logger.info("Selected shape: " + selectedShape);
            startX = e.getX();
            startY = e.getY();
            switch (selectedShape) {
                case "Rectangle":
                    currentShape = new Rectangle(startX, startY);
                    break;
                case "Circle":
                    currentShape = new Circle(startX, startY);
                    break;
                default:
                    currentShape = new Line(startX, startY);
                    break;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            endX = e.getX();
            endY = e.getY();
            updateShape();
            drawingArea.draw(currentShape);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            endX = e.getX();
            endY = e.getY();
            updateShape();
            drawingArea.save(currentShape);
        }

        private void updateShape() {
            currentShape.endX = endX;
            currentShape.endY = endY;
            currentShape.setColor(selectedColor);
        }
    }

    // main method
    public static void main(String[] args) {
        new ShapeDrawer();
    }
}
