import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Vertex {

    private double x, y, radius;
    private int ID;

    //@formatter:off
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
    public void move(double x, double y) { this.x = x; this.y = y; }
    //public void setX(int x) { this.x = x; }
    //public void setY(int y) { this.y = y; }
    //@formatter:on

    ArrayList<Edge> edges;
    Program program;

    public Vertex(double x, double y) {
        this(x, y, 0);
    }

    public Vertex(double x, double y, int ID) {
        edges = new ArrayList<Edge>();
        this.x = x;
        this.y = y;
        this.ID = ID;
        radius = CONST.vertexSize;
    }

    void send(Message message) {
        message.setEdge(edges.get(message.fromPort));
        message.toPort = message.edge.to.edges.indexOf(message.edge.oppositeEdge);
        MessageQueue.getInstance().pushMessage(message);
    }

    void receive(Message message) {
        program.receive(message);
    }

    public void draw(Graphics g, double offsetx, double offsety, double zoom) {
        int rX = (int) (offsetx + (x - radius) * zoom);
        int rY = (int) (offsety + (y - radius) * zoom);
        int rR = (int) (radius * zoom * 2);

        g.setColor(new Color(0, 255, 0));
        g.fillOval(rX, rY, rR, rR);
        g.setColor(new Color(0, 0, 0));
        g.drawOval(rX, rY, rR, rR);

        String caption = Canvas.shorten(g, ((Integer) ID).toString(), rR, Preference.begin);
        if (caption.endsWith(".."))
            caption = "V";
        g.drawString(caption, rX + (rR - g.getFontMetrics().stringWidth(caption)) / 2, rY
                + (rR + g.getFontMetrics().getAscent()) / 2);

    }

    public boolean isOnPoint(double x, double y) {
        return isNearPoint(x, y, 0.0);
    }

    public boolean isNearPoint(double x1, double y1, double distance) {
        return (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y) < (radius + distance)
                * (radius + distance);
    }

    public void onClicked() {
        Dialog.DialogNewVertex newVertexDialog = new Dialog.DialogNewVertex(getID());
        int ok = JOptionPane.showConfirmDialog(null, newVertexDialog.getPanel(), "Edit vertex",
                JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            setID(newVertexDialog.getID());
        }
    }

    public void repaint(Canvas canvas, double offsetx, double offsety, double zoom) {
        int rX = (int) (offsetx + (x - radius) * zoom);
        int rY = (int) (offsety + (y - radius) * zoom);
        int rR = (int) (radius * zoom * 2);
        canvas.repaint(rX - rR, rY - rR, 2 * rR, 2 * rR);
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

}
