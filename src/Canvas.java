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

}
