import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Vertex implements Drawable {

    private int x, y, radius;
    public int getX() { return x;}
    public int getY() { return y;}
    public int getRadius() { return radius;}
    
    ArrayList<Edge> edges;
    Program program;

    public Vertex(int x, int y) {
        edges = new ArrayList<Edge>();
        this.x = x;
        this.y = y;
        radius = 6;
    }

    void send(Message message) {
        // TODO aby posielal listy cez queue, neposielal priamo
        Edge edge = edges.get(message.port);
        if (edge.to.program!=null
                && edge.to.program.running==true){
            // TODO zasa asi enchceme nezbedny indexOf            
            message.port = edge.to.edges.indexOf(edge.oppositeEdge); 
            edge.to.recieve(message);
        }
    }

    void recieve(Message message) {
        program.recieve(message);
    }

    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.fillOval(x - radius, y - radius, 2*radius, 2*radius);
        g.setColor(new Color(0, 255, 0));
        g.fillOval(x - radius+1, y - radius+1, 2*radius-2, 2*radius-2);
    }
    public boolean isOnPoint(int x1, int y1) {return isNearPoint(x1, y1, 0);}
    public boolean isNearPoint(int x1, int y1, int distance) {
        return (x1-x)*(x1-x)+(y1-y)*(y1-y)<(radius+distance)*(radius+distance);
    }
    public void repaint(Canvas canvas) {
        canvas.repaint(x-2*radius,x+2*radius,4*radius,4*radius);        
    }

}
