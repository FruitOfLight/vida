import java.util.ArrayList;

public class Vertex {

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

}
