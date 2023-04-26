package DrawingTool;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.MouseListener;

public class DrawingTool extends JFrame implements MouseListener,MouseMotionListener,ActionListener{
    int A, B,co=0, c , x1 , x2 , y1, y2;
    Button b1,E,r,g,b,w,black,m,o,y,P,cyan,clear,line;
    Panel panel1;
    Graphics gr;
    boolean lflag=false,p=false,e=false;
 Color[] colors ={Color.RED,Color.BLUE,Color.GREEN,Color.WHITE,Color.MAGENTA,Color.ORANGE,Color.pink,Color.CYAN,Color.BLACK,Color.YELLOW};
 DrawingTool()
 {
        super("DrawingTool");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,700);
        setLayout(null);
        
        panel1 = new Panel(new FlowLayout());
        panel1.setBounds(0,0,80,700);
        panel1.setBackground(Color.GRAY);

        addMouseMotionListener(this);
        addMouseListener(this);

  b1 = new Button("draw");
        E = new Button("erase");
        clear = new Button("clear");
        line = new Button("line");

  r = new Button("red");
  g = new Button("green");
        b = new Button("blue");
        m = new Button("magenta");
        P = new Button("Pink");
        y = new Button("yellow");
        o = new Button("orange");
        cyan= new Button("cyan");
        black = new Button("black");
  
  panel1.add(b1);
  panel1.add(E);
        panel1.add(clear);
        panel1.add(line);
  panel1.add(r);panel1.add(g);panel1.add(b);panel1.add(black);panel1.add(P);panel1.add(cyan);panel1.add(y);panel1.add(o);panel1.add(m);
  b1.addActionListener(this);
        E.addActionListener(this);
        line.addActionListener(this);
  r.addActionListener(this);
  g.addActionListener(this);
        b.addActionListener(this);
        o.addActionListener(this);
        m.addActionListener(this);
        y.addActionListener(this);
        black.addActionListener(this);
        P.addActionListener(this);
        cyan.addActionListener(this);
        clear.addActionListener(this);
        add(panel1);
 }
 public void actionPerformed(ActionEvent ae)
 {
  if(ae.getActionCommand()=="draw")
  {
	  p = true; e =false;lflag = false; co =0;
	  } 
        else if(ae.getActionCommand()=="erase")
        {
        	p = false; e =true;lflag = false; co =0;
        	}
        else if(ae.getActionCommand()=="line") 
        {
        	lflag=true;p=false;e=false; co =0;
        	}
        else if(ae.getActionCommand()=="clear")
        {
   gr= getGraphics();
   gr.setColor(colors[3]);
   gr.fillRect(0,0,700,700);
        }
  else if(ae.getActionCommand()=="red")
  {
	  c=0;
	  }
  else if(ae.getActionCommand()=="green")
  {
	  c=2;
	  }
        else if(ae.getActionCommand()=="blue")
        {
        	c=1;
        	}
        else if(ae.getActionCommand()=="magenta")
        {
        	c=4;
        	}
        else if(ae.getActionCommand()=="cyan")
        {
        	c=7;
        	}
        else if(ae.getActionCommand()=="yellow")
        {
        	c=9;
        	}
        else if(ae.getActionCommand()=="black")
        {
        	c=8;
        	}
        else if(ae.getActionCommand()=="Pink")
        {
        	c=6;
        	}
        else if(ae.getActionCommand()=="orange")
        {
        	c=5;
        	}
    }
    

    public void mouseClicked(MouseEvent e)
    {
        gr = getGraphics();
        System.out.println("click");
        if( lflag && co==0)
        {
            x1 = e.getX();
            y1 = e.getY();
            gr.setColor(colors[c]);
            gr.fillOval(x1,y1,5,5);
            co++;
        }
        else if(lflag && co==1)
        {
            x2 = e.getX();
            y2 = e.getY();
            gr.setColor(colors[c]);
            gr.fillOval(x2,y2,5,5);
            gr.drawLine(x1,y1,x2,y2);
            x1 = x2;
            y1 = y2;
        }
    }
    public void mouseEntered(MouseEvent e)
    {}
    public void mouseExited(MouseEvent e)
    {}
    public void mousePressed(MouseEvent e)
    {}
    public void mouseReleased(MouseEvent e)
    {}

 public void mouseMoved(MouseEvent e)
 {}
 public void mouseDragged(MouseEvent me)
 {
  A=me.getX();
  B=me.getY();
  gr = getGraphics();
  if(p==true)
  {
   gr.setColor(colors[c]);
   gr.fillOval(A,B,6,6);
  }
  else if(e==true)
  {
   gr.setColor(colors[3]);
   gr.fillOval(A,B,15,15);
  }
    }
    public static void main(String[] args)
    {
        new DrawingTool();
    }
}
