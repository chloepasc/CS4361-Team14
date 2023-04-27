import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Canvas extends JPanel{

    private BufferedImage originalImage;
    private BufferedImage canvasImage;

    //TOOL VARIABLES
    private int activeTool;
    //public static final int SELECTION_TOOL = 1;
    public static final int DRAW_TOOL = 0;
    public static final int FILL_TOOL = 1;
    public static final int ERASE_TOOL = 2;
    public static final int SHAPE_TOOL = 3;
    private Point start = (null);
    private Point end = (null);
    private Rectangle rect = null;
    private Ellipse2D.Double circle = null;
    private ArrayList<Point> points = new ArrayList<Point>();
    private ArrayList<Point> erasePoints = new ArrayList<Point>();
    private static final String[] SHAPES = {"Line", "Rectangle", "Circle"};
    private String selectedShape = "Line";
    private Stroke drawStroke = new BasicStroke(
        3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    private Stroke eraseStroke = new BasicStroke(
        3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    private Stroke shapeStroke = new BasicStroke(
        3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    private RenderingHints renderingHints;
  //  private Shape currentShape;
  //  private DrawingArea drawingArea;

    //GENERAL GUI VARIABLES
    private JPanel gui;
    private JPanel imageView;
    private Color color = Color.RED;
    private Color bgColor = Color.WHITE;
    private JLabel output = new JLabel("Drawing App");
    private BufferedImage colorSample = new BufferedImage(
            16,16,BufferedImage.TYPE_INT_RGB);
    private JLabel imageLabel;
    //int canvasWidth, canvasHeight;
    private boolean dirty = false;

    public JComponent getGui() {

        if (gui==null) {
            Map<Key, Object> hintsMap = new HashMap<RenderingHints.Key,Object>();
            hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            hintsMap.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            hintsMap.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY); //transparency
            renderingHints = new RenderingHints(hintsMap); 

            gui = new JPanel(new BorderLayout(4,4));
            gui.setBorder(new EmptyBorder(5,3,5,3));
            setImage(new BufferedImage(1250,900,BufferedImage.TYPE_INT_RGB));

            imageView = new JPanel(new GridBagLayout());
            imageView.setPreferredSize(new Dimension(1250,900));
            imageLabel = new JLabel(new ImageIcon(canvasImage));
            JScrollPane imageScroll = new JScrollPane(imageView);
            imageView.add(imageLabel);
            imageLabel.addMouseMotionListener(new ImageMouseMotionListener());
            imageLabel.addMouseListener(new ImageMouseListener());
            gui.add(imageScroll,BorderLayout.CENTER);

            JToolBar tb = new JToolBar();
            tb.setFloatable(false);

            JButton colorButton = new JButton();
            colorButton.setMnemonic('o');
            colorButton.setToolTipText("Choose a Color");
            ActionListener colorListener = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    Color c = JColorChooser.showDialog(
                            gui, "Choose a color", color);
                    if (c!=null) {
                        setColor(c);
                    }
                }
            };

            colorButton.addActionListener(colorListener);
            colorButton.setIcon(new ImageIcon(colorSample));
            tb.add(colorButton);
            setColor(color);

        
            ImageIcon fillIcon = new ImageIcon("fillIcon.png");
            final JRadioButton fill = new JRadioButton(fillIcon);
            tb.add(fill);

            ImageIcon drawIcon = new ImageIcon("drawIcon.png");
            final JRadioButton draw = new JRadioButton(drawIcon, true);
            tb.add(draw); 

            JSlider drawSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 25); //draw slider+spinner

            final SpinnerNumberModel strModel = new SpinnerNumberModel(25, 1, 100, 1);
            JSpinner drawSpinner = new JSpinner(strModel);
            drawSpinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    activeTool = DRAW_TOOL;
                    Object o = drawSpinner.getValue();
                    Integer i = (Integer)o; 

                    drawSlider.setValue((int)drawSpinner.getValue());
                    drawStroke = new BasicStroke(
                            i.intValue(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1.7f);
                }
            });
            tb.add(drawSpinner);
            tb.addSeparator();

            drawSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    activeTool = DRAW_TOOL;
                    Integer value = drawSlider.getValue();
                    drawSpinner.setValue((int) drawSlider.getValue());
                    drawStroke = new BasicStroke(
                        value.intValue(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1.7f);
                }
            });
            tb.add(drawSlider);

            ImageIcon eraseIcon = new ImageIcon("eraseIcon.png");
            final JRadioButton erase = new JRadioButton(eraseIcon);
            tb.add(erase);   

            JSlider eraseSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1); //draw slider+spinner

            final SpinnerNumberModel ersModel = new SpinnerNumberModel(1, 1, 100, 1);
            JSpinner eraseSpinner = new JSpinner(ersModel);
            eraseSpinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    activeTool = ERASE_TOOL;
                    Object o = eraseSpinner.getValue();
                    Integer i = (Integer)o; 

                    eraseSlider.setValue((int)eraseSpinner.getValue());
                    eraseStroke = new BasicStroke(
                            i.intValue(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1.7f);
                }
            });
            tb.add(eraseSpinner);
            tb.addSeparator();

            eraseSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    activeTool = ERASE_TOOL;
                    Integer value = eraseSlider.getValue();
                    eraseSpinner.setValue((int) eraseSlider.getValue());
                    eraseStroke = new BasicStroke(
                        value.intValue(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1.7f);
                }
            });
            tb.add(eraseSlider);

            JRadioButton shapeButton = new JRadioButton("Shapes");
            tb.add(shapeButton);
            JComboBox<String> shapes = new JComboBox<>(SHAPES);
            tb.add(shapes);
            shapes.addActionListener(evt -> this.selectedShape = ((JComboBox) evt.getSource()).getSelectedItem().toString());
            JSlider shapeSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1); //draw slider+spinner

            final SpinnerNumberModel spModel = new SpinnerNumberModel(1, 1, 100, 1);
            JSpinner shapeSpinner = new JSpinner(spModel);
            shapeSpinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    activeTool = SHAPE_TOOL;
                    Object o = shapeSpinner.getValue();
                    Integer i = (Integer)o; 

                    shapeSlider.setValue((int)shapeSpinner.getValue());
                    shapeStroke = new BasicStroke(
                            i.intValue(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1.7f);
                }
            });
            tb.add(shapeSpinner);
            tb.addSeparator();
            shapeSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    activeTool = SHAPE_TOOL;
                    Integer value = shapeSlider.getValue();
                    shapeSpinner.setValue((int) shapeSlider.getValue());
                    shapeStroke = new BasicStroke(
                        value.intValue(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1.7f);
                }
            });
            tb.add(shapeSlider);



            ActionListener clearListener = new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    int result = JOptionPane.OK_OPTION;
                    if (dirty) {
                        result = JOptionPane.showConfirmDialog(
                                gui, "Erase the current painting?");
                    }
                    if (result==JOptionPane.OK_OPTION) {
                        clear(canvasImage);
                    }
                }
            };
            JButton clearButton = new JButton("Clear");
            tb.add(clearButton);
            clearButton.addActionListener(clearListener);

            gui.add(tb, BorderLayout.PAGE_START);

            JToolBar tools = new JToolBar(JToolBar.HORIZONTAL);
            tools.setFloatable(false);
            //final JRadioButton select = new JRadioButton("Select");

            ButtonGroup bg = new ButtonGroup();
           // bg.add(select);
            bg.add(draw);
            bg.add(erase);   
            bg.add(shapeButton);
            bg.add(fill);

            ActionListener toolGroupListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (ae.getSource()==draw) {
                        activeTool = DRAW_TOOL;
                    }else if (ae.getSource()==erase) {
                        activeTool = ERASE_TOOL;
                    }else if (ae.getSource()==shapeButton) {
                        activeTool = SHAPE_TOOL;
                    }else if (ae.getSource()==fill){
                        activeTool = FILL_TOOL;
                    }
                }
            };
            //select.addActionListener(toolGroupListener);
            draw.addActionListener(toolGroupListener);
            erase.addActionListener(toolGroupListener);
            shapeButton.addActionListener(toolGroupListener);
            fill.addActionListener(toolGroupListener);

           // text.addActionListener(toolGroupListener);

            gui.add(tools, BorderLayout.LINE_END);

            gui.add(output,BorderLayout.PAGE_END);
            clear(colorSample);
            clear(canvasImage);
        }

        return gui;
    }

    public void clear(BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        g.setRenderingHints(renderingHints);
        if(bi == colorSample){
            g.setColor(color);
        }
        else{
            g.setColor(bgColor);
        }
        g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        g.dispose();
        imageLabel.repaint();
    }

    public void setImage(BufferedImage image) {
        this.originalImage = image;
        int w = image.getWidth();
        int h = image.getHeight();
        canvasImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = this.canvasImage.createGraphics();
        g.setRenderingHints(renderingHints);
        g.drawImage(image, 0, 0, gui);
        g.dispose();
    }

    public void setColor(Color color) {
        this.color = color;
        clear(colorSample);
    }

    private JMenu getFileMenu(boolean webstart){
        JMenu file = new JMenu("File");
        file.setMnemonic('f');

        JMenuItem newImageItem = new JMenuItem("New");
        newImageItem.setMnemonic('n');
        ActionListener newImage = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                BufferedImage bi = new BufferedImage(
                        1500, 1000, BufferedImage.TYPE_INT_ARGB);
                clear(bi);
                setImage(bi);
            }
        };
        newImageItem.addActionListener(newImage);
        //file.add(newImageItem);

        if (webstart) {
            //TODO Add open/save functionality using JNLP API
        } else {
            //TODO Add save functionality using J2SE API
            file.addSeparator();
            ActionListener openListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    if (!dirty) {
                        JFileChooser ch = getFileChooser();
                        int result = ch.showOpenDialog(gui);
                        if (result==JFileChooser.APPROVE_OPTION ) {
                            try {
                                BufferedImage bi = ImageIO.read(
                                        ch.getSelectedFile());
                                setImage(bi);
                            } catch (IOException e) {
                                showError(e);
                                e.printStackTrace();
                            }
                        }
                    } else {
                        // TODO
                        JOptionPane.showMessageDialog(
                                gui, "TODO - prompt save image..");
                    }
                }
            };
            JMenuItem openItem = new JMenuItem("Open");
            openItem.setMnemonic('o');
            openItem.addActionListener(openListener);
           // file.add(openItem);

            ActionListener saveListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser ch = getFileChooser();
                    int result = ch.showSaveDialog(gui);
                    if (result==JFileChooser.APPROVE_OPTION ) {
                        try {
                            File f = ch.getSelectedFile();
                            ImageIO.write(Canvas.this.canvasImage, "png", f);
                            Canvas.this.originalImage = Canvas.this.canvasImage;
                            dirty = false;
                        } catch (IOException ioe) {
                            showError(ioe);
                            ioe.printStackTrace();
                        }
                    }
                }
            };
            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener(saveListener);
            saveItem.setMnemonic('s');
            file.add(saveItem);
        }

        if (canExit()) {
            ActionListener exit = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // TODO Auto-generated method stub
                    System.exit(0);
                }
            };
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.setMnemonic('x');
            file.addSeparator();
            exitItem.addActionListener(exit);
            file.add(exitItem);
        }

        return file;
    }

    private void showError(Throwable t) {
        JOptionPane.showMessageDialog(
                gui, 
                t.getMessage(), 
                t.toString(), 
                JOptionPane.ERROR_MESSAGE);
    }

    JFileChooser chooser = null;

    public JFileChooser getFileChooser() {
        if (chooser==null) {
            chooser = new JFileChooser();
            FileFilter ff = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
            chooser.setFileFilter(ff);
        }
        return chooser;
    }

    public boolean canExit() {
        boolean canExit = false;
        SecurityManager sm = System.getSecurityManager();
        if (sm==null) {
            canExit = true;
        } else {
            try {
                sm.checkExit(0);
                canExit = true; 
            } catch(Exception stayFalse) {
            }
        }
        return canExit;
    }

    public JMenuBar getMenuBar(boolean webstart){
        JMenuBar mb = new JMenuBar();
        mb.add(this.getFileMenu(webstart));
        return mb;
    }

    public static void main(String[] args) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // use default
                }

                Canvas bp = new Canvas();

                JFrame f = new JFrame("Drawing App");

                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setLocationByPlatform(true);

                f.setContentPane(bp.getGui());
                f.setJMenuBar(bp.getMenuBar(false));

                f.pack();
                f.setMinimumSize(f.getSize());

                f.setVisible(true);

            }
        };
        SwingUtilities.invokeLater(r);
    }


    public void fill(Point point) throws AWTException{

        Graphics2D g = this.canvasImage.createGraphics();
        g.setColor(this.color);
        System.out.println(point.x);
        System.out.println(point.y);
        
        Robot rb= new Robot();
        System.out.println(rb.getPixelColor(point.x, point.y));
        //int color = 
        //point.
        
    }
    public void draw(Point point) {
        points.add(new Point(point));
        Point midpoint = new Point(0,0);

        Graphics2D g = this.canvasImage.createGraphics();
        g.setRenderingHints(renderingHints);
        g.setColor(this.color);
        g.setStroke(drawStroke);
        int n = 0;
        for (int i = 0; i < points.size() - 1; i++){
            Point p1 = points.get(i);
            Point p2 = points.get(i+1);

            midpoint.x = ((p1.x+p2.x)/2);
            midpoint.y = ((p1.y+p2.y)/2);

            g.drawLine(p1.x, p1.y, ((p1.x+midpoint.x)/2), ((p1.y+midpoint.y)/2));
            g.drawLine(((p1.x+midpoint.x)/2), ((p1.y+midpoint.y)/2), midpoint.x, midpoint.y);
            g.drawLine(midpoint.x, midpoint.y, ((p2.x+midpoint.x)/2), ((p2.y+midpoint.y)/2));
            g.drawLine(((p2.x+midpoint.x)/2), ((p2.y+midpoint.y)/2), p2.x, p2.y);

        }
        g.dispose();
        this.imageLabel.repaint();
    }

    public void erase(Point point) {
        System.out.println("enter erase");
        erasePoints.add(new Point(point));
        Graphics2D g = this.canvasImage.createGraphics();
        g.setRenderingHints(renderingHints);
        g.setColor(bgColor);
        g.setStroke(eraseStroke);
        int n = 0;
        for (int i = 0; i < erasePoints.size() - 1; i++){
            Point p1 = erasePoints.get(i);
            Point p2 = erasePoints.get(i+1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
            System.out.println("erasing");
        }        
        g.dispose();
        this.imageLabel.repaint();
    }

    public void shapeDrawer(Point point, String action){
        Graphics2D g = this.canvasImage.createGraphics();
        g.setRenderingHints(renderingHints);
        g.setColor(color);
        g.setStroke(shapeStroke);

        if(selectedShape.equals("Rectangle")){
            if(action.equals("press")){
                start = point;

            }else if(action.equals("drag")){
                end = point;
                int x = Math.min(start.x, end.x);
                int y = Math.min(start.y, end.y);
                int width = Math.abs(start.x - end.x);
                int height = Math.abs(start.y - end.y);
                rect = new Rectangle(x, y, width, height);
                this.imageLabel.repaint();
            }else if(action.equals("release")){
                end = point;
                int x = Math.min(start.x, end.x);
                int y = Math.min(start.y, end.y);
                int width = Math.abs(start.x - end.x);
                int height = Math.abs(start.y - end.y);
                rect = new Rectangle(x, y, width, height);
                g.fill(rect);
                this.imageLabel.repaint();
            }
        }else if(selectedShape.equals("Line")){
            g.setStroke(shapeStroke);
            if(action.equals("press")){
                start = point;
            }else if(action.equals("drag")){
                end = point;
                this.imageLabel.repaint();
            }else if(action.equals("release")){
                end = point;
                g.drawLine(start.x, start.y, end.x, end.y);                
                this.imageLabel.repaint();
            }
        }else if(selectedShape.equals("Circle")){
            if(action.equals("press")){
                start = point;
            }else if(action.equals("drag")){
                end = point;
                int x = Math.min(start.x, end.x);
                int y = Math.min(start.y, end.y);
                int diameter = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
                circle = new Ellipse2D.Double(x, y, diameter, diameter);
                this.imageLabel.repaint();
            }else if(action.equals("release")){
                end = point;                
                int x = Math.min(start.x, end.x);
                int y = Math.min(start.y, end.y);
                int diameter = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
                circle = new Ellipse2D.Double(x, y, diameter, diameter);
                g.fill(circle);    
                this.imageLabel.repaint();            
            }
        }
            g.dispose();
    }

    public BufferedImage createImage(JPanel panel) {

        int w = panel.getWidth();
        System.out.println("width = "+ w);
        int h = panel.getHeight();
        System.out.println("height = " +h);

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        panel.paint(g);
        g.dispose();
        return bi;
    }

    class ImageMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent arg0) {
            if (activeTool==Canvas.DRAW_TOOL) {
                draw(arg0.getPoint());
            // } else if (activeTool==Canvas.TEXT_TOOL) {
            //     // TODO
            //     text(arg0.getPoint());
            } else if (activeTool==Canvas.ERASE_TOOL) {
                System.out.println("eraser clicked");
                erase(arg0.getPoint());
            }else if(activeTool==Canvas.SHAPE_TOOL){
                System.out.println("shape clicked");
                shapeDrawer(arg0.getPoint(), "press");
            }else if(activeTool==Canvas.FILL_TOOL){
                System.out.println("fill clicked");
                try {
                    fill(arg0.getPoint());
                } catch (AWTException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(
                        gui, 
                        "Application error", 
                        "Error!", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if (activeTool==Canvas.DRAW_TOOL){
                points.clear();
            }else if (activeTool==Canvas.ERASE_TOOL){
                erasePoints.clear();
            }else if(activeTool==Canvas.SHAPE_TOOL){
                shapeDrawer(arg0.getPoint(), "release");
            }
        }
    }

    class ImageMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent arg0) {
            reportPositionAndColor(arg0);
            if (activeTool==Canvas.DRAW_TOOL) {
                draw(arg0.getPoint());
            } else if (activeTool==Canvas.ERASE_TOOL) {
                erase(arg0.getPoint());
            } else if (activeTool==Canvas.SHAPE_TOOL){
                shapeDrawer(arg0.getPoint(), "drag");
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            reportPositionAndColor(arg0);
        }

    }

    @Override
    public void paint(Graphics g) {
            super.paint(g);
        if (rect != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHints(renderingHints);
            g2d.setColor(this.color);
            g2d.setStroke(drawStroke);
            g2d.draw(rect);
        }
    }


    private void reportPositionAndColor(MouseEvent me) {
        String text = "";

            text += "X,Y: " + (me.getPoint().x+1) + "," + (me.getPoint().y+1);

        output.setText(text);
    }
}
