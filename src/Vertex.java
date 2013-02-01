import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Vertex {

    private double x, y, radius;
    private int ID;

    // @formatter:off
	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getRadius() {
		return radius;
	}

	public void move(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// @formatter:on

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
        draw(g, offsetx, offsety, zoom, true);
    }

    public void draw(Graphics g, double offsetx, double offsety, double zoom, boolean showID) {
        int rX = (int) (offsetx + (x - radius) * zoom);
        int rY = (int) (offsety + (y - radius) * zoom);
        int rR = (int) (radius * zoom * 2);

        g.setColor(new Color(0, 255, 0));
        g.fillOval(rX, rY, rR, rR);
        g.setColor(new Color(0, 0, 0));
        g.drawOval(rX, rY, rR, rR);

        if (showID) {
            g.setFont(new Font(Font.DIALOG, Font.PLAIN, (int) (13 * Math.sqrt(zoom))));
            String caption = Canvas.shorten(g, ((Integer) ID).toString(), rR, Preference.begin);
            if (caption.endsWith(".."))
                caption = "V";
            g.drawString(caption, rX + (rR - g.getFontMetrics().stringWidth(caption)) / 2, rY
                    + (rR + g.getFontMetrics().getAscent()) / 2);
        }

    }

    public void zoomDraw(Graphics g) {
        int edgeHeight = 20;
        int indent = 25;
        int width = CONST.zoomWindowWidth - 2 * indent, height = CONST.zoomWindowHeight - 2
                * indent;
        g.setColor(new Color(0, 255, 0));
        g.fillRect(indent, indent, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(indent, indent, width, height);
        g.drawLine(indent, indent + edgeHeight, width + indent, indent + edgeHeight);
        g.drawLine(indent, height + indent - edgeHeight, width + indent, height + indent
                - edgeHeight);
        ArrayList<Vertex> neigh = new ArrayList<Vertex>();
        for (Edge edge : edges) {
            if (!neigh.contains(edge.to))
                neigh.add(edge.to);
        }
        int n = neigh.size();
        int up = n / 2, down = n / 2 + n % 2;
        for (int i = 0; i < up - 1; i++) {
            g.drawLine(indent + (i + 1) * (width / up), indent, indent + (i + 1) * (width / up),
                    indent + edgeHeight);
        }
        for (int i = 0; i < down - 1; i++) {
            g.drawLine(indent + (i + 1) * (width / down), CONST.zoomWindowHeight - indent
                    - edgeHeight, indent + (i + 1) * (width / down), CONST.zoomWindowHeight
                    - indent);
        }
        for (int i = 0; i < up; i++) {
            g.setFont(new Font(null, Font.PLAIN, edgeHeight));
            String caption = ((Integer) neigh.get(i).getID()).toString();
            int textWidth = g.getFontMetrics().stringWidth(caption);
            int boxWidth = width / up;
            g.drawString(((Integer) neigh.get(i).getID()).toString(), indent + i * boxWidth
                    + (boxWidth - textWidth) / 2, indent + edgeHeight - 1);
            g.drawLine(indent + i * boxWidth + boxWidth / 2, indent, indent + i * boxWidth
                    + boxWidth / 2, 0);
        }
        for (int i = 0; i < down; i++) {
            g.setFont(new Font(null, Font.PLAIN, edgeHeight));
            String caption = ((Integer) neigh.get(up + i).getID()).toString();
            int textWidth = g.getFontMetrics().stringWidth(caption);
            int boxWidth = width / down;
            g.drawString(((Integer) neigh.get(up + i).getID()).toString(), indent + i * boxWidth
                    + (boxWidth - textWidth) / 2, CONST.zoomWindowHeight - indent - 1);
            g.drawLine(indent + i * boxWidth + boxWidth / 2, CONST.zoomWindowHeight - indent,
                    indent + i * boxWidth + boxWidth / 2, CONST.zoomWindowHeight);
        }
        g.setFont(new Font(null, Font.PLAIN, 15));
        g.drawString("ID: " + ((Integer) this.getID()).toString(), indent + 5, indent + edgeHeight
                + 15);
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
