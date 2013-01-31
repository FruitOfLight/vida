import java.awt.Color;
import java.awt.Graphics;

// orientovana hrana z from do to
public class Edge {

    Vertex from, to;
    Edge oppositeEdge; // hrana z to do from

    static void connectOpposite(Edge e1, Edge e2) {
        e1.oppositeEdge = e2;
        e2.oppositeEdge = e1;
    }

    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
        from.edges.add(this);
    }

    public void draw(Graphics g, double offsetx, double offsety, double zoom) {
        g.setColor(new Color(0, 0, 0));
        int x1 = (int) (offsetx + from.getX() * zoom);
        int y1 = (int) (offsety + from.getY() * zoom);
        int x2 = (int) (offsetx + to.getX() * zoom);
        int y2 = (int) (offsety + to.getY() * zoom);
        g.drawLine(x1, y1, x2, y2);
    }

    public boolean isNear(double x, double y, double zoom) {
        double distance = dist(x, y);
        if (distance > 10.0 / zoom / zoom)
            return false;
        double d = (from.getX() - to.getX()) * (from.getX() - to.getX())
                + (from.getY() - to.getY()) * (from.getY() - to.getY());
        double d1 = (from.getX() - x) * (from.getX() - x) + (from.getY() - y) * (from.getY() - y);
        double d2 = (to.getX() - x) * (to.getX() - x) + (to.getY() - y) * (to.getY() - y);
        if (d1 > d || d2 > d)
            return false;
        return true;
    }

    public double dist(double x, double y) {
        double a1 = to.getY() - from.getY(), b1 = -to.getX() + from.getX(), c1;
        c1 = -a1 * from.getX() - b1 * from.getY();

        double a2 = b1, b2 = -a1, c2;
        c2 = -a2 * x - b2 * y;
        double x1, y1;
        if (a1 == 0) {
            y1 = (-c1 * a2 + a1 * c2) / (-b2 * a1 + b1 * a2);
            x1 = (-c2 - b2 * y1) / (a2);
        } else {
            y1 = (-c2 * a1 + a2 * c1) / (-b1 * a2 + b2 * a1);
            x1 = (-c1 - b1 * y1) / (a1);
        }
        return (y1 - y) * (y1 - y) + (x1 - x) * (x1 - x);
    }

    public void removeFromVertex() {
        if (from == null)
            return;
        from.removeEdge(this);
        from = null;
        oppositeEdge.removeFromVertex();
    }

}
