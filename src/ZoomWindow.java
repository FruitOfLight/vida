import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class ZoomWindow implements Drawable {

    public Canvas canvas;
    public Vertex vertex;
    public int width, height;

    public ZoomWindow() {
        setCanvas(new Canvas(this));
        height = CONST.zoomWindowHeight;
        width = CONST.zoomWindowWidth;
        vertex = null;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawVertex(Vertex v) {
        vertex = v;
        canvas.repaint();
    }

    public void draw(Graphics g) {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        if (vertex == null)
            return;
        double zoom = Math.min((width - 20) / vertex.getRadius() / 2,
                (height - 20) / vertex.getRadius() / 2);
        double offsetX = -vertex.getX() * zoom + width / 2;
        double offsetY = -vertex.getY() * zoom + height / 2;
        int centerX = (int) (offsetX + vertex.getX() * zoom);
        int centerY = (int) (offsetY + vertex.getY() * zoom);
        ArrayList<Vertex> neigh = new ArrayList<Vertex>();
        for (Edge edge : vertex.edges) {
            if (!neigh.contains(edge.to))
                neigh.add(edge.to);
        }
        int n = neigh.size();
        int pom = width;
        for (int i = 0; i < n; i++) {
            g.drawLine(centerX, centerY, width / 2 + (int) (pom * Math.sin(i * 2 * Math.PI / n)),
                    height / 2 + (int) (-pom * Math.cos(i * 2 * Math.PI / n)));
        }
        vertex.draw(g, offsetX, offsetY, zoom, false);
    }
}
