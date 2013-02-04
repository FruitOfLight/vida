import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 4907612881980276015L;
    Drawable element;

    public Canvas(Drawable element) {
        this.element = element;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (element != null) {
            element.draw(g);
        }
    }

    static void realDrawRect(Graphics g, double x, double y, double w, double h) {
        g.drawRect((int) (x), (int) (y), (int) (x + w) - (int) (x), (int) (y + h) - (int) (y));
    }

    static void realFillRect(Graphics g, double x, double y, double w, double h) {
        g.fillRect((int) (x), (int) (y), (int) (x + w) - (int) (x), (int) (y + h) - (int) (y));
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
}
