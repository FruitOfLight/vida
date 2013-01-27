import java.awt.Color;
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
        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, getSize().width, getSize().height);
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

}
