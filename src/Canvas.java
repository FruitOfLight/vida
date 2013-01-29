import java.awt.Graphics;
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
    
    static void realDrawRect(Graphics g, double x, double y, double w, double h){
        g.drawRect((int)(x), (int)(y), (int)(x+w)-(int)(x), (int)(y+h)-(int)(y));
    }
    static void realFillRect(Graphics g, double x, double y, double w, double h){
        g.fillRect((int)(x), (int)(y), (int)(x+w)-(int)(x), (int)(y+h)-(int)(y));
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

}
