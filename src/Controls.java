import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Controls implements Drawable {
    JPanel panel;
    Canvas canvas;
    Model model;
    Map<String, Component> map = new TreeMap<String, Component>();

    static final int gridWidth = 30;
    static final int gridHeight = 30;

    public Controls() {
        model = GUI.model;
        panel = new JPanel();
        canvas = new Canvas(this);
        //panel.setBackground(new Color(200, 200, 100, 20));
        panel.setLayout(null);

        try {
            panel.add(new ControlButton(this, "run", 0, 1));
            panel.add(new ControlButton(this, "stop", 0, 1));
            panel.add(new ControlButton(this, "start", 1, 1));
            panel.add(new ControlButton(this, "pause", 1, 1));
            panel.add(new ControlButton(this, "slow", 2, 1));
            panel.add(new ControlButton(this, "fast", 3, 1));
            panel.add(new ControlButton(this, "p_settings", "b_settings", 1, 1));
            panel.add(new ControlButton(this, "p_open", "b_open", 2, 1));
            panel.add(new ControlButton(this, "p_save", "b_save", 3, 1));
            panel.add(new ControlButton(this, "g_new", "b_new", 1, 0));
            panel.add(new ControlButton(this, "g_open", "b_open", 2, 0));
            panel.add(new ControlButton(this, "g_save", "b_save", 3, 0));

            panel.add(new ControlLabel(this, "label1", 4, 1, 8));

            /*panel.add(new ControlButton(this, "run", 0, 1));
            panel.add(new ControlButton(this, "stop", 1, 1));
            panel.add(new ControlButton(this, "start", 2, 1));
            panel.add(new ControlButton(this, "pause", 3, 1));
            panel.add(new ControlButton(this, "slow", 4, 1));
            panel.add(new ControlButton(this, "fast", 5, 1));*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        canvas.setSize(300, CONST.controlsHeight);
        panel.add(canvas);
        refresh();
    }

    public void refresh() {
        for (Component c : map.values()) {
            c.setVisible(false);
        }
        String label1text = "";
        if (model.running == RunState.running) {
            map.get("b_stop").setVisible(true);
            map.get("b_pause").setVisible(true);
            map.get("b_slow").setVisible(true);
            map.get("b_fast").setVisible(true);
            label1text = ((Double) GUI.model.getSendSpeed()).toString();
            if (label1text.length() > 5)
                label1text = label1text.substring(0, 5);
            label1text = "Playing " + label1text;
        }
        if (model.running == RunState.paused) {
            map.get("b_stop").setVisible(true);
            map.get("b_start").setVisible(true);
            map.get("b_slow").setVisible(true);
            map.get("b_fast").setVisible(true);
            label1text = ((Double) GUI.model.getSendSpeed()).toString();
            if (label1text.length() > 5)
                label1text = label1text.substring(0, 5);
            label1text = "Paused (" + label1text + ")";
        }
        if (model.running == RunState.stopped) {
            map.get("b_run").setVisible(true);
            map.get("b_p_open").setVisible(true);
            map.get("b_p_save").setVisible(true);
            map.get("b_p_settings").setVisible(true);
            map.get("b_g_open").setVisible(true);
            map.get("b_g_save").setVisible(true);
            map.get("b_g_new").setVisible(true);
            String s[] = model.path.split("/");
            label1text = (model.path.equals("")) ? "none" : s[s.length - 1];
        }
        map.get("l_label1").setVisible(true);
        ((JLabel) map.get("l_label1")).setText(label1text);
        canvas.setLocation(panel.getWidth() - canvas.getWidth(), 0);
        canvas.repaint();
    }

    public void onClick(ControlButton button) {
        onClick(button.name);
    }

    public void onClick(String name) {
        if (name == "start") {
            GUI.model.start();
        }
        if (name == "run") {
            GUI.model.start();
        }
        if (name == "pause") {
            GUI.model.pause();
        }
        if (name == "stop") {
            GUI.model.stop();
            GUI.graph.setDefaultValues();
        }
        if (name == "fast") {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() * CONST.speedFactor);
        }
        if (name == "slow") {
            GUI.model.setSendSpeed(GUI.model.getSendSpeed() / CONST.speedFactor);
        }
        if (name == "p_open") {
            Menu.performAction(2, 0);
        }
        if (name == "p_save") {

        }
        if (name == "p_settings") {
            Menu.performAction(2, 1);
        }
        if (name == "g_open") {
            Menu.performAction(1, 1);
        }
        if (name == "g_save") {
            Menu.performAction(1, 2);
        }
        if (name == "g_new") {
            Menu.performAction(1, 0);
        }

        //System.out.println("clicked " + button.name);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0));
        //int x = 0, y = 0;
        //for (int i = 0; i < 500; ++i)
        //    g.drawOval(x - 10 * i, y - 10 * i, 20 * i, 20 * i);
        /*String programString = "";
        if (GUI.model.path.equals(""))
            programString = "none";
        else {
            int last = 0;
            for (int i = 0; i < GUI.model.path.length(); i++)
                if (GUI.model.path.charAt(i) == '/')
                    last = i;
            programString = GUI.model.path.substring(last + 1,
                    Math.max(GUI.model.path.length() - 4, last + 1));
        }
        String speedString = ((Double) GUI.model.getSendSpeed()).toString();
        if (speedString.length() > 5)
            speedString = speedString.substring(0, 5);
        if (model.running == RunState.running)
            speedString = "Running " + speedString;
        if (model.running == RunState.paused)
            speedString = "Paused (" + speedString + ")";
        if (model.running == RunState.stopped)
            speedString = "Stopped (" + speedString + ")";
        g.drawString(speedString, 10, 20);
        g.drawString("" + programString, 10, 40);*/
        g.drawString("fps: " + Model.afps + ":" + Model.sfps + ":" + (int) Model.fps + " mc:"
                + MessageQueue.messageCount, 150, 20);

    }
}

class ControlButton extends JButton implements ActionListener {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;

    ControlButton(Controls controls, String name, String filename, int x, int y) throws IOException {
        super(new ImageIcon(ImageIO.read(new File("images/gui-buttons/" + filename + ".png"))));
        this.controls = controls;
        this.name = name;
        controls.map.put("b_" + name, this);
        this.addActionListener(this);
        this.setLocation(Controls.gridWidth * x, Controls.gridHeight * y);
        this.setSize(Controls.gridWidth, Controls.gridHeight);
        this.setVisible(true);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
    }

    ControlButton(Controls controls, String name, int x, int y) throws IOException {
        this(controls, name, "b_" + name, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controls.onClick(this);
        controls.refresh();
    }
}

class ControlLabel extends JLabel {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;

    ControlLabel(Controls controls, String name, int x, int y, int width) throws IOException {
        this.controls = controls;
        this.name = name;
        controls.map.put("l_" + name, this);
        this.setLocation(Controls.gridWidth * x, Controls.gridHeight * y);
        this.setSize(width * Controls.gridWidth, Controls.gridHeight);
        this.setVisible(true);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

}
