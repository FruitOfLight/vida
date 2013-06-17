package ui;
/*import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.TimerTask;

public class InformationBubble implements Drawable {

    static class Information {
        int endTime;
        String info;

        Information(String s, int e) {
            info = s;
            endTime = e;
        }
    }

    private double x, y;
    private float transparency;
    private PositionY posY;
    private PositionX posX;
    private double width;
    public ArrayList<Information> informations;
    private final int padding = 3;
    private int fontSize;
    private boolean lockedPosition;

    // @formatter:off
    public void setX(double x) {this.x = x;}
    public double getX() {return x;}
    public void setY(double y) {this.y = y;}
    public double getY() {return y;}
    public void setTransparency(float transparency) {this.transparency = transparency;}
    public float getTransparency() {return transparency;}
    public void setWidth(double width) {this.width = width;}
    public double getWidth() {return width;}
    public void setFontSize(int fontSize) {this.fontSize = fontSize;}
    public int getFontSize() {return fontSize;}
    public void setPositionX(PositionX posX) {this.posX = posX;}
    public void setPositionY(PositionY posY) {this.posY = posY;}
    public PositionX getPositionX() {return posX;}
    public PositionY getPositionY() {return posY;}
    public void setLockedPosition(boolean lockedPosition) {this.lockedPosition = lockedPosition;}
    public boolean getLockedPosition() {return lockedPosition;}
    // @formatter:on

    public InformationBubble(double x, double y) {
        this.x = x;
        this.y = y;
        posX = PositionX.left;
        posY = PositionY.down;
        width = 500;
        informations = new ArrayList<Information>();
        fontSize = 11;
        lockedPosition = false;
        transparency = 1f;
        //TODO: cele zle
        GUI.informationBubbleList.add(this);
    }

    public void defaultSettings() {
        informations = new ArrayList<Information>();
    }

    private AlphaComposite transparentComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return (AlphaComposite.getInstance(type, alpha));
    }

    public void addInformation(String s, int expiration) {
        informations.add(new Information(s, expiration));
    }

    public double getBubbleWidth(Graphics2D g, ArrayList<String> inf) {
        double w = 0;
        for (String s : inf) {
            w = Math.max(w, g.getFontMetrics().stringWidth(s));
        }
        return w;
    }

    public double getBubbleHeight(Graphics2D g, ArrayList<String> inf) {
        return inf.size() * g.getFontMetrics().getAscent();
    }

    public double getBubbleX(double w) {
        double x1 = x;
        if (posX == PositionX.right)
            x1 -= w;
        if (lockedPosition)
            x1 = (x1 - GUI.graph.canvas.offX) / GUI.graph.canvas.zoom;
        return x1;
    }

    public double getBubbleY(double h) {
        double y1 = y;
        if (posY == PositionY.down)
            y1 -= h;
        if (lockedPosition)
            y1 = (y1 - GUI.graph.canvas.offY) / GUI.graph.canvas.zoom;
        return y1;
    }

    public ArrayList<String> parseInformations(Graphics2D g) {
        ArrayList<String> res = new ArrayList<String>();
        for (Information i : informations) {
            String[] sentence = i.info.split(" ");
            String p = "";
            for (int j = 0; j < sentence.length; j++) {
                if (g.getFontMetrics().stringWidth(p) >= width) {
                    res.add(p);
                    p = "";
                }
                p += " ";
                p += sentence[j];
            }
            res.add(p);
        }
        return res;
    }

    public void draw(Graphics2D g) {
        Composite originalComposite = g.getComposite();
        g.setComposite(transparentComposite(transparency));
        if (informations.size() == 0)
            return;
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, fontSize));
        ArrayList<String> printStrings = parseInformations(g);
        double w = getBubbleWidth(g, printStrings);
        double h = getBubbleHeight(g, printStrings);
        double x1 = getBubbleX(w);
        double y1 = getBubbleY(h);
        g.setColor(new Color(150, 214, 250));
        g.fillRoundRect((int) (x1 - padding), (int) (y1 - padding), (int) (w + 2 * padding),
                (int) (h + 2 * padding), 10, 10);
        g.setColor(new Color(0, 0, 250));
        g.drawRoundRect((int) (x1 - padding), (int) (y1 - padding), (int) (w + 2 * padding),
                (int) (h + 2 * padding), 10, 10);
        g.setColor(Canvas.contrastColor(new Color(150, 214, 250), Constrast.textbw));
        int j = 0;
        for (String s : printStrings) {
            j++;
            g.drawString(s, (float) x1, (float) (y1 + j * g.getFontMetrics().getAscent() - padding));
        }
        g.setComposite(originalComposite);
    }

    public void updateExpiration() {
        ArrayList<Information> help = new ArrayList<Information>();
        for (Information i : informations) {
            if (i.getExpiration() > 0)
                i.setExpiration(i.getExpiration() - 1);
            if (i.getExpiration() != 0 && i.getExpiration() != -2)
                help.add(i);
            else if (i.getExpiration() == -2 && GUI.model.running != RunState.running)
                help.add(i);
        }
        informations = help;
    }

    static class ExpirationEvent extends TimerTask {

        @Override
        public void run() {
            if (GUI.model.running != RunState.running) {
                GUI.globalTimer.schedule(new ExpirationEvent(), 100);
                return;
            }
            for (InformationBubble i : GUI.informationBubbleList) {
                i.updateExpiration();
            }
            GUI.globalTimer.schedule(new ExpirationEvent(), 100);
        }
    }

}*/
