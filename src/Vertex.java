import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Vertex implements Drawable {

    int x, y;
    ArrayList<Edge> edges;
    Program program;

    public Vertex(int x, int y) {
        edges = new ArrayList<Edge>();
        this.x = x;
        this.y = y;
    }

    void send(Message message) {
        // TODO aby posielal listy cez queue, neposielal priamo
        edges.get(message.port).to.recieve(message);
    }

    void recieve(Message message) {
        program.recieve(message);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.fillOval(x - 6, y - 6, 12, 12);
        g.setColor(new Color(0, 255, 0));
        g.fillOval(x - 5, y - 5, 10, 10);
    }

}
