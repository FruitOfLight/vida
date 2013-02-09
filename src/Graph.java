import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;

public class Graph implements Drawable {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    public MessageQueue messages;
    // premenne pre vykreslovanie
    public Canvas canvas;
    GraphListener listener;
    int width, height;

    //
    private GraphType type;

    // boolean moving = false, deleting = false;

    public Graph() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        setCanvas(new Canvas(this));
        emptyGraph();
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        listener = new GraphListener(this);
        this.canvas.addMouseListener(listener);
        this.canvas.addMouseMotionListener(listener);
        this.canvas.addMouseWheelListener(listener);
        // this.canvas.setFocusable(true);
    }

    @Override
    public void draw(Graphics2D g) {

        // vykresli vrcholy a hrany
        g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 14));
        for (Edge edge : edges) {
            edge.draw(g);
        }
        listener.draw(g);
        for (Vertex vertex : vertices) {
            vertex.draw(g);
        }
        // vykresli spravy
        try {
            for (Cube cube : Cube.getAllCubes()) {
                cube.message.edgeDraw(g);
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            draw(g);
        }
    }

    public void clickWithMouse(MouseEvent mouse) {
        double x = mouseGetX(mouse);
        double y = mouseGetY(mouse);
        double newRadius = CONST.vertexSize;
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, 0) && mouse.getClickCount() == 2
                    && GUI.model.running == RunState.stopped) {
                vertex.onClicked();
                vertex.repaint(canvas);
                return;
            } else if (vertex.isNearPoint(x, y, 0)) {
                GUI.zoomWindow.drawVertex(vertex);
                return;
            }
        }
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, newRadius)) {
                return;
            }
        }
        if (GUI.model.running == RunState.running) {
            GUI.model.pause();
            GUI.controls.panel.repaint();
        } else if (GUI.model.running == RunState.paused) {
            GUI.model.start();
            GUI.controls.panel.repaint();
        } else {
            createVertex(x, y, getNewVertexID());
        }
        canvas.repaint();
    }

    public void deleteWithMouse(MouseEvent mouse) {
        Vertex v = getVertex(mouseGetX(mouse), mouseGetY(mouse));
        if (v != null) {
            removeVertex(v);
            return;
        }
        ArrayList<Edge> delete = getEdges(mouseGetX(mouse), mouseGetY(mouse));
        for (Edge edge : delete) {
            type = GraphType.any;
            edge.removeFromVertex();
            edges.remove(edge);
        }
    }

    double mouseGetX(MouseEvent mouse) {
        return (mouse.getX() - canvas.offX) / canvas.zoom;
    }

    double mouseGetY(MouseEvent mouse) {
        return (mouse.getY() - canvas.offY) / canvas.zoom;
    }

    public int getNewVertexID() {
        return getNewVertexID(vertices.size());
    }

    public int getNewVertexID(int size) {
        if (GUI.model.settings.isProperty(Property.anonym)) {
            return 0;
        }
        int bound = size * 2;
        if (bound < 10) {
            bound = 10;
        }
        if (bound > 100 && size < 80) {
            bound = 100;
        }

        while (true) {
            int id = GUI.random.nextInt(bound);
            boolean good = true;
            for (Vertex v : vertices) {
                if (v.getID() == id) {
                    good = false;
                    break;
                }
            }
            if (good) {
                return id;
            }
        }
    }

    public void createVertex(double x, double y, int ID) {
        type = GraphType.any;
        Vertex vertex = new Vertex(x, y, ID);
        vertices.add(vertex);
    }

    public void removeVertex(Vertex vertex) {
        type = GraphType.any;
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
        type = GraphType.any;
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
                edgeTo = edge;
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

        // TODO co ak su vrcholy inak velke, pridat parameter max(radius1,
        // radius2)
        canvas.repaintBetween(from.getX(), from.getY(), to.getX(), to.getY());
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
            if (edge.isNear(x, y, canvas.zoom)) {
                result.add(edge);
            }
        }
        return result;
    }

    private static final String version = "Version 1.00";

    public void read(Scanner input) {
        emptyGraph();
        try {
            String line = input.nextLine();
            if (!line.equals(version)) {
                throw new ParseException(line);
            }
            int n = Integer.parseInt(input.next()), m = Integer.parseInt(input.next());
            for (int i = 0; i < n; i++) {
                double x = Double.parseDouble(input.next()), y = Double.parseDouble(input.next());
                int ID = Integer.parseInt(input.next());
                vertices.add(new Vertex(x, y, ID));
            }
            for (int i = 0; i < m; i++) {
                int f = Integer.parseInt(input.next()), t = Integer.parseInt(input.next());
                createEdge(vertices.get(f), vertices.get(t));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        canvas.repaint();
    }

    public void print(PrintStream output) {
        output.println(version);
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
        Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(
                GUI.model.settings.getGraphType());
        int ok = JOptionPane.showConfirmDialog(null, newGraphDialog.getPanel(), "New graph",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            createNew(newGraphDialog);
            // { "Empty", "Clique", "Circle", "Grid", "Wheel", "Random" };
        }
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
        type = GraphType.any;
        canvas.offX = CONST.graphWidth / 2;
        canvas.offY = CONST.graphHeight / 2;
        canvas.zoom = 1.0;
        vertices.clear();
        edges.clear();
    }

    private void createClique(int n, boolean edges) {
        type = GraphType.clique;
        double d = CONST.graphWidth / 3;

        for (int i = 0; i < n; ++i) {
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        }
        if (edges) {
            for (int i = 0; i < n; ++i) {
                for (int j = i + 1; j < n; ++j) {
                    createEdge(vertices.get(i), vertices.get(j));
                }
            }
        }
    }

    private void createCycle(int n, boolean edges) {
        type = GraphType.cycle;
        int d = CONST.graphWidth / 3;

        for (int i = 0; i < n; ++i) {
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        }
        if (edges) {
            for (int i = 0; i < n; ++i) {
                createEdge(vertices.get(i), vertices.get((i + 1) % n));
            }
        }
    }

    private void createWheel(int n, boolean edges) {
        type = GraphType.wheel;
        int d = CONST.graphWidth / 3;

        createVertex(0.0, 0.0, getNewVertexID());
        for (int i = 0; i < n; ++i) {
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        }
        if (edges) {
            for (int i = 0; i < n; ++i) {
                createEdge(vertices.get(i + 1), vertices.get((i + 1) % n + 1));
                createEdge(vertices.get(0), vertices.get(i + 1));
            }
        }
    }

    private void createGrid(int m, int n, boolean edges) {
        type = GraphType.grid;
        canvas.offX = 0;
        canvas.offY = 0;
        double dy = (CONST.graphHeight - 30) / (double) (m - 1);
        double dx = (CONST.graphWidth - 30) / (double) (n - 1);
        System.out.println(dx + " " + dy);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                createVertex(15 + j * dx, 15 + i * dy, getNewVertexID());
            }
        }
        if (edges) {
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (j != n - 1) {
                        createEdge(vertices.get(i * n + j), vertices.get(i * n + j + 1));
                    }
                    if (i != m - 1) {
                        createEdge(vertices.get(i * n + j), vertices.get(i * n + j + n));
                    }
                }
            }
        }
    }

    public void setDefaultValues() {
        for (Vertex vertex : vertices)
            vertex.defaultSettings();
    }

    public void acceptSettings(ModelSettings settings) {
        for (int i = 0; i < vertices.size(); i++)
            vertices.get(i).setID(getNewVertexID(i));
    }

}
