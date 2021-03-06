package ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JPanel;

import enums.Constrast;
import enums.Preference;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 4907612881980276015L;
    private Drawable element;
    // pozicia
    public double offX, offY, zoom;
    Color repaintColor;
    //
    public static Stroke boldStroke, normalStroke, thinStroke;

    public Canvas(Drawable element) {
        super();
        repaintColor = new Color(255, 255, 255);
        this.element = element;
        offX = offY = 0;
        zoom = 1.0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintComponent((Graphics2D) g);
    }

    protected void paintComponent(Graphics2D g) {
        super.paintComponent(g);

        boldStroke = new BasicStroke((float) Math.max(4.0 / Math.sqrt(zoom), 4.0 / zoom));
        normalStroke = new BasicStroke((float) (1.6 / Math.sqrt(zoom)));
        thinStroke = new BasicStroke((float) (0.8 / Math.sqrt(zoom)));

        g.setStroke(new BasicStroke((float) 1.0));
        g.setColor(repaintColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.setStroke(thinStroke);

        // TODO bug report, pri starte je canvas nejak divne posunuty netusim preco
        // TODO bug report, ked sa odzmaze vrchol a spusti sa to, tak si to mysli, ze tam vrchol stale je

        if (element != null) {
            AffineTransform oldTransform = g.getTransform();
            g.translate(offX, offY);
            g.scale(zoom, zoom);
            element.draw(g);
            g.setTransform(oldTransform);
        }

    }

    public static Color contrastColor(Color color, Constrast type) {
        if (type == Constrast.invert) {
            return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
        }
        if (type == Constrast.textbw) {
            // nasledovne konstanty nie su nahodne, neodporucam menit :)
            int value = color.getRed() * 299 + color.getGreen() * 587 + color.getBlue() * 114;
            if (value >= 128000)
                return Color.black;
            else
                return Color.white;
        }
        if (type == Constrast.borderbw) {
            // tu sa da s konstantami trochu pohrat
            int value = color.getRed() + color.getGreen() + color.getBlue() + 2;
            if (value > 200)
                return Color.black;
            else
                return Color.blue;
        }

        return Color.black;
    }

    public static String shorten(Graphics g, String text, int width, Preference preference) {
        if (g.getFontMetrics().stringWidth(text) <= width)
            return text;
        if (preference == Preference.begin) {
            if (g.getFontMetrics().stringWidth("..") > width)
                return "";
            int len = 0;
            while (g.getFontMetrics().stringWidth(text.substring(0, len + 1) + "..") <= width)
                len++;
            return text.substring(0, len) + "..";
        }
        if (preference == Preference.end) {
            if (g.getFontMetrics().stringWidth("..") > width)
                return "";
            int len = 0;
            while (g.getFontMetrics().stringWidth(".." + text.substring(text.length() - len)) <= width)
                len++;
            return ".." + text.substring(text.length() - len);
        }
        return "";
    }

    public static String[] shortenWrap(Graphics g, String text, int width, String regex) {
        if (g.getFontMetrics().stringWidth(text) <= width) {
            String[] line = new String[1];
            line[0] = text.trim();
            return line;
        }
        String[] lines = text.split(regex);
        for (int i = 0; i < lines.length; ++i)
            lines[i] = shorten(g, lines[i].trim(), width, Preference.begin);
        return lines;
    }

    public static String[] multiGet(Graphics g, String text, int width) {
        ArrayList<String> words = new ArrayList<String>();
        int from = 0, to;
        while ((from = text.indexOf("{", from)) != -1) {
            from++;
            to = text.indexOf("}", from);
            if (to != -1)
                words.add(shorten(g, text.substring(from, to), width, Preference.begin));
        }
        return (String[]) words.toArray(new String[words.size()]);
    }

    public static double vectorProduct(double x1, double y1, double x2, double y2) {
        return x1 * y2 - x2 * y1;
    }

    public static double scalarProduct(double x1, double y1, double x2, double y2) {
        return x1 * x2 + y1 * y2;
    }

    public static double scalarProduct(double x, double y) {
        return x * x + y * y;
    }
}
