import java.awt.Color;
import java.awt.Graphics;

public class ZoomWindow implements Drawable {

	public Canvas canvas;

	public ZoomWindow() {
		setCanvas(new Canvas(this));
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public void drawVertex(Vertex v) {
	}

	public void draw(Graphics g) {
		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, CONST.zoomWindowWidth, CONST.zoomWindowHeight);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, CONST.zoomWindowWidth - 1, CONST.zoomWindowHeight - 1);
	}

}
