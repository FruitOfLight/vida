package graph;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import model.ModelSettings;
import model.Player;
import ui.CONST;
import ui.Canvas;
import ui.Dialog;
import ui.Drawable;
import ui.GUI;
import ui.Tool;
import enums.GraphType;
import enums.InitType;
import enums.Property;
import enums.RunState;
import enums.ToolTarget;
import enums.ToolType;

public class Graph implements Drawable {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    // premenne pre vykreslovanie
    public Canvas canvas;
    public double mousex, mousey;
    public GraphListener listener;
    public Player player;

    //
    private GraphType type = GraphType.any;

    public GraphType getType() {
        return type;
    }

    // boolean moving = false, deleting = false;
    //pausnuty graf
    long pauseTime = -1;
    long waitTime = 0;

    public Graph() {
        player = GUI.player;
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
    }

    static long ticks, totalTime, longest;
    static long time, lastTime, delay;

    @Override
    public void draw(Graphics2D g) {
        // TODO toto fps meranie je tu len docasne
        time = System.currentTimeMillis();
        delay = time - lastTime;
        lastTime = time;
        longest = Math.max(longest, delay);
        totalTime += delay;
        ticks++;
        if (totalTime >= 2000) {
            Player.sfps = (int) (1000 / longest);
            Player.afps = (int) (1000 * ticks / totalTime);
            totalTime = 0;
            longest = 0;
            ticks = 0;
        }
        //

        if (pauseTime > 0) {
            if (System.currentTimeMillis() - pauseTime > waitTime) {
                pauseTime = -1;
                waitTime = 0;
                player.state = RunState.running;
                player.start();
            }
        }
        // vykresli vrcholy a hrany
        g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 14));
        for (Edge edge : edges) {
            edge.preDraw(g);
        }
        for (Edge edge : edges) {
            edge.draw(g);
        }
        g.setStroke(Canvas.thinStroke);
        listener.draw(g);
        for (Vertex vertex : vertices) {
            vertex.draw(g);
        }
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
        for (Edge edge : edges) {
            edge.queue.draw(g);
        }
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
        mousex = cursorGetX();
        mousey = cursorGetY();
        //System.out.println("mxy " + mousex + " " + mousey);
        for (Vertex vertex : vertices) {
            vertex.bubble.draw(g);
        }
        Vertex.randomInit.draw(g);
        if (player.state != RunState.stopped)
            player.observer.draw(g);
    }

    public boolean selectWithMouse(MouseEvent mouse) {
        Tool tool = GUI.controls.getTool();
        Object o = getObject(mouseGetX(mouse), mouseGetY(mouse), tool);

        if (o == null)
            return false;
        if (o instanceof Vertex) {
            if (mouse.getClickCount() == 2 && player.state == RunState.stopped) {
                ((Vertex) o).onClicked();
            } else {
                GUI.zoomWindow.drawVertex(((Vertex) o));
                GUI.zoomWindow.canvas.setVisible(true);
            }
            return true;
        } else if (o instanceof Edge) {
            ((Edge) o).selected ^= true;
            return true;
        } else if (o instanceof Message) {
            ((Message) o).selected = 5;
            return true;
        }
        return false;
    }

    public boolean toggleInitWithMouse(MouseEvent mouse) {
        Tool tool = GUI.controls.getTool();
        Object o = getObject(mouseGetX(mouse), mouseGetY(mouse), tool);
        if (o == null)
            return false;
        if (o instanceof RandomInit) {
            ((RandomInit) o).toggleInitial();
            return true;
        } else if (o instanceof Vertex) {
            if (player.state == RunState.stopped) {
                ((Vertex) o).toggleInitial();
                return true;
            }
        }
        return false;
    }

    public boolean deleteWithMouse(MouseEvent mouse) {
        Tool tool = GUI.controls.getTool();
        Object o = getObject(mouseGetX(mouse), mouseGetY(mouse), tool);
        if (o == null)
            return false;
        if (o instanceof Vertex) {
            removeVertex((Vertex) o);
            return true;
        } else if (o instanceof Edge) {
            removeEdge((Edge) o);
            return true;
        }
        return false;
    }

    public boolean applySpeedTool(MouseEvent mouse, ui.Tool tool) {
        Object o = getObject(mouseGetX(mouse), mouseGetY(mouse), tool);
        if (o == null)
            return false;
        if (o instanceof Vertex) {
            for (Edge edge : ((Vertex) o).edges)
                edge.setSpeed(tool.value);
            return true;
        } else if (o instanceof Edge) {
            ((Edge) o).setSpeed(tool.value);
            return true;
        } else if (o instanceof Message) {
            return true;
        }
        return false;
    }

    double mouseGetX(MouseEvent mouse) {
        return (mouse.getX() - canvas.offX) / canvas.zoom;
    }

    double mouseGetY(MouseEvent mouse) {
        return (mouse.getY() - canvas.offY) / canvas.zoom;
    }

    double cursorGetX() {
        Point p = canvas.getMousePosition();
        return ((p == null ? -1e10 : p.x) - canvas.offX) / canvas.zoom;
    }

    double cursorGetY() {
        Point p = canvas.getMousePosition();
        return ((p == null ? -1e10 : p.y) - canvas.offY) / canvas.zoom;
    }

    public int getNewVertexID() {
        return getNewVertexID(vertices.size());
    }

    public int getNewVertexID(int size) {
        if (player.model.settings.isProperty(Property.anonym)) {
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

    public boolean createVertex(double x, double y, int ID) {
        double newRadius = CONST.vertexSize;
        for (Vertex vertex : vertices) {
            if (vertex.isNearPoint(x, y, newRadius)) {
                return false;
            }
        }
        Vertex vertex = new Vertex(x, y, ID);
        vertices.add(vertex);
        edited();
        return true;
    }

    public void removeVertex(Vertex vertex) {
        ArrayList<Edge> delete = new ArrayList<Edge>();
        for (Edge edge : vertex.edges) {
            delete.add(edge);
        }
        for (Edge edge : delete) {
            edge.remove(this);
        }
        vertices.remove(vertex);
        edited();
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
        edited();
    }

    void removeEdge(Edge edge) {
        edge.remove(this);
        edited();
    }

    Object getObject(double x, double y, Tool tool) {
        Object o = null;
        if (tool.type == ToolType.init && Vertex.randomInit.isOnPoint(x, y)) {
            return Vertex.randomInit;
        }
        if (tool.compatible(ToolTarget.message) && (o = getMessage(x, y)) != null) {
            return o;
        }
        if (tool.compatible(ToolTarget.vertex) && (o = getVertex(x, y)) != null) {
            return o;
        }
        if (tool.compatible(ToolTarget.edge) && (o = getEdge(x, y)) != null) {
            return o;
        }
        return null;
    }

    Vertex getVertex(double x, double y) {
        for (Vertex vertex : vertices) {
            if (vertex.isOnPoint(x, y)) {
                return vertex;
            }
        }
        return null;
    }

    Edge getEdge(double x, double y) {
        for (Edge edge : edges) {
            if (edge.isNear(x, y, canvas.zoom)) {
                return edge;
            }
        }
        return null;
    }

    Message getMessage(double x, double y) {
        Message result = null;
        for (Edge edge : edges)
            for (Message message : edge.queue.getMessages()) {
                if (message.isOnPoint(x, y)
                        && (result == null || message.position < result.position))
                    result = message;
            }
        return result;
    }

    private static final String version = "Version 1.01";

    public void read(Scanner input) {
        emptyGraph();
        checking = true;
        try {
            String line = input.nextLine();
            if (!line.equals(version)) {
                throw new ParseException(line, -1);
            }
            GraphType loadedType = GraphType.valueOf(input.nextLine());
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
            type = loadedType;
        } catch (Exception e) {
            System.err.println("Exception while loading graph");
            e.printStackTrace();
            emptyGraph();
        }
        checking = false;
        edited();
        fitToScreen();
        GUI.gRepaint();
    }

    public void print(PrintStream output) {
        output.println(version);
        output.println(type.name());
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

    public String getTypeString() {
        return type.toString() + " " + vertices.size();
    }

    public void createNew() {
        Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(
                (player.model.settings.getGraphType() == GraphType.any) ? type
                        : player.model.settings.getGraphType(),
                (player.model.settings.getGraphType() == GraphType.any) ? false : true);
        int ok = JOptionPane.showConfirmDialog(null, newGraphDialog.getPanel(), "New graph",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            createNew(newGraphDialog);
        }
    }

    public void createNew(Dialog.DialogNewGraph newGraphDialog) {
        emptyGraph();
        checking = true;
        switch (newGraphDialog.getType()) {
        case 0:
            type = GraphType.any;
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
        checking = false;
        fitToScreen();
        GUI.gRepaint();
    }

    public void resizeCanvas(int w, int h) {
        canvas.offX += (w - canvas.getWidth()) * 0.5;
        canvas.offY += (h - canvas.getHeight()) * 0.5;
        canvas.setSize(w, h);
    }

    public void fitToScreen() {
        if (vertices.size() == 0 || canvas.getWidth() <= 0 || canvas.getHeight() <= 0) {
            canvas.offX = canvas.getWidth() / 2;
            canvas.offY = canvas.getHeight() / 2;
            canvas.zoom = 1.0;
            return;
        }
        double minx = vertices.get(0).getX(), maxx = vertices.get(0).getX(), miny = vertices.get(0)
                .getY(), maxy = vertices.get(0).getY();
        for (Vertex v : vertices) {
            minx = Math.min(minx, v.getX());
            maxx = Math.max(maxx, v.getX());
            miny = Math.min(miny, v.getY());
            maxy = Math.max(maxy, v.getY());
        }
        double border = 20.0;
        double w = maxx - minx + 2 * border, h = maxy - miny + 2 * border;
        canvas.zoom = Math.min(canvas.getWidth() / w, canvas.getHeight() / h);
        canvas.offX = -(minx + maxx) / 2.0 * canvas.zoom + canvas.getWidth() / 2;
        canvas.offY = -(miny + maxy) / 2.0 * canvas.zoom + canvas.getHeight() / 2;
    }

    public void emptyGraph() {
        canvas.offX = canvas.getWidth() / 2;
        canvas.offY = canvas.getHeight() / 2;
        canvas.zoom = 1.0;
        vertices.clear();
        edges.clear();
        edited();
    }

    private void createClique(int n, boolean edges) {
        double d = canvas.getHeight() / 3.0 * Math.sqrt(n) / 5;

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
        type = GraphType.clique;
    }

    private void createCycle(int n, boolean edges) {
        double d = canvas.getHeight() / 3 * Math.sqrt(n) / 5;

        for (int i = 0; i < n; ++i) {
            createVertex(d * Math.sin(i * 2 * Math.PI / n), -d * Math.cos(i * 2 * Math.PI / n),
                    getNewVertexID());
        }
        if (edges) {
            for (int i = 0; i < n; ++i) {
                createEdge(vertices.get(i), vertices.get((i + 1) % n));
            }
        }
        type = GraphType.cycle;
    }

    private void createWheel(int n, boolean edges) {
        double d = canvas.getHeight() / 3 * Math.sqrt(n) / 5;

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
        type = GraphType.wheel;
    }

    private void createGrid(int m, int n, boolean edges) {
        double dy = 20 + 50 / Math.sqrt(m);
        double dx = 20 + 50 / Math.sqrt(n);
        System.out.println(dx + " " + dy);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                createVertex(j * dx, i * dy, getNewVertexID());
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
        type = GraphType.grid;
    }

    private boolean checking = false;

    public void edited() {
        if (checking)
            return;
        System.out.println("edited");
        checking = true;
        boolean fix = (GUI.controls != null && GUI.controls.get("g_lock-type") != null && GUI.controls
                .get("g_lock-type").isActive());
        boolean correct = true;
        switch (type) {
        case any:
            break;
        case clique:
            for (Vertex v : vertices) {
                if (v.edges.size() < vertices.size() - 1) {
                    TreeSet<Vertex> neighs = new TreeSet<Vertex>();
                    for (Edge e : v.edges)
                        neighs.add(e.to);
                    for (Vertex u : vertices) {
                        if (u != v && !neighs.contains(u))
                            if (fix)
                                createEdge(v, u);
                            else
                                correct = false;
                    }
                }
            }
            break;
        case wheel:
            correct = false;
            break;
        case cycle:
            for (Vertex v : vertices) {
                if (v.edges.size() > 2) {
                    if (fix)
                        for (Edge e : v.edges) {
                            if (e.to.edges.size() > 2) {
                                removeEdge(e);
                                if (v.edges.size() <= 2)
                                    break;
                            }
                        }
                    if (v.edges.size() > 2) {
                        correct = false;
                    }
                }
                if (v.edges.size() == 0) {
                    if (fix) {
                        Edge closest = null;
                        for (Edge e : edges) {
                            if (closest == null
                                    || (closest.closedDist(v.getX(), v.getY()) > e.closedDist(
                                            v.getX(), v.getY())))
                                closest = e;
                        }
                        if (closest != null) {
                            createEdge(closest.from, v);
                            createEdge(closest.to, v);
                            removeEdge(closest);
                        }
                    } else {
                        correct = false;
                    }
                }
                // moze nastat aj == 0 z predosleho pripadu
                if (v.edges.size() < 2) {
                    if (fix)
                        for (Vertex u : vertices) {
                            if (u.edges.size() < 2) {
                                createEdge(v, u);
                                if (v.edges.size() >= 2)
                                    break;
                            }
                        }
                    if (v.edges.size() > 2) {
                        correct = false;
                    }
                }
            }
            break;
        case grid:
            correct = false;
            break;
        }
        if (!correct) {
            type = GraphType.any;
        }
        checking = false;
    }

    public void setDefaultValues() {
        for (Vertex vertex : vertices)
            vertex.defaultSettings();
    }

    public void acceptSettings(ModelSettings settings) {
        if (settings.getInit() == InitType.no) {
            while (Vertex.initialSet.size() > 0)
                Vertex.initialSet.first().setInitial(0);
        }
        if (settings.getInit() == InitType.one) {
            while (Vertex.initialSet.size() > 1)
                Vertex.initialSet.first().setInitial(0);
        }
        Vertex.randomInit.autoInitial();
    }
}
