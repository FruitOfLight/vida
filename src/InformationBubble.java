import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.TimerTask;

public class InformationBubble implements Drawable {

    class Information {

        int expiration;
        String info;

        // @formatter:off
        public int getExpiration() {return expiration;}
        public void setExpiration(int expiration) {this.expiration = expiration;}
        public String getInfo() {return info;}
        public void setInfo(String info) {this.info = info;}
        // @formatter:on

        public Information(String info, int expiration) {
            this.info = info;
            this.expiration = expiration;
        }

    }

    public double x, y;
    public ArrayList<Information> informations;
    public boolean transparency;
    private int padding = 3;

    // @formatter:off
    public double getX() {return x;}
    public double getY() {return y;}
    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public boolean getTransparency() {return transparency;}
    public void setTransparency(boolean transparency) {this.transparency = transparency;}
    // @formatter:on

    public InformationBubble(double x, double y) {
        this.x = x;
        this.y = y;
        informations = new ArrayList<Information>();
        GUI.informationBubbleList.add(this);
    }

    public void addInformation(String info, int expiration) {
        informations.add(new Information(info, expiration));
    }

    public void updateExpiration() {
        ArrayList<Information> help = new ArrayList<Information>();
        for (Information i : informations) {
            if (i.getExpiration() > 0)
                i.setExpiration(i.getExpiration() - 1);
            if (i.getExpiration() != 0)
                help.add(i);
        }
        informations = help;
    }

    public void defaultSettings() {
        informations = new ArrayList<Information>();
    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }

    public void draw(Graphics2D g) {
        Composite originalComposite = g.getComposite();
        if (transparency)
            g.setComposite(makeComposite(0.9f));
        if (informations.size() == 0)
            return;
        g.setColor(Canvas.contrastColor(new Color(255, 255, 255), Constrast.textbw));
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
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
        g.setComposite(originalComposite);
    }

    static class ExpirationEvent extends TimerTask {

        @Override
        public void run() {
            for (InformationBubble i : GUI.informationBubbleList) {
                i.updateExpiration();
            }
            GUI.globalTimer.schedule(new ExpirationEvent(), 100);
        }
    }

}
