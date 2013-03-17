import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class VertexInformationPanel implements Drawable {

    class Information {

        public String info;
        public long time;

        public Information(String info, long time) {
            this.info = info;
            this.time = time;
        }

        public String getInfo() {
            return info;
        }

        public long getTime() {
            return time;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public void setTime(long time) {
            this.time = time;
        }

    }

    public ArrayList<Information> informations;
    public Vertex vertex;
    private int padding = 3;
    long expiration = 2000;

    public VertexInformationPanel(Vertex vertex) {
        this.vertex = vertex;
        informations = new ArrayList<Information>();
    }

    public void defaultSettings() {
        informations = new ArrayList<Information>();
    }

    public void updateTimeExpiration() {
        ArrayList<Information> help = new ArrayList<Information>();
        long time = System.currentTimeMillis();
        for (Information i : informations) {
            if (time - i.getTime() > expiration && GUI.model.getRunState() == RunState.running)
                continue;
            help.add(i);
        }
        informations = help;
    }

    public void draw(Graphics2D g) {
        updateTimeExpiration();
        if (informations.size() == 0)
            return;
        g.setColor(Canvas.contrastColor(new Color(255, 255, 255), Constrast.textbw));
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
        //String caption = informations.get(informations.size() - 1).getInfo();
        double x = vertex.getX(), y = vertex.getY() - vertex.getRadius();
        double w = 0;
        for (Information i : informations) {
            w = Math.max(w, g.getFontMetrics().stringWidth(i.getInfo()));
        }
        double h = g.getFontMetrics().getAscent();
        int n = informations.size();
        g.setColor(new Color(150, 214, 250));
        g.fillRoundRect((int) (x - padding), (int) (y - n * h - 2 * padding),
                (int) w + 2 * padding, (int) (n * h) + 2 * padding, 10, 10);
        g.setColor(new Color(0, 0, 250));
        g.drawRoundRect((int) (x - padding), (int) (y - n * h - 2 * padding),
                (int) w + 2 * padding, (int) (n * h) + 2 * padding, 10, 10);
        g.setColor(Canvas.contrastColor(new Color(255, 255, 255), Constrast.textbw));
        int j = 0;
        for (Information i : informations) {
            j++;
            g.drawString(i.getInfo(), (float) x, (float) (y - (n - j) * h - padding));
        }
    }

    public void setInformation(String s) {
        informations.add(new Information(s, System.currentTimeMillis()));
    }

}
