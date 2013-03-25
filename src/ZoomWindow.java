import java.awt.Color;
import java.awt.Graphics2D;

public class ZoomWindow implements Drawable {

    public Canvas canvas;
    public Vertex vertex;
    public int width, height;

    public ZoomWindow() {
        setCanvas(new Canvas(this));
        canvas.setBackground(new Color(0, 0, 0, 0));
        canvas.repaintColor = new Color(200, 255, 255, 200);
        height = CONST.zoomWindowHeight;
        width = CONST.zoomWindowWidth;
        vertex = null;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void drawVertex(Vertex v) {
        vertex = v;
        GUI.gRepaint();
        //canvas.repaint();
    }

    public void draw(Graphics2D g) {
        //g.setColor(new Color(255, 255, 255, 200));
        //g.fillRect(0, 0, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, width - 1, height - 1);
        if (vertex == null)
            return;
        vertex.zoomDraw(g);
    }
}
