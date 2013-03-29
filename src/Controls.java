import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Controls implements Drawable {
    JPanel panel;
    Canvas canvas;
    Model model;
    Map<String, Component> map = new TreeMap<String, Component>();

    static final int gridWidth = 30;
    static final int gridSpace = 10;
    static final int gridHeight = 30;

    public Controls() {
        model = GUI.model;
        panel = new JPanel();
        canvas = new Canvas(this);
        //panel.setBackground(new Color(200, 200, 100, 20));
        panel.setLayout(null);
        hintLabel = new JLabel("");
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setFont(new Font(null, Font.PLAIN, 10));
        hintLabel.setSize(100, 10);
        panel.add(hintLabel);

        try {
            panel.add(new ControlButton(this, "s_run", 0, 1));
            panel.add(new ControlButton(this, "p_stop", 0, 1));
            panel.add(new ControlButton(this, "p_start", 1, 1));
            panel.add(new ControlButton(this, "p_pause", 1, 1));
            panel.add(new ControlButton(this, "s_load", 1, 1));
            panel.add(new ControlButton(this, "s_settings", 2, 1));
            panel.add(new ControlButton(this, "p_slow", 2, 1));
            panel.add(new ControlButton(this, "p_fast", 3, 1));
            panel.add(new ControlButton(this, "v_bubble", 11, 1, CBtype.toggle));

            panel.add(new ControlButton(this, "g_new", 0, 0));
            panel.add(new ControlButton(this, "g_open", 1, 0));
            panel.add(new ControlButton(this, "g_save", 2, 0));

            panel.add(new ControlLabel(this, "graph", 3, 0, 8));
            panel.add(new ControlLabel(this, "program", 3, 1, 8));
            panel.add(new ControlLabel(this, "running", 4, 1, 7));

        } catch (IOException e) {
            e.printStackTrace();
        }
        canvas.setSize(300, CONST.controlsHeight);
        panel.add(canvas);
        refresh();
    }

    static final String[] notStoppedVisible = { "b_p_stop", "b_p_fast", "b_p_slow", "b_v_bubble",
            "l_running" };
    static final String[] stoppedVisible = { "b_s_run", "b_s_load", "b_s_settings", "b_v_bubble",
            "b_g_new", "b_g_open", "b_g_save", "l_graph", "l_program" };

    public void refresh() {
        for (Component c : map.values()) {
            c.setVisible(false);
        }
        if (model.running == RunState.stopped) {
            for (String s : stoppedVisible) {
                if (map.get(s) == null)
                    System.err.println(s + " not found");
                map.get(s).setVisible(true);
            }
            ((JLabel) map.get("l_program")).setText((model.programName.equals("")) ? "none"
                    : model.programName);
            ((JLabel) map.get("l_graph")).setText(GUI.graph.getTypeString());
            ((JLabel) map.get("l_running"))
                    .setText("Stopped (" + model.getSendSpeedString(5) + ")");
        } else {
            for (String s : notStoppedVisible) {
                if (map.get(s) == null)
                    System.err.println(s + " not found");
                map.get(s).setVisible(true);
            }
        }

        if (model.running == RunState.running) {
            map.get("b_p_pause").setVisible(true);
            ((JLabel) map.get("l_running")).setText("Playing " + model.getSendSpeedString(5));
        }
        if (model.running == RunState.paused) {
            map.get("b_p_start").setVisible(true);
            ((JLabel) map.get("l_running")).setText("Paused (" + GUI.model.getSendSpeedString(5)
                    + ")");
        }
        canvas.setLocation(panel.getWidth() - canvas.getWidth(), 0);
        canvas.repaint();
    }

    public void onClick(ControlButton button) {
        onClick(button.name);
    }

    public void onClick(String name) {
        if (name == "p_start") {
            GUI.model.start();
        } else if (name == "s_run") {
            GUI.model.start();
        } else if (name == "p_pause") {
            GUI.model.pause();
        } else if (name == "p_stop") {
            GUI.model.stop();
        } else if (name == "p_fast") {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() * CONST.speedFactor);
        } else if (name == "p_slow") {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() / CONST.speedFactor);
        } else if (name == "s_load") {
            Menu.performAction(2, 0);
        } else if (name == "s_settings") {
            Menu.performAction(2, 1);
        } else if (name == "g_open") {
            Menu.performAction(1, 1);
        } else if (name == "g_save") {
            Menu.performAction(1, 2);
        } else if (name == "g_new") {
            Menu.performAction(1, 0);
        } else {
            System.out.println("Unknown action " + name);
        }
        //System.out.println("clicked " + button.name);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0));
        g.drawString("fps: " + Model.afps + ":" + Model.sfps + ":" + (int) Model.fps + " mc:"
                + MessageQueue.messageCount, 150, 20);
    }

    Component hintElement;
    JLabel hintLabel;

    public void hintOn(Component c) {
        hintElement = c;
        hintLabel.setText(c.toString());
        hintLabel.setLocation(c.getX() + (c.getWidth() - hintLabel.getWidth()) / 2,
                Controls.gridHeight);
        hintLabel.setVisible(true);
        //System.out.println("label " + hintLabel.getX() + " " + hintLabel.getY());
    }

    public void hintOff(Component c) {
        if (hintElement != c)
            return;
        hintElement = null;
        hintLabel.setVisible(false);
    }

}

class ControlButton extends JButton implements ActionListener, MouseListener {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;
    CBtype type;

    ControlButton(Controls controls, String name, int x, int y, CBtype type) throws IOException {
        super();
        String t = (type == CBtype.toggle) ? "-inactive" : "";
        this.setIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons" + t + "/" + "b_"
                + name + ".png"))));
        this.setRolloverIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons" + t
                + "-hover/" + "b_" + name + ".png"))));
        this.setPressedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-pressed/"
                + "b_" + name + ".png"))));
        if (type == CBtype.toggle) {
            this.setSelectedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-active/"
                    + "b_" + name + ".png"))));
            this.setRolloverSelectedIcon(new ImageIcon(ImageIO.read(new File(
                    "images/gui-buttons-active-hover/" + "b_" + name + ".png"))));
        }
        this.controls = controls;
        this.name = name;
        this.type = type;
        controls.map.put("b_" + name, this);
        this.addActionListener(this);
        this.addMouseListener(this);
        this.setLocation(Controls.gridWidth * x, (Controls.gridHeight + Controls.gridSpace) * y);
        this.setSize(Controls.gridWidth, Controls.gridHeight);
        this.setVisible(true);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.setFocusable(false);
    }

    ControlButton(Controls controls, String name, int x, int y) throws IOException {
        this(controls, name, x, y, CBtype.normal);
    }

    @Override
    public String toString() {
        String s[] = name.split("_");
        return s[s.length - 1];
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (type == CBtype.toggle)
            setSelected(!isSelected());
        controls.onClick(this);
        controls.refresh();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        controls.hintOn(this);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        controls.hintOff(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}

enum CBtype {
    normal, toggle;
}

class ControlLabel extends JLabel {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;

    ControlLabel(Controls controls, String name, int x, int y, int width) throws IOException {
        this.controls = controls;
        this.name = name;
        controls.map.put("l_" + name, this);
        this.setLocation(Controls.gridWidth * x, (Controls.gridHeight + Controls.gridSpace) * y);
        this.setSize(width * Controls.gridWidth, Controls.gridHeight);
        this.setVisible(true);
        this.setBorder(BorderFactory.createEmptyBorder());
    }
}
