import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Controls implements Drawable {
    JPanel panel;
    Canvas canvas;
    Model model;

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

        canvas.setSize(300, CONST.controlsHeight);
        panel.add(canvas);
        ControlBuilder.build(this);

        refresh();
    }

    public void refresh() {
        ControlBuilder.refresh();

        canvas.setLocation(panel.getWidth() - canvas.getWidth(), 0);
        canvas.repaint();
    }

    public void onClick(ControlClickButton button) {
        onClick(button.name);
    }

    public void onClick(String name) {
        if (name.equals("p_start")) {
            GUI.model.start();
        } else if (name.equals("s_run")) {
            GUI.model.start();
        } else if (name.equals("p_pause")) {
            GUI.model.pause();
        } else if (name.equals("p_stop")) {
            GUI.model.stop();
        } else if (name.equals("p_fast")) {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() * CONST.speedFactor);
        } else if (name.equals("p_slow")) {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() / CONST.speedFactor);
        } else if (name.equals("s_load")) {
            Menu.performAction(2, 0);
        } else if (name.equals("s_settings")) {
            Menu.performAction(2, 1);
        } else if (name.equals("g_open")) {
            Menu.performAction(1, 1);
        } else if (name.equals("g_save")) {
            Menu.performAction(1, 2);
        } else if (name.equals("g_new")) {
            Menu.performAction(1, 0);
        } else {
            System.out.println("Unknown action " + name);
        }
        refresh();
        //System.out.println("clicked " + button.name);
    }

    public boolean showMe(String name) {
        if (name.equals("p_start")) {
            return model.running == RunState.paused;
        } else if (name.equals("s_run")) {
            return model.running == RunState.stopped;
        } else if (name.equals("p_pause")) {
            return model.running == RunState.running;
        } else if (name.equals("p_stop")) {
            return model.running != RunState.stopped;
        } else {
            return true;
        }
    }

    public String getContent(String name) {
        if (name.equals("l_graph")) {
            return GUI.graph.getTypeString();
        } else if (name.equals("l_program")) {
            return model.programName.equals("") ? "none" : model.programName;
        } else if (name.equals("l_running")) {
            if (model.running == RunState.running)
                return "Playing " + model.getSendSpeedString(5);
            else if (model.running == RunState.paused)
                return "Paused (" + model.getSendSpeedString(5) + ")";
            else
                return "Stopped (" + model.getSendSpeedString(5) + ")";
        } else {
            return name;
        }
    }

    /*
        private Map<String, Boolean> values = new TreeMap<String, Boolean>();

        public void setValue(String name, boolean v) {
            values.put(name, v);
        }

        public Boolean getValue(String name) {
            return values.get(name);
        }
    */
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
        hintLabel.setLocation((c.getX() + (c.getWidth() - hintLabel.getWidth()) / 2),
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
