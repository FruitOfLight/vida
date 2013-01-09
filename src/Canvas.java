import java.awt.Color;	
import java.awt.Graphics;
import javax.swing.JPanel;

public class Canvas extends JPanel {

	public Canvas()
	{
		
	}
	
	public void drawVertex(Graphics g, Vertex v)
	{
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
