package graph;

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

import ui.Canvas;
import ui.Drawable;
import ui.GUI;

import enums.BubblePosition;
import enums.BubbleState;
import enums.Constrast;
import enums.RunState;

public class Bubble implements Drawable {
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
    Graph graph;
    private LinkedList<Information> list;
    private boolean updated;
    private int maxWidth;

    public void setMaxWidth(int width) {
        maxWidth = width;
        updated = false;
    }

    public float transparency;
    boolean locked;
    public BubblePosition position;
    ArrayList<String> lines;

    private double x, y, dx, dy, dw, dh, dr = 8;

    public Bubble(double x, double y) {
        graph = GUI.player.graph;
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
    public void draw(Graphics2D g) {
        updateInformation();
        if (list.size() == 0)
            return;
        if (!GUI.controls.get("v_bubble-all-vertices").isActive())
            return;
        float alpha = transparency * (isOnPoint(graph.mousex, graph.mousey) ? 0.5f : 1.0f);
        if (alpha < 0.01)
            return;
        Composite originalComposite = g.getComposite();
        if (alpha < 0.99) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }
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
        if (alpha < 0.99) {
            g.setComposite(originalComposite);
        }
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
            dx = (dx - graph.canvas.offX) / graph.canvas.zoom;
            dy = (dy - graph.canvas.offY) / graph.canvas.zoom;
            g.translate((x - graph.canvas.offX) / graph.canvas.zoom, (y - graph.canvas.offY)
                    / graph.canvas.zoom);
            g.scale(1.0 / Math.sqrt(graph.canvas.zoom), 1.0 / Math.sqrt(graph.canvas.zoom));
            g.translate(-(x - graph.canvas.offX) / graph.canvas.zoom, -(y - graph.canvas.offY)
                    / graph.canvas.zoom);

        } else {
            g.translate(x, y);
            g.scale(1.0 / Math.sqrt(graph.canvas.zoom), 1.0 / Math.sqrt(graph.canvas.zoom));
            g.translate(-x, -y);
        }

    }

    // pozor, zmena 
    public synchronized void addInformation(String text, long endTime) {
        list.add(new Information(text, endTime));
        updated = false;
    }

    public synchronized void updateInformation() {
        boolean paused = graph.player.state == RunState.paused;
        for (Iterator<Information> it = list.iterator(); it.hasNext();) {
            if (it.next().dead(BubbleSet.time, paused)) {
                it.remove();
                updated = false;
            }
        }
    }

    public boolean isOnPoint(double mx, double my) {
        if (locked) {
            mx = (mx - (x - graph.canvas.offX) / graph.canvas.zoom) * Math.sqrt(graph.canvas.zoom)
                    + (x - graph.canvas.offX) / graph.canvas.zoom;
            my = (my - (y - graph.canvas.offY) / graph.canvas.zoom) * Math.sqrt(graph.canvas.zoom)
                    + (y - graph.canvas.offY) / graph.canvas.zoom;
        } else {
            mx = (mx - x) / Math.sqrt(graph.canvas.zoom) + x;
            my = (my - y) / Math.sqrt(graph.canvas.zoom) + y;

        }
        return dx <= mx && mx <= dx + dw && dy <= my && my <= dy + dh;
    }

    public synchronized void defaultSettings() {
        list.clear();
    }

}