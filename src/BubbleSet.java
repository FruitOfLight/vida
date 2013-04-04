import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class BubbleSet {
    // nemazat tu komentare !!!

    // Vsetko co pracuje s list musi byt synchronized
    //private LinkedList<Bubble> list = new LinkedList<Bubble>();
    //private LinkedList<Bubble> bornlist = new LinkedList<Bubble>();
    static long time = 1;

    public void step(long delay) {
        time += (long) (delay * Math.sqrt(GUI.model.getSendSpeed()));
    }

    /*nemazat komentar
     * @Override
    synchronized public void draw(Graphics2D g) {
        g.setFont(new Font(Font.DIALOG, Font.PLAIN, (int) (fontSize / Math
                .sqrt(GUI.graph.canvas.zoom))));

        for (Bubble bubble : list)
            bubble.draw(g);
    }

    synchronized public void clear() {
        list.clear();
        bornlist.clear();
        time = 1;
    }

    public void addBubble(Bubble bubble) {
        bornlist.add(bubble);
    }

    synchronized public void updateBubbles() {
        while (bornlist.size() > 0)
            list.add(bornlist.pop());
        // vyfiltrujeme vsetky mrtve bubliny
        for (Iterator<Bubble> it = list.iterator(); it.hasNext();) {
            if (it.next().state == BubbleState.dead)
                it.remove();
        }
    }
    */
}

class Bubble implements Drawable {
    static class Information {
        long endTime;
        String info;

        Information(String s, long e) {
            info = s;
            endTime = e;
        }

        boolean dead(long time, boolean paused) {
            return (endTime > 0 && endTime <= time) || (endTime == -2 && !paused);
        }

        int wrap(Graphics g, int width, ArrayList<String> list) {
            int lastNewLine = 0, lastSpace = 0, w = 0, maxw = 0;
            for (int i = 0; i < info.length(); ++i) {
                if (info.charAt(i) == ' ') {
                    w += g.getFontMetrics().stringWidth(info.substring(lastSpace, i + 1));
                    if (w >= width) {
                        list.add(info.substring(lastNewLine, i));
                        maxw = Math.max(g.getFontMetrics().stringWidth(list.get(list.size() - 1)),
                                maxw);
                        w = 0;
                        lastNewLine = i + 1;
                    }
                    lastSpace = i + 1;
                }
            }
            if (lastNewLine < info.length()) {
                list.add(info.substring(lastNewLine));
                maxw = Math.max(g.getFontMetrics().stringWidth(list.get(list.size() - 1)), maxw);
            }
            list.add("");
            return maxw;
        }
    }

    static int getFontSize(Canvas canvas) {
        return (int) (11. / Math.sqrt(canvas.zoom));
    }

    BubbleState state;
    private LinkedList<Information> list;
    private boolean updated;
    private int maxWidth;

    void setMaxWidth(int width) {
        maxWidth = width;
        updated = false;
    }

    float transparency;
    boolean locked;
    BubblePosition position;
    ArrayList<String> lines;

    private double x, y, dx, dy, dw, dh, dr = 8;

    public Bubble(double x, double y) {
        list = new LinkedList<Bubble.Information>();
        lines = new ArrayList<String>();
        state = BubbleState.alive;
        this.x = x;
        this.y = y;
        locked = false;
        updated = false;
        maxWidth = 300;
        position = BubblePosition.NE;
        transparency = 1.0f;
    }

    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setLockedPosition(boolean b) {
        locked = b;
    }

    @Override
    public synchronized void draw(Graphics2D g) {
        float alpha = transparency;
        Composite originalComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        if (list.size() == 0)
            return;
        AffineTransform at = g.getTransform();
        preDraw(g);

        g.setColor(new Color(150, 214, 250));
        g.fill(new RoundRectangle2D.Double(dx, dy, dw, dh, dr, dr));
        g.setColor(Canvas.contrastColor(g.getColor(), Constrast.textbw));
        g.draw(new RoundRectangle2D.Double(dx, dy, dw, dh, dr, dr));

        int j = 0;
        for (String s : lines) {
            j += (s.equals("")) ? g.getFontMetrics().getAscent() * 0.25 : g.getFontMetrics()
                    .getAscent();
            g.drawString(s, (float) (dx + dr / 2), (float) (dy + j));
        }
        g.setTransform(at);
        g.setComposite(originalComposite);
    }

    public synchronized void preDraw(Graphics2D g) {
        if (!updated) {
            dw = dh = 0;
            lines.clear();
            for (Information info : list) {
                dw = Math.max(dw, (double) info.wrap(g, maxWidth, lines));
                // Mala medzera medzi spavami
                dh -= g.getFontMetrics().getAscent();
                dh += g.getFontMetrics().getAscent() * 0.25;
            }
            dw += dr;
            dh += lines.size() * g.getFontMetrics().getAscent() - g.getFontMetrics().getAscent()
                    * 0.25 + g.getFontMetrics().getDescent();
            updated = true;
        }
        dx = (position == BubblePosition.NE || position == BubblePosition.SE) ? x : x - dw;
        dy = (position == BubblePosition.SW || position == BubblePosition.SE) ? y : y - dh;
        if (locked) {
            dx = (dx - GUI.graph.canvas.offX) / GUI.graph.canvas.zoom;
            dy = (dy - GUI.graph.canvas.offY) / GUI.graph.canvas.zoom;
            g.translate((x - GUI.graph.canvas.offX) / GUI.graph.canvas.zoom,
                    (y - GUI.graph.canvas.offY) / GUI.graph.canvas.zoom);
            g.scale(1.0 / Math.sqrt(GUI.graph.canvas.zoom), 1.0 / Math.sqrt(GUI.graph.canvas.zoom));
            g.translate(-(x - GUI.graph.canvas.offX) / GUI.graph.canvas.zoom,
                    -(y - GUI.graph.canvas.offY) / GUI.graph.canvas.zoom);

        } else {
            g.translate(x, y);
            g.scale(1.0 / Math.sqrt(GUI.graph.canvas.zoom), 1.0 / Math.sqrt(GUI.graph.canvas.zoom));
            g.translate(-x, -y);
        }

    }

    // pozor, zmena 
    public synchronized void addInformation(String text, long endTime) {
        list.add(new Information(text, endTime));
        updated = false;
    }

    public synchronized void updateInformation() {
        boolean paused = GUI.model.running == RunState.paused;
        for (Iterator<Information> it = list.iterator(); it.hasNext();) {
            if (it.next().dead(BubbleSet.time, paused)) {
                it.remove();
                updated = false;
            }
        }
    }

    public synchronized void defaultSettings() {
        list.clear();
    }

}

enum BubbleState {
    alive, hidden, dead
}

// ktorym smerom od x y ide tabulka
enum BubblePosition {
    NW, NE, SW, SE
}

/*enum PositionX {
    left, right
}

enum PositionY {
    up, down
}*/