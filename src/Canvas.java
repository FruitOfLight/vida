import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class Canvas extends JPanel {

	public Canvas()
	{
	}
	
	static class CanvasListener implements MouseListener
	{
		MouseEvent begin=null;
		
		@Override
		public void mouseClicked(MouseEvent mouse)
		{
			GUI.canvas.addVertex(mouse);
		}
		
		@Override
		public void mousePressed(MouseEvent mouse)
		{
			begin=mouse;
		}
		
		@Override
		public void mouseReleased(MouseEvent mouse)
		{
			GUI.canvas.addEdge(begin, mouse);
		}
		
		@Override public void mouseEntered(MouseEvent mouse) {}
		@Override public void mouseExited(MouseEvent mouse) {}
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
	
	static public void addEdge(MouseEvent begin, MouseEvent end)
	{
		//TODO ak je zapnute prehravanie, zrusit
		int from=findVertex(begin.getX(), begin.getY()),to=findVertex(end.getX(), end.getY());
		if(from==-1 || to==-1) return ;
		boolean exists=false;
		for(int i=0; i<GUI.graph.edges.size(); i++)
		{
			int f=GUI.graph.edges.get(i).from,t=GUI.graph.edges.get(i).to;
			if(f==Math.min(from, to) && t==Math.max(from, to)) exists=true;
		}
		if(exists) return ;
		GUI.graph.edges.add(new Edge(Math.min(from, to), Math.max(from, to)));
		GUI.canvas.repaint(Math.min(begin.getX(), end.getX())-10, Math.min(begin.getY(), end.getY())-10,
				Math.max(begin.getX(), end.getX())-Math.min(begin.getX(), end.getX())+10,
				Math.max(begin.getY(), end.getY())-Math.min(begin.getY(), end.getY())+10);
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
	
	@Override
	public void paintComponent(Graphics g)
	{
        super.paintComponent(g);
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 25, 500, 500);
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
