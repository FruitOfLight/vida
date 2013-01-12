import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Graph implements Drawable {

    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    // premenne pre vykreslovanie
    public Canvas canvas;
    int width, height;
    // premenne pre listenery
    public Vertex begin = null;
    int xlast, ylast;
    boolean moving = false, deleting = false;

    public Graph() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
    }

    public void setPosition(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        GraphListener listener = new GraphListener();
        this.canvas.addMouseListener(listener);
        this.canvas.addMouseMotionListener(listener);
        this.canvas.addKeyListener(listener);
        this.canvas.setFocusable(true);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        // vykresli polhranu
        if (begin != null && !moving && !deleting) {
            g.setColor(new Color(0, 0, 0));
            g.drawLine(begin.getX(), begin.getY(), xlast, ylast);
        }
        // vykresli vrcholy a hrany
        for (Edge edge : edges) {
            edge.draw(g);
        }
        for (Vertex vertex : vertices) {
            vertex.draw(g);
        }
    }

    class GraphListener implements MouseListener, MouseMotionListener,
            KeyListener {

        @Override
        public void mouseClicked(MouseEvent mouse) {
            if (!deleting && !moving) {
                addVertex(mouse);
            }
            if (deleting) {
                Vertex vertex = findVertex(mouse.getX(), mouse.getY());
                if (vertex != null) {
                    deleteVertex(vertex);
                } else {
                    deleteEdges(mouse.getX(), mouse.getY());
                }
                canvas.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent mouse) {
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
            if (begin == null) {
                return;
            }
            if (!moving) {
                repaintBetween(begin.getX(), begin.getY(), xlast, ylast);
                xlast = mouse.getX();
                ylast = mouse.getY();
                repaintBetween(begin.getX(), begin.getY(), xlast, ylast);
            } else {
                for (Vertex vertex : vertices) {
                    if (!vertex.equals(begin)
                            && vertex.isNearPoint(mouse.getX(), mouse.getY(),
                                    vertex.getRadius())) {
                        return;
                    }
                }
                begin.setX(mouse.getX());
                begin.setY(mouse.getY());
                canvas.repaint();
            }
        }

        @Override
        public void keyPressed(KeyEvent key) {
            if (key.getKeyCode() == 16 && !deleting) {
                moving = true;
            }
            if (key.getKeyCode() == 17 && !moving) {
                deleting = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent key) {
            if (key.getKeyCode() == 16) {
                moving = false;
            }
            if (key.getKeyCode() == 17) {
                deleting = false;
            }
        }

        @Override
        public void keyTyped(KeyEvent key) {
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
        Dialog.DialogNewVertex newVertexDialog = new Dialog.DialogNewVertex();
        int ok = JOptionPane.showConfirmDialog(null,
                newVertexDialog.getPanel(), "New vertex",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            int x = mouse.getX(), y = mouse.getY(), ID = newVertexDialog
                    .getID();
            for (Vertex vertex : vertices) {
                if (vertex.isNearPoint(x, y, vertex.getRadius())) {
                    return;
                }
            }
            Vertex vertex = new Vertex(x, y, ID);
            vertices.add(vertex);
        }
        canvas.repaint();
    }

    public void deleteVertex(Vertex vertex) {
        // TODO skontrolovat ci to mazem spravne a vsade kde sa vrchol nachadza
        ArrayList<Edge> delete = new ArrayList<Edge>();
        for (Edge edge : edges) {
            if (edge.from.equals(vertex)) {
                edge.from.edges.remove(edge);
                delete.add(edge);
            }
            if (edge.to.equals(vertex)) {
                edge.to.edges.remove(edge);
                delete.add(edge);
            }
        }
        for (Edge edge : delete) {
            edges.remove(edge);
        }
        vertices.remove(vertex);
    }

    public void addEdge(Vertex from, Vertex to) {
        // TODO ak je zapnute prehravanie, zrusit
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Edge edgeFrom = null;
        Edge edgeTo = null;
        for (int i = 0; i < edges.size(); i++) {
            Vertex f = edges.get(i).from, t = edges.get(i).to;
            if (f.equals(from) && t.equals(to)) {
                edgeFrom = edges.get(i);
            }
            if (f.equals(to) && t.equals(from)) {
                edgeTo = edges.get(i);
            }
        }
        if (edgeFrom == null) {
            edgeFrom = new Edge(from, to);
            edges.add(edgeFrom);
        }
        if (edgeTo == null) {
            edgeTo = new Edge(to, from);
            edges.add(edgeTo);
        }
        Edge.connectOpposite(edgeFrom, edgeTo);
        // vrcholom hrany nepridavame, hrana sa im prida sama
        repaintBetween(from.getX(), from.getY(), to.getX(), to.getY());
    }

    public Vertex findVertex(int x, int y) {
        for (Vertex vertex : vertices) {
            if (vertex.isOnPoint(x, y)) {
                return vertex;
            }
        }
        return null;
    }

    public void deleteEdges(int x, int y) {
        ArrayList<Edge> delete = new ArrayList<Edge>();
        for (Edge edge : edges) {
            if (edge.isNear(x, y)) {
                delete.add(edge);
            }
        }
        for (Edge edge : delete) {
            edges.remove(edge);
        }
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
                int x = input.nextInt(), y = input.nextInt(), ID = input
                        .nextInt();
                vertices.add(new Vertex(x, y, ID));
            }
            for (int i = 0; i < m; i++) {
                int f = input.nextInt(), t = input.nextInt();
                edges.add(new Edge(vertices.get(Math.min(f,t)), vertices.get(Math.max(f,t))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print(PrintStream output) {
        output.println(vertices.size() + " " + edges.size());
        for (int i = 0; i < vertices.size(); i++) {
            output.println(vertices.get(i).getX() + " "
                    + vertices.get(i).getY() + " " + vertices.get(i).getID());
        }
        // TODO spravit efektivnejsie indexOf
        for (int i = 0; i < edges.size(); i++) {
            output.println(vertices.indexOf(edges.get(i).from) + " "
                    + vertices.indexOf(edges.get(i).to));
        }
    }

}
