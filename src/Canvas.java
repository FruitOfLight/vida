import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class Canvas extends JPanel {

	static int begin=-1;
	static int xp=300,yp=300;
	
	public Canvas()
	{
	}
	
	static class CanvasListener implements MouseListener, MouseMotionListener
	{
		
		@Override
		public void mouseClicked(MouseEvent mouse)
		{
			GUI.canvas.addVertex(mouse);
		}
		
		@Override
		public void mousePressed(MouseEvent mouse)
		{
			begin=findVertex(mouse.getX(),mouse.getY());
		}
		
		@Override
		public void mouseReleased(MouseEvent mouse)
		{
			GUI.canvas.addEdge(begin, findVertex(mouse.getX(),mouse.getY()));
			GUI.canvas.repaint();
			begin=-1; xp=0; yp=0;
		}
		
		@Override
		public void mouseDragged(MouseEvent mouse)
		{
			System.out.println(xp+" "+yp);
			repaintBetween(GUI.graph.vertices.get(begin).x, GUI.graph.vertices.get(begin).y, xp, yp);
			xp=mouse.getX(); yp=mouse.getY();
			repaintBetween(GUI.graph.vertices.get(begin).x, GUI.graph.vertices.get(begin).y, xp, yp);
		}
		
		@Override public void mouseEntered(MouseEvent mouse) {}
		@Override public void mouseExited(MouseEvent mouse) {}
		@Override public void mouseMoved(MouseEvent mouse) {}
	}
	
	static public void addVertex(MouseEvent mouse)
	{
		//TODO ak je zapnute prehravanie, zrusit
		//TODO este musi vybehnut policko, kde zada ID a tak
		int x = mouse.getX(),y = mouse.getY();
		for(int i=0; i<GUI.graph.vertices.size(); i++)
		{
			int xp=GUI.graph.vertices.get(i).x,yp=GUI.graph.vertices.get(i).y;
			if((x-xp)*(x-xp)+(y-yp)*(y-yp)<255) return;
		}
		GUI.graph.vertices.add(new Vertex(x,y));
		GUI.canvas.repaint(x-10, y-10, 20, 20);
	}
	
	static public void addEdge(int from, int to)
	{
		//TODO ak je zapnute prehravanie, zrusit
		if(from==-1 || to==-1 || from==to) return ;
		boolean exists=false;
		for(int i=0; i<GUI.graph.edges.size(); i++)
		{
			int f=GUI.graph.edges.get(i).from,t=GUI.graph.edges.get(i).to;
			if(f==Math.min(from, to) && t==Math.max(from, to)) exists=true;
		}
		if(exists) return ;
		GUI.graph.edges.add(new Edge(Math.min(from, to), Math.max(from, to)));
		repaintBetween(GUI.graph.vertices.get(from).x,GUI.graph.vertices.get(from).y
				,GUI.graph.vertices.get(to).x,GUI.graph.vertices.get(to).y);
	}
	
	static public int findVertex(int x, int y)
	{
		for(int i=0; i<GUI.graph.vertices.size(); i++)
		{
			int x1=GUI.graph.vertices.get(i).x,y1=GUI.graph.vertices.get(i).y;
			if((x1-x)*(x1-x)+(y1-y)*(y1-y)<=100) return i;
		}
		return -1;
	}
	
	public void drawVertex(Graphics g, Vertex v)
	{
		g.setColor(new Color(0,0,0));
		g.fillOval(v.x-6, v.y-6, 12, 12);
		g.setColor(new Color(0,255,0));
		g.fillOval(v.x-5, v.y-5, 10, 10);
	}
	
	public void drawEdge(Graphics g, Vertex from, Vertex to)
	{
		g.setColor(new Color(0,0,0));
		g.drawLine(from.x, from.y, to.x, to.y);
	}
	
	static public void repaintBetween(int x1, int y1, int x2, int y2)
	{
		if(x1>x2) {int p=x1; x1=x2; x2=p;}
		if(y1>y2) {int p=y1; y1=y2; y2=p;}
		GUI.canvas.repaint(x1-10, y1-10, x2-x1+20, y2-y1+20);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
        super.paintComponent(g);
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 25, 500, 500);
        //vykresli polhranu ak existuje
        if(begin!=-1)
        {
        	g.setColor(new Color(0,0,0));
        	g.drawLine(GUI.graph.vertices.get(begin).x,GUI.graph.vertices.get(begin).y,xp,yp);
        }
        // vykresli hrany
        for(int i=0; i<GUI.graph.edges.size(); i++)
        {
        	int from = GUI.graph.edges.get(i).from;
        	int to = GUI.graph.edges.get(i).to;
        	drawEdge(g, GUI.graph.vertices.get(from), GUI.graph.vertices.get(to));
        }
        //vykresli vrcholy
        for(int i=0; i<GUI.graph.vertices.size(); i++)
        {
        	drawVertex(g,GUI.graph.vertices.get(i));
        }
    }
	
}
