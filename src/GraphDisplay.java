import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;



class GraphDisplay extends JComponent
{
  public static void main(String[] args)
  {
    GraphDisplay gd = new GraphDisplay();
    gd.showInWindow(400,400,"A Random Graph");
		
    //	Add some randomly placed nodes
    int n = 20;
    for(int i=0; i<n; i++)
      gd.addNode(i,Math.random(),Math.random());
		
    //	Add a few random edges in slow motion
    try{
      for(int j=0; j<2*n; j++)
	{
	  int a = (int)(Math.random()*n);
	  int b = (int)(Math.random()*n);
	  gd.addEdge(a,b,Color.red);
	  Thread.sleep(500);
	  gd.addEdge(a,b,Color.black);
	}
    }catch(InterruptedException e) {
    }
  }


  public GraphDisplay()
  {
    minX = minY = Double.POSITIVE_INFINITY;
    maxX = maxY = Double.NEGATIVE_INFINITY;
  }
	
  public synchronized void addNode(Object identifier, double x, double y,
				   Color col)
  {
    maxX = Math.max(maxX,x);
    maxY = Math.max(maxY,y);
    minX = Math.min(minX,x);
    minY = Math.min(minY,y);
    nodes.put(identifier,new Node(x,y,col));
    repaint();
  }

  public synchronized void addNode(Object identifier, double x, double y)
  {
    maxX = Math.max(maxX,x);
    maxY = Math.max(maxY,y);
    minX = Math.min(minX,x);
    minY = Math.min(minY,y);
    nodes.put(identifier,new Node(x,y,NODE_COLOR));
    repaint();
  }
	
  public synchronized void addEdge(Object start, Object end, Color c)
  {
    removeEdge(start,end);
    edges.add(new Edge(start,end,c));
    repaint();
  }
	
  public synchronized boolean removeEdge(Object start, Object end)
  {
    Iterator<Edge> it = edges.iterator();
    while(it.hasNext())
      {
	Edge tmp = it.next();
	if(tmp.joins(start,end))
	  {
	    it.remove();
	    repaint();
	    return true;
	  }
      }
    return false;
  }
	
  public void addEdge(Object start, Object end)
  {
    addEdge(start,end,Color.black);
  }
	
	
  public JFrame showInWindow(int width, int height, String title)
  {
    JFrame f = new JFrame();
    f.add(this);
    f.setSize(width,height);
    f.setTitle(title);
    f.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent we) {
	  System.exit(0);
	}
      });
    f.setVisible(true);
    return f;
  }

  public void paint(Graphics g)
  {
    if(nodes.isEmpty())
      return;
	
    double xscl = (getSize().width -2*MARGIN) / (maxX-minX);
    double yscl = (getSize().height-2*MARGIN) / (maxY-minY);
		
	
    g.translate(+MARGIN,+MARGIN);
	
    synchronized(this){
      for(Edge e: edges)
	e.paint(g,xscl,yscl,minX,minY);
      for(Node n: nodes.values())
	n.paint(g,xscl,yscl,minX,minY);
    }		
		
    g.translate(-MARGIN,-MARGIN);
  }
	
	
	
  protected double minX,maxX,minY,maxY;
  protected HashMap<Object,Node> nodes = new HashMap<Object,Node>();
  protected Vector<Edge> edges = new Vector<Edge>();
	
  protected int   MARGIN      = 20;
  protected int   NODE_RADIUS = 5;
  protected Color NODE_COLOR  = Color.blue.brighter();





	
  private class Node
  {
    public Node(double x, double y, Color col)
    {
      this.x = x;
      this.y = y;
      this.col = col;
    }
		
    public void paint(Graphics g, double xscl, double yscl, double tx, double ty)
    {
      g.setColor(col);
      g.fillOval(
		 (int)((x-tx)*xscl - NODE_RADIUS),
		 (int)((y-ty)*yscl - NODE_RADIUS),
		 2*NODE_RADIUS,
		 2*NODE_RADIUS
		 );
    }
		
    protected double x,y;
    protected Color col;
	        
  }
	
  private class Edge
  {
    public Edge(Object start, Object end, Color col)
    {
      this.start = start;
      this.end = end;
      this.col = col;
    }
		
    public boolean joins(Object a, Object b)
    {
      return	(start.equals(a) && end.equals(b))
	||	(start.equals(b) && end.equals(a));
    }
		
    public void paint(Graphics g, double xscl, double yscl, double tx, double ty)
    {
      Node a = nodes.get(start);
      Node b = nodes.get(end);
      g.setColor(col);
      g.drawLine(
		 (int)(xscl*(a.x-tx)),
		 (int)(yscl*(a.y-ty)),
		 (int)(xscl*(b.x-tx)),
		 (int)(yscl*(b.y-ty))
		 );			
    }
		
    protected Object start,end;
    protected Color col;
  }
}