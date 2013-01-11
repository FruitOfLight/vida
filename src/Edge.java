import java.awt.Color;
import java.awt.Graphics;

// orientovana hrana z from do to
public class Edge implements Drawable{

    Vertex from, to;
    Edge oppositeEdge; // hrana z to do from

    static void connectOpposite(Edge e1, Edge e2){
        e1.oppositeEdge = e2;
        e2.oppositeEdge = e1;
    }
    
    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
        from.edges.add(this);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.drawLine(from.getX(), from.getY(), to.getX(), to.getY());
    }

}
