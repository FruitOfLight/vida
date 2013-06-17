package ui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class PopupPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 4907612881980276015L;
    public Component element;
    private boolean lockShow;

    public PopupPanel(Component element_) {
        this.element = element_;
        lockShow = false;
        this.addMouseListener(this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (lockShow)
            return;
        if (element.isVisible())
            element.setVisible(false);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (lockShow)
            return;
        if (!element.isVisible())
            element.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (lockShow) {
            lockShow = false;
            element.setVisible(false);
        } else {
            lockShow = true;
            element.setVisible(true);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 255, 255));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, this.getWidth(), this.getHeight());
    }

}
