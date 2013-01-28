import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class Graph implements Drawable {

    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public MessageQueue messages;
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
        setCanvas(new Canvas(this));
        xlast = -1; ylast = -1;
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
    	g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, CONST.graphWidth, CONST.graphHeight);
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
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
        // vykresli spravy
        try {
            for (Message message : messages.deadlist)
                message.messageDraw(g);
            for (Message message : messages.list)
                message.messageDraw(g);
        } catch (ConcurrentModificationException e) {

        }
        /*
         * for (int i = 0; i < messages.list.size(); i++) {
         * messages.list.get(i).messageDraw( g,
         * (i+1)*10-MessageQueue.MessageDrawEvent.counter); }
         */

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
            xlast = mouse.getX(); ylast = mouse.getY();
        }

        @Override
        public void mouseReleased(MouseEvent mouse) {
            addEdge(begin, findVertex(mouse.getX(), mouse.getY()));
            canvas.repaint();
            begin = null;
            xlast = -1;
            ylast = -1;
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
            // TODO toto je len provizorne
            if (key.getKeyCode() == 'P')
                MessageQueue.getInstance().sendInterval -= (MessageQueue
                        .getInstance().sendInterval > 200) ? 100 : 10;
            if (key.getKeyCode() == 'M')
                MessageQueue.getInstance().sendInterval += (MessageQueue
                        .getInstance().sendInterval > 200) ? 100 : 10;
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
        int x = mouse.getX(), y = mouse.getY();
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, 0)) {
                Dialog.DialogNewVertex newVertexDialog = new Dialog.DialogNewVertex(
                        vertex.getID());
                int ok = JOptionPane.showConfirmDialog(null,
                        newVertexDialog.getPanel(), "Edit vertex",
                        JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                    vertex.setID(newVertexDialog.getID());
                }
                canvas.repaint();
                return;
            }
        }
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, vertex.getRadius())) {
                return;
            }
        }

        addVertex(x, y, getNewVertexID());
        canvas.repaint();
    }

    public void addVertex(int x, int y, int ID) {
        Vertex vertex = new Vertex(x, y, ID);
        vertices.add(vertex);
    }

    public int getNewVertexID() {
        int bound = vertices.size() * 2;
        if (bound < 10)
            bound = 10;
        if (bound > 100 && vertices.size() < 80)
            bound = 100;

        while (true) {
            int id = GUI.random.nextInt(bound);
            boolean good = true;
            for (Vertex v : vertices)
                if (v.getID() == id) {
                    good = false;
                    break;
                }
            if (good)
                return id;
        }
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
            edge.removeFromVertex();
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
        // TODO dat namiesto 10 polomer vrcholu
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
                addEdge(vertices.get(f), vertices.get(t));
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

    public void createNew() {
        Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph();
        int ok = JOptionPane.showConfirmDialog(null, newGraphDialog.getPanel(),
                "New graph", JOptionPane.OK_CANCEL_OPTION);
        // { "Empty", "Clique", "Circle", "Grid", "Wheel", "Random" };
        if (ok == JOptionPane.OK_OPTION) {
            int d = CONST.graphWidth / 3, n = newGraphDialog.getTF1();
            int middlex = CONST.graphWidth / 2;
            int middley = CONST.graphHeight / 2;
            GUI.graph.vertices.clear();
            GUI.graph.edges.clear();
            switch (newGraphDialog.getType()) {
            case 0:
                break;
            case 1:
                for (int i = 0; i < n; ++i)
                    addVertex(
                            middlex + (int) (d * Math.sin(i * 2 * Math.PI / n)),
                            middley + (int) (d * Math.cos(i * 2 * Math.PI / n)),
                            getNewVertexID());
                if (newGraphDialog.getEdges())
                    for (int i = 0; i < n; ++i)
                        for (int j = i + 1; j < n; ++j)
                            addEdge(vertices.get(i), vertices.get(j));
                break;
            case 2:
                for (int i = 0; i < n; ++i)
                    addVertex(
                            middlex + (int) (d * Math.sin(i * 2 * Math.PI / n)),
                            middley + (int) (d * Math.cos(i * 2 * Math.PI / n)),
                            getNewVertexID());
                if (newGraphDialog.getEdges())
                    for (int i = 0; i < n; ++i)
                        addEdge(vertices.get(i), vertices.get((i + 1) % n));
                break;
            case 3:
                break;
            case 4:
                addVertex(middlex, middley, getNewVertexID());
                for (int i = 0; i < n; ++i)
                    addVertex(
                            middlex + (int) (d * Math.sin(i * 2 * Math.PI / n)),
                            middley + (int) (d * Math.cos(i * 2 * Math.PI / n)),
                            getNewVertexID());
                if (newGraphDialog.getEdges())
                    for (int i = 0; i < n; ++i) {
                        addEdge(vertices.get(i + 1),
                                vertices.get((i + 1) % n + 1));
                        addEdge(vertices.get(0), vertices.get(i + 1));
                    }
                break;
            
            case 5:
                break;
            }

        }
        canvas.repaint();
    }

}
