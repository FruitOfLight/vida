import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;

public class Graph implements Drawable {
    public ArrayList<Vertex> vertices;
    public ArrayList<Edge> edges;
    // premenne pre vykreslovanie
    public Canvas canvas;
    double mousex, mousey;
    GraphListener listener;

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
            Model.sfps = (int) (1000 / longest);
            Model.afps = (int) (1000 * ticks / totalTime);
            totalTime = 0;
            longest = 0;
            ticks = 0;
        }
        //

        if (pauseTime > 0) {
            if (System.currentTimeMillis() - pauseTime > waitTime) {
                pauseTime = -1;
                waitTime = 0;
                GUI.model.running = RunState.running;
                GUI.model.start();
            }
        }
        // vykresli vrcholy a hrany
        g.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 14));
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
        if (GUI.model.algorithm != null)
            GUI.model.algorithm.draw(g);
    }

    public boolean selectWithMouse(MouseEvent mouse) {
        Tool tool = GUI.controls.getTool();
        Object o = getObject(mouseGetX(mouse), mouseGetY(mouse), tool);

        if (o == null)
            return false;
        if (o instanceof Vertex) {
            if (mouse.getClickCount() == 2 && GUI.model.running == RunState.stopped) {
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

    public boolean applySpeedTool(MouseEvent mouse, Tool tool) {
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
        edges.remove(edge);
        edge.removeFromVertex();
        edited();
    }

    Object getObject(double x, double y, Tool tool) {
        Object o = null;
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
        try {
            String line = input.nextLine();
            if (!line.equals(version)) {
                throw new ParseException(line);
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
            emptyGraph();
        }
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

    public void pushAway(Vertex v) {
        for (Vertex neig : vertices) {
            if (neig == v)
                continue;
            if (!v.isNearPoint(neig.getX(), neig.getY(), neig.getRadius() + 5))
                continue;
            for (int i = 0; i < 50; i++) {
                double dx = i;
                if (Math.random() < 0.5)
                    dx += 3 * Math.random();
                else {
                    dx *= -1;
                    dx -= 3 * Math.random();
                }
                double dy = i;
                if (Math.random() < 0.5)
                    dy += 3 * Math.random();
                else {
                    dy *= -1;
                    dy -= 3 * Math.random();
                }
                boolean t = true;
                for (Vertex v1 : vertices) {
                    if (v1 == neig)
                        continue;
                    if (v1.isNearPoint(neig.getX() + dx, neig.getY() + dy, 5 + neig.getRadius()))
                        t = false;
                }
                if (!t)
                    continue;
                neig.move(neig.getX() + dx, neig.getY() + dy);
                break;
            }
        }
    }

    public String getTypeString() {
        return type.toString() + " " + vertices.size();
    }

    public void createNew() {
        Dialog.DialogNewGraph newGraphDialog = new Dialog.DialogNewGraph(
                (GUI.model.settings.getGraphType() == GraphType.any) ? type
                        : GUI.model.settings.getGraphType(),
                (GUI.model.settings.getGraphType() == GraphType.any) ? false : true);
        int ok = JOptionPane.showConfirmDialog(null, newGraphDialog.getPanel(), "New graph",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            createNew(newGraphDialog);
        }
    }

    public void createNew(Dialog.DialogNewGraph newGraphDialog) {
        emptyGraph();
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
        GUI.gRepaint();
    }

    public void resizeCanvas(int w, int h) {
        canvas.offX += (w - canvas.getWidth()) * 0.5;
        canvas.offY += (h - canvas.getHeight()) * 0.5;
        canvas.setSize(w, h);
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
        double d = canvas.getHeight() / 3;

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
        int d = canvas.getHeight() / 3;

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
        int d = canvas.getHeight() / 3;

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
        canvas.offX = 0;
        canvas.offY = 0;
        double dy = (canvas.getHeight() - 30) / (double) (m - 1);
        double dx = (canvas.getWidth() - 30) / (double) (n - 1);
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
            break;
        case cycle:
            break;
        case grid:
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
        // zacykli sa pri odkomentovani        
        //for (int i = 0; i < vertices.size(); i++)
        //    vertices.get(i).setID(getNewVertexID(i));
    }

}
