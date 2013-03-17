import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class VertexInformationPanel implements Drawable {

    public ArrayList<String> informations;
    public Vertex vertex;
    private int padding = 3;

    public VertexInformationPanel(Vertex vertex) {
        this.vertex = vertex;
        informations = new ArrayList<String>();
    }

    public void defaultSettings() {
        informations = new ArrayList<String>();
    }

    public void draw(Graphics2D g) {
        if (informations.size() == 0)
            return;
        g.setColor(Canvas.contrastColor(new Color(255, 255, 255), Constrast.textbw));
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        String caption = informations.get(informations.size() - 1);
        double x = vertex.getX(), y = vertex.getY() - vertex.getRadius();
        double w = g.getFontMetrics().stringWidth(caption);
        double h = g.getFontMetrics().getAscent();
        g.setColor(new Color(150, 214, 250));
        g.fillRect((int) (x - padding), (int) (y - h - 2 * padding), (int) w + 2 * padding, (int) h
                + 2 * padding);
        g.setColor(Canvas.contrastColor(new Color(255, 255, 255), Constrast.textbw));
        g.drawString(caption, (float) x, (float) (y - padding));
    }

    public void setInformation(String s) {
        informations.add(s);
    }

}
