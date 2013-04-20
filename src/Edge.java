import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

// orientovana hrana z from do to
public class Edge {

    Vertex from, to;
    Edge oppositeEdge; // hrana z to do from
    boolean selected;
    private boolean removed;
    private int speed;
    MessageQueue queue;

    static void connectOpposite(Edge e1, Edge e2) {
        e1.oppositeEdge = e2;
        e2.oppositeEdge = e1;
    }

    public Edge(Vertex from, Vertex to) {
        queue = new MessageQueue();
        this.from = from;
        this.to = to;
        removed = false;
        selected = false;
        speed = 0; // -2 .. 2
        from.edges.add(this);
    }

    public void restart() {
        queue.clear();
    }

    public void send(Message message) {
        message.setEdge(this);
        message.toPort = this.to.edges.indexOf(this.oppositeEdge);
        queue.pushMessage(message);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color((speed >= 0) ? 0 : (speed == -1) ? 160 : 240, (speed <= 0) ? 0
                : (speed == 1) ? 120 : 160, 0));
        Line2D line = new Line2D.Double(from.getX(), from.getY(), to.getX(), to.getY());
        if (selected)
            g.setStroke(Canvas.boldStroke);
        g.draw(line);
        g.setStroke(Canvas.normalStroke);
    }

    public boolean isNear(double x, double y, double zoom) {
        double distance = Math.abs(dist(x, y));
        if (distance > 5.0 / Math.sqrt(zoom))
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
        double s = Canvas.vectorProduct(from.getX() - x, from.getY() - y, to.getX() - x, to.getY()
                - y);
        double d = Math
                .sqrt(Canvas.scalarProduct(from.getX() - to.getX(), from.getY() - to.getY()));
        return s / d;
    }

    public void remove(Graph graph) {
        if (removed)
            return;
        removed = true;
        graph.edges.remove(this);
        from.removeEdge(this);
        oppositeEdge.remove(graph);
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(-2, Math.min(2, speed));
        if (oppositeEdge.speed != this.speed)
            oppositeEdge.speed = this.speed;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public int hashCode() {
        return from.hashCode() + to.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge))
            return false;
        return from.equals(((Edge) obj).from) && to.equals(((Edge) obj).to);
    }

}
