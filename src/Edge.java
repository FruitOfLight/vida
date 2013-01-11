import java.awt.Color;
import java.awt.Graphics;

// TODO mozno chceme aby hrana bola one directed
public class Edge implements Drawable{

    Vertex from, to;

    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
        from.edges.add(this);
        to.edges.add(this);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.drawLine(from.x, from.y, to.x, to.y);
    }

}
