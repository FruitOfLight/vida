package graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import ui.Canvas;
import ui.Drawable;
import ui.GUI;
import ui.Tool;
import enums.Constrast;
import enums.InitType;
import enums.RunState;
import enums.ToolType;

public class RandomInit implements Drawable {
    private int initial;

    public int getInitial() {
        return initial;
    }

    public void toggleInitial() {
        setInitial((getInitial() == 0) ? 1 : 0);
    }

    public void setInitial(int i) {
        if (i != 0) {
            while (Vertex.initialSet.size() > 0)
                Vertex.initialSet.first().setInitial(0);
        }
        autoInitial();
    }

    public void autoInitial() {
        initial = 0;
        if (Vertex.initialSet.size() == 0 && GUI.player.model.settings.getInit() != InitType.no) {
            initial = 1;
        }
    }

    private Color color;
    private double x, y, radius;

    public RandomInit() {
        color = new Color(0, 255, 0);
        autoInitial();
        radius = 0.;
        x = y = 0.0;
    }

    public boolean active() {
        if (GUI.player.state != RunState.stopped)
            return false;
        Tool tool = GUI.controls.getTool();
        if (tool.type.equals(ToolType.init) && GUI.player.model.settings.getInit() != InitType.no) {
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics2D g) {
        if (active()) {
            Canvas canvas = GUI.player.graph.canvas;
            radius = 50. * Math.sqrt(1.0 / canvas.zoom);
            x = (radius * canvas.zoom + 10 - canvas.offX) / canvas.zoom;
            y = (radius * canvas.zoom + 10 - canvas.offY) / canvas.zoom;

            Shape shape = new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            if (getInitial() != 0) {
                shape = new Rectangle2D.Double(x - radius, y - radius, 2 * radius, 2 * radius);
            }
            g.setColor(color);
            g.fill(shape);
            g.setColor(Canvas.contrastColor(color, Constrast.borderbw));
            g.draw(shape);
            g.setColor(Canvas.contrastColor(color, Constrast.textbw));

            g.setFont(new Font(Font.DIALOG, Font.PLAIN, (int) (13 * Math.sqrt(1.0 / canvas.zoom))));
            /*String caption = Canvas.shorten(g, ((Integer) ID).toString(), (int) (radius * 2),
                    Preference.begin);*/
            String[] captions = { "Choose", "random", "at start",
                    "(Mode: " + GUI.player.model.settings.getInit().toString() + ")" };
            for (int i = 0; i < captions.length; ++i) {
                g.drawString(captions[i],
                        (float) (x - g.getFontMetrics().stringWidth(captions[i]) / 2), (float) (y
                                + g.getFontMetrics().getAscent() / 2 + (i - 2)
                                * g.getFontMetrics().getHeight()));
            }
        }
    }

    // ci je bod v gulicke vrchola
    public boolean isOnPoint(double x1, double y1) {
        return active() && ((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y) < (radius) * (radius));
    }
}
