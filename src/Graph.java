import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
    double oldOffX, oldOffY;
    int preClickX, preClickY;
    boolean mousePressed;
    boolean dontClick;
    // pozicia
    double offX, offY, zoom;

    //boolean moving = false, deleting = false;

    public Graph() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        setCanvas(new Canvas(this));
        xlast = -1;
        ylast = -1;
        mousePressed = false;
        dontClick = false;
        emptyGraph();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        GraphListener listener = new GraphListener();
        this.canvas.addMouseListener(listener);
        this.canvas.addMouseMotionListener(listener);
        this.canvas.addMouseWheelListener(listener);
        //this.canvas.setFocusable(true);
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
        if (begin != null && !GUI.gkl.isPressed(CONST.deleteKey)
                && !GUI.gkl.isPressed(CONST.moveKey)) {
            g.setColor(new Color(0, 0, 0));
            g.drawLine((int) ((offX + begin.getX()) * zoom), (int) ((offY + begin.getY()) * zoom),
                    xlast, ylast);
        }
        // vykresli vrcholy a hrany
        g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 14));
        for (Edge edge : edges) {
            edge.draw(g, offX, offY, zoom);
        }
        for (Vertex vertex : vertices) {
            vertex.draw(g, offX, offY, zoom);
        }
        // vykresli spravy
        try {
            for (Message message : messages.deadList)
                message.edgeDraw(g, offX, offY, zoom);
            for (Message message : messages.mainList)
                message.edgeDraw(g, offX, offY, zoom);
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            draw(g);
        }
    }

    class GraphListener implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Override
        public void mouseClicked(MouseEvent mouse) {
            if (dontClick)
                return;
            if (GUI.gkl.isPressed(CONST.deleteKey)) {
                if (GUI.model.running != RunState.stopped)
                    return;
                // TODO dovolit, ale opravit graf
                if (ModelSettings.getInstance().getGraphType() != GraphType.none)
                    return;
                deleteMouse(mouse);
                canvas.repaint();
                return;
            }
            if (!GUI.gkl.isPressed(CONST.deleteKey) && !GUI.gkl.isPressed(CONST.moveKey)) {
                clickMouse(mouse);
            }
        }

        @Override
        public void mousePressed(MouseEvent mouse) {
            begin = getVertex(mouseGetX(mouse), mouseGetY(mouse));
            xlast = mouse.getX();
            ylast = mouse.getY();
            preClickX = mouse.getX();
            preClickY = mouse.getY();
            oldOffX = offX;
            oldOffY = offY;
            mousePressed = true;
            dontClick = false;
        }

        @Override
        public void mouseReleased(MouseEvent mouse) {
            if (GUI.model.running == RunState.stopped)
                createEdge(begin, getVertex(mouseGetX(mouse), mouseGetY(mouse)));
            canvas.repaint();
            begin = null;
            xlast = -1;
            ylast = -1;
            mousePressed = false;
        }

        @Override
        public void mouseDragged(MouseEvent mouse) {
            dontClick = true;
            if (begin == null) {
                if (GUI.gkl.isPressed(CONST.moveKey)) {
                    offX = oldOffX + (mouse.getX() - preClickX) / zoom;
                    offY = oldOffY + (mouse.getY() - preClickY) / zoom;
                    canvas.repaint();
                }
                return;
            }
            if (GUI.gkl.isPressed(CONST.moveKey)) {
                for (Vertex vertex : vertices) {
                    if (!vertex.equals(begin)
                            && vertex.isNearPoint(mouse.getX(), mouse.getY(), begin.getRadius())) {
                        return;
                    }
                }
                begin.move(mouseGetX(mouse), mouseGetY(mouse));
                canvas.repaint();

            } else {
                if (GUI.model.running != RunState.stopped)
                    return;
                repaintBetween((int) ((offX + begin.getX()) * zoom),
                        (int) ((offY + begin.getY()) * zoom), xlast, ylast);
                xlast = mouse.getX();
                ylast = mouse.getY();
                repaintBetween((int) ((offX + begin.getX()) * zoom),
                        (int) ((offY + begin.getY()) * zoom), xlast, ylast);
            }
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

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (mousePressed && begin == null) {
                int ticks = e.getWheelRotation();
                zoom *= 1 + ticks * 0.1;
                //System.out.println("Mouse wheel " + ticks);
                dontClick = true;
                canvas.repaint();
            }

        }
    }

    public void clickMouse(MouseEvent mouse) {
        if (GUI.model.running != RunState.stopped)
            return;
        double x = mouseGetX(mouse);
        double y = mouseGetY(mouse);
        double newRadius = (double) CONST.vertexSize;
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, 0)) {
                vertex.clicked();
                vertex.repaint(canvas, offX, offY, zoom);
                return;
            }
        }
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, newRadius)) {
                return;
            }
        }
        createVertex(x, y, getNewVertexID());
        canvas.repaint();
    }

    public void deleteMouse(MouseEvent mouse) {
        Vertex v = getVertex(mouseGetX(mouse), mouseGetY(mouse));
        if (v != null) {
            removeVertex(v);
            return;
        }
        ArrayList<Edge> delete = getEdges(mouseGetX(mouse), mouseGetY(mouse));
        for (Edge edge : delete) {
            edge.removeFromVertex();
            edges.remove(edge);
        }
    }

    public int getNewVertexID() {
        return getNewVertexID(vertices.size());
    }

    public int getNewVertexID(int size) {
        if (ModelSettings.getInstance().getAnonym() == Anonym.anonymOn)
            return 0;
        int bound = size * 2;
        if (bound < 10)
            bound = 10;
        if (bound > 100 && size < 80)
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

    public void createVertex(double x, double y, int ID) {
        Vertex vertex = new Vertex(x, y, ID);
        vertices.add(vertex);
    }

    public void removeVertex(Vertex vertex) {
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

    public void createEdge(Vertex from, Vertex to) {
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        Edge edgeFrom = null;
        Edge edgeTo = null;
        for (Edge edge : edges) {
            if (edge.from.equals(from) && edge.to.equals(to)) {
                edgeFrom = edge;
            }
            if (edge.from.equals(to) && edge.to.equals(from)) {
                edgeFrom = edge;
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

        // TODO co ak su vrcholy inak velke, pridat parameter max(radius1, radius2)
        repaintBetween((int) from.getX(), (int) from.getY(), (int) to.getX(), (int) to.getY());
    }

    double mouseGetX(MouseEvent mouse) {
        return ((double) mouse.getX() / zoom) - offX;
    }

    double mouseGetY(MouseEvent mouse) {
        return ((double) mouse.getY() / zoom) - offY;
    }

    Vertex getVertex(double x, double y) {
        for (Vertex vertex : vertices) {
            if (vertex.isOnPoint(x, y)) {
                return vertex;
            }
        }
        return null;
    }

    ArrayList<Edge> getEdges(double x, double y) {
        ArrayList<Edge> result = new ArrayList<Edge>();
        for (Edge edge : edges) {
            if (edge.isNear(x, y, zoom)) {
                result.add(edge);
            }
        }
        return result;
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
        canvas.repaint(x1 - CONST.vertexSize, y1 - CONST.vertexSize,
                x2 - x1 + CONST.vertexSize * 2, y2 - y1 + CONST.vertexSize * 2);
    }

    public void accept(GraphModifier visitor) {
        visitor.visit(this);
        canvas.repaint();
    }

    public void read(Scanner input) {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        try {
            int n = input.nextInt(), m = input.nextInt();
            for (int i = 0; i < n; i++) {
                double x = input.nextDouble(), y = input.nextDouble();
                int ID = input.nextInt();
                vertices.add(new Vertex(x, y, ID));
            }
            for (int i = 0; i < m; i++) {
                int f = input.nextInt(), t = input.nextInt();
                createEdge(vertices.get(f), vertices.get(t));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void print(PrintStream output) {
        output.println(vertices.size() + " " + edges.size());
        for (int i = 0; i < vertices.size(); i++) {
            output.println(vertices.get(i).getX() + " " + vertices.get(i).getY() + " "
                    + vertices.get(i).getID());
        }
        // TODO spravit efektivnejsie indexOf
        for (int i = 0; i < edges.size(); i++) {
            output.println(vertices.indexOf(edges.get(i).from) + " "
                    + vertices.indexOf(edges.get(i).to));
        }
    }

    public void createNew() {
        Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(ModelSettings
                .getInstance().getGraphType());
        int ok = JOptionPane.showConfirmDialog(null, newGraphDialog.getPanel(), "New graph",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION)
            createNew(newGraphDialog);
        // { "Empty", "Clique", "Circle", "Grid", "Wheel", "Random" };
    }

    public void createNew(Dialog.DialogNewGraph newGraphDialog) {
        emptyGraph();
        switch (newGraphDialog.getType()) {
        case 0:
            break;
        case 1:
            createClique(newGraphDialog.getInputValue(0), newGraphDialog.getEdges());
            break;
        case 2:
            createCycle(newGraphDialog.getInputValue(0), newGraphDialog.getEdges());
            break;
        case 3:
            createGrid(newGraphDialog.getInputValue(0), newGraphDialog.getInputValue(1),
                    newGraphDialog.getEdges());
            break;
        case 4:
            createWheel(newGraphDialog.getInputValue(0), newGraphDialog.getEdges());
            break;
        case 5:
            break;
        }
        canvas.repaint();
    }

    public void emptyGraph() {
        offX = CONST.graphWidth / 2;
        offY = CONST.graphHeight / 2;
        zoom = 1.0;
        vertices.clear();
        edges.clear();
    }

    private void createClique(int n, boolean edges) {
        double d = CONST.graphWidth / 3;

        for (int i = 0; i < n; ++i)
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        if (edges)
            for (int i = 0; i < n; ++i)
                for (int j = i + 1; j < n; ++j)
                    createEdge(vertices.get(i), vertices.get(j));
    }

    private void createCycle(int n, boolean edges) {
        int d = CONST.graphWidth / 3;

        for (int i = 0; i < n; ++i)
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        if (edges)
            for (int i = 0; i < n; ++i)
                createEdge(vertices.get(i), vertices.get((i + 1) % n));
    }

    private void createWheel(int n, boolean edges) {
        int d = CONST.graphWidth / 3;

        createVertex(0.0, 0.0, getNewVertexID());
        for (int i = 0; i < n; ++i)
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        if (edges)
            for (int i = 0; i < n; ++i) {
                createEdge(vertices.get(i + 1), vertices.get((i + 1) % n + 1));
                createEdge(vertices.get(0), vertices.get(i + 1));
            }
    }

    private void createGrid(int m, int n, boolean edges) {
        offX = 0;
        offY = 0;
        double dy = (CONST.graphHeight - 30) / (double) (m - 1);
        double dx = (CONST.graphWidth - 30) / (double) (n - 1);
        System.out.println(dx + " " + dy);
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++) {
                createVertex(15 + j * dx, 15 + i * dy, getNewVertexID());
            }
        if (edges)
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++) {
                    if (j != n - 1)
                        createEdge(vertices.get(i * n + j), vertices.get(i * n + j + 1));
                    if (i != m - 1)
                        createEdge(vertices.get(i * n + j), vertices.get(i * n + j + n));
                }
    }
}
