import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Canvas {

    /** Reference to the original image. */
    private BufferedImage originalImage;
    /** Image used to make changes. */
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
    /** The color to use when calling clear, text or other 
     * drawing functionality. */
    private Color color = Color.GRAY;
    /** General user messages. */
    private JLabel output = new JLabel("Drawing App");

    private BufferedImage colorSample = new BufferedImage(
            16,16,BufferedImage.TYPE_INT_RGB);
    private JLabel imageLabel;
    private int activeTool;
    public static final int SELECTION_TOOL = 1;
    public static final int DRAW_TOOL = 0;
    public static final int ERASE_TOOL = 2;
    public static final int POLY_TOOL = 3;
    public static final int SQUARE_TOOL = 4;
    public static final int CIRCLE_TOOL = 5;
    public static final int FILL_TOOL = 6;

    //public static final int TEXT_TOOL = 2;

    private Point selectionStart; 
    private Rectangle selection;
    private boolean dirty = false;
    private Stroke stroke = new BasicStroke(
            3,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.7f);
    private RenderingHints renderingHints;

    public JComponent getGui() {
        if (gui==null) {
            Map<Key, Object> hintsMap = new HashMap<RenderingHints.Key,Object>();
            hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            hintsMap.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            renderingHints = new RenderingHints(hintsMap); 

            setImage(new BufferedImage(320,240,BufferedImage.TYPE_INT_RGB));
            gui = new JPanel(new BorderLayout(4,4));
            gui.setBorder(new EmptyBorder(5,3,5,3));

            JPanel imageView = new JPanel(new GridBagLayout());
            imageView.setPreferredSize(new Dimension(480,320));
            imageLabel = new JLabel(new ImageIcon(canvasImage));
            JScrollPane imageScroll = new JScrollPane(imageView);
            imageView.add(imageLabel);
            imageLabel.addMouseMotionListener(new ImageMouseMotionListener());
            imageLabel.addMouseListener(new ImageMouseListener());
            gui.add(imageScroll,BorderLayout.CENTER);

            JToolBar tb = new JToolBar();
            tb.setFloatable(false);

            final JRadioButton draw = new JRadioButton("Draw", true);
            tb.add(draw); 


            JButton colorButton = new JButton("Color"); //color stuff
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

            final SpinnerNumberModel strModel = new SpinnerNumberModel(1, 1, 100, 1);
            JSpinner strokeSpinner = new JSpinner(strModel);
            strokeSpinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    Object o = strokeSpinner.getValue();
                    Integer i = (Integer)o; 

                    strokeSlider.setValue((int)strokeSpinner.getValue());
                    stroke = new BasicStroke(
                            i.intValue(),
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND,
                            1.7f);
                }
            });
            tb.add(strokeSpinner);
            tb.addSeparator();

            strokeSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e){
                    Integer value = strokeSlider.getValue();
                    strokeSpinner.setValue((int) strokeSlider.getValue());
                    stroke = new BasicStroke(
                        value.intValue(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1.7f);
                }
            });
            tb.add(strokeSlider);

            final JRadioButton erase = new JRadioButton("Erase");
            tb.add(erase);   

            JSlider eraseSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1); //draw slider+spinner

            final SpinnerNumberModel ersModel = new SpinnerNumberModel(1, 1, 100, 1);
            JSpinner eraseSpinner = new JSpinner(ersModel);
            eraseSpinner.addChangeListener(new ChangeListener()
            {
                @Override
                public void stateChanged(ChangeEvent e)
                {
                    Object o = eraseSpinner.getValue();
                    Integer i = (Integer)o; 

                    eraseSlider.setValue((int)eraseSpinner.getValue());
                    stroke = new BasicStroke(
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
                    Integer value = eraseSlider.getValue();
                    eraseSpinner.setValue((int) eraseSlider.getValue());
                    stroke = new BasicStroke(
                        value.intValue(),
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1.7f);
                }
            });
            tb.add(eraseSlider);



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
                    if (ae.getSource()==select) {
                        activeTool = SELECTION_TOOL;
                    } else if (ae.getSource()==draw) {
                        activeTool = DRAW_TOOL;
                    // } else if (ae.getSource()==text) {
                    //     activeTool = TEXT_TOOL;
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

    /** Clears the entire image area by painting it with the current color. */
    public void clear(BufferedImage bi) {
        Graphics2D g = bi.createGraphics();
        g.setRenderingHints(renderingHints);
        g.setColor(color);
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

        selection = new Rectangle(0,0,w,h); 
        if (this.imageLabel!=null) {
            imageLabel.setIcon(new ImageIcon(canvasImage));
            this.imageLabel.repaint();
        }
        if (gui!=null) {
            gui.invalidate();
        }
    }

    /** Set the current painting color and refresh any elements needed. */
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
                        360, 300, BufferedImage.TYPE_INT_ARGB);
                clear(bi);
                setImage(bi);
            }
        };
        newImageItem.addActionListener(newImage);
        file.add(newImageItem);

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
            file.add(openItem);

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
        Graphics2D g = this.canvasImage.createGraphics();
        g.setRenderingHints(renderingHints);
        g.setColor(this.color);
        g.setStroke(stroke);
        int n = 0;
        g.drawLine(point.x, point.y, point.x+n, point.y+n);
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
            // TODO Auto-generated method stub
            if (activeTool==Canvas.SELECTION_TOOL) {
                selectionStart = arg0.getPoint();
            } else if (activeTool==Canvas.DRAW_TOOL) {
                // TODO
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
                        "Application error.  :(", 
                        "Error!", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        @Override
        public void mouseReleased(MouseEvent arg0) {
            if (activeTool==Canvas.SELECTION_TOOL) {
                selection = new Rectangle(
                        selectionStart.x,
                        selectionStart.y,
                        arg0.getPoint().x,
                        arg0.getPoint().y);
            }
        }
    }

    class ImageMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent arg0) {
            reportPositionAndColor(arg0);
            if (activeTool==Canvas.SELECTION_TOOL) {
                selection = new Rectangle(
                        selectionStart.x,
                        selectionStart.y,
                        arg0.getPoint().x-selectionStart.x,
                        arg0.getPoint().y-selectionStart.y);
            } else if (activeTool==Canvas.DRAW_TOOL) {
                draw(arg0.getPoint());
            }
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            reportPositionAndColor(arg0);
        }

    }

    private void reportPositionAndColor(MouseEvent me) {
        String text = "";
        if (activeTool==Canvas.SELECTION_TOOL) {
            text += "Selection (X,Y:WxH): " + 
                    (int)selection.getX() +
                    "," +
                    (int)selection.getY() +
                    ":" +
                    (int)selection.getWidth() +
                    "x" +
                    (int)selection.getHeight();
        } else {
            text += "X,Y: " + (me.getPoint().x+1) + "," + (me.getPoint().y+1);
        }
        output.setText(text);
    }
}
