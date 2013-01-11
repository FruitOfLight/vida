import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Graph implements Drawable {

    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public Canvas canvas;
    public Vertex begin = null;
    int xlast, ylast;

    public Graph() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        GraphListener listener = new GraphListener();
        this.canvas.addMouseListener(listener);
        this.canvas.addMouseMotionListener(listener);
    }

    public void draw(Graphics g) {
        // vykresli polhranu
        if (begin != null) {
            g.setColor(new Color(0, 0, 0));
            g.drawLine(begin.getX(), begin.getY(), xlast, ylast);
        }
        // vykresli vrcholy a hrany
        for (Edge edge : edges) edge.draw(g);
        for (Vertex vertex : vertices) vertex.draw(g);
    }

    class GraphListener implements MouseListener, MouseMotionListener {

        @Override
        public void mouseClicked(MouseEvent mouse) {
            addVertex(mouse);
        }

        @Override
        public void mousePressed(MouseEvent mouse) {
            System.out.println("dafug");
            begin = findVertex(mouse.getX(), mouse.getY());
        }

        @Override
        public void mouseReleased(MouseEvent mouse) {
            addEdge(begin, findVertex(mouse.getX(), mouse.getY()));
            canvas.repaint();
            begin = null;
            xlast = 0;
            ylast = 0;
        }

        @Override
        public void mouseDragged(MouseEvent mouse) {
            repaintBetween(begin.getX(), begin.getY(), xlast, ylast);
            xlast = mouse.getX();
            ylast = mouse.getY();
            repaintBetween(begin.getX(), begin.getY(), xlast, ylast);
        }

        @Override
        public void mouseEntered(MouseEvent mouse) {
        }

        @Override
        public void mouseExited(MouseEvent mouse) {
        }

        @Override
        public void mouseMoved(MouseEvent mouse) {
        }
    }

    public void addVertex(MouseEvent mouse) {
        // TODO ak je zapnute prehravanie, zrusit
        // TODO este musi vybehnut policko, kde zada ID a tak
        int x = mouse.getX(), y = mouse.getY();
        for (Vertex vertex : vertices)
            if (vertex.isNearPoint(x,y,vertex.getRadius())) return;
        Vertex vertex = new Vertex(x, y);
        vertices.add(vertex);    
        vertex.repaint(canvas);
    }

    public void addEdge(Vertex from, Vertex to) {
        // TODO ak je zapnute prehravanie, zrusit
        if (from == null || to == null || from.equals(to))
            return;
        Edge edgeFrom = null;
        Edge edgeTo = null;
        for (int i = 0; i < edges.size(); i++) {
            Vertex f = edges.get(i).from, t = edges.get(i).to;
            if (f.equals(from) && t.equals(to))
                edgeFrom = edges.get(i);
            if (f.equals(to) && t.equals(from))
                edgeTo = edges.get(i);
        }
        if (edgeFrom==null){
            edgeFrom = new Edge(from, to);
            edges.add(edgeFrom);
        }
        if (edgeTo==null){
            edgeTo = new Edge(to, from);
            edges.add(edgeTo);
        }
        Edge.connectOpposite(edgeFrom, edgeTo);
        //vrcholom hrany nepridavame, hrana sa im prida sama
        repaintBetween(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public Vertex findVertex(int x, int y) {
        for (Vertex vertex : vertices)
            if (vertex.isOnPoint(x,y)) return vertex;
        return null;
    }

    public void repaintBetween(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            int p = x1;
            x1 = x2;
            x2 = p;
        }
        if (y1 > y2) {
            int p = y1;
            y1 = y2;
            y2 = p;
        }
        canvas.repaint(x1 - 10, y1 - 10, x2 - x1 + 20, y2 - y1 + 20);
    }

    public void read(Scanner input) {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        try {
            int n = input.nextInt(), m = input.nextInt();
            for (int i = 0; i < n; i++) {
                int x = input.nextInt(), y = input.nextInt();
                vertices.add(new Vertex(x, y));
            }
            for (int i = 0; i < m; i++) {
                int f = input.nextInt(), t = input.nextInt();
                edges.add(new Edge(vertices.get(Math.min(f, t)), vertices
                        .get(Math.max(f, t))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print(PrintStream output) {
        output.println(vertices.size() + " " + edges.size());
        for (int i = 0; i < vertices.size(); i++)
            output.println(vertices.get(i).getX() + " " + vertices.get(i).getY());
        // TODO spravit efektivnejsie indexOf
        for (int i = 0; i < edges.size(); i++)
            output.println(vertices.indexOf(edges.get(i).from) + " "
                    + vertices.indexOf(edges.get(i).to));
    }

}
