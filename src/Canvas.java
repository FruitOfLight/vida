import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Canvas extends JPanel {

    Drawable element;

    public Canvas(Drawable element) {
        this.element = element;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 25, 500, 500);

        if (element != null)
            element.draw(g);
    }

}
