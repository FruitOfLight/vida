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
import javax.swing.JPanel;

public class Controls implements Drawable {
    JPanel panel;
    Canvas canvas;
    Model model;
    JButton playButton;
    Map<String, Component> map = new TreeMap<String, Component>();

    static final int gridWidth = 24;
    static final int gridHeight = 24;

    public Controls() {
        model = GUI.model;
        panel = new JPanel();
        //panel.setBackground(new Color(200, 200, 100, 20));
        panel.setLayout(null);
        canvas = new Canvas(this);
        canvas.setLocation(CONST.controlsWidth - 200, 0);
        canvas.setSize(200, 50);
        panel.add(canvas);

        try {
            panel.add(new ControlButton(this, "run", 0, 1));
            panel.add(new ControlButton(this, "stop", 0, 1));
            panel.add(new ControlButton(this, "start", 1, 1));
            panel.add(new ControlButton(this, "pause", 1, 1));
            panel.add(new ControlButton(this, "slow", 2, 1));
            panel.add(new ControlButton(this, "fast", 3, 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        refresh();

    }

    public void refresh() {
        for (Component c : map.values()) {
            c.setVisible(false);
        }
        if (model.running == RunState.running) {
            map.get("stop").setVisible(true);
            map.get("pause").setVisible(true);
            map.get("slow").setVisible(true);
            map.get("fast").setVisible(true);
        }
        if (model.running == RunState.paused) {
            map.get("stop").setVisible(true);
            map.get("start").setVisible(true);
            map.get("slow").setVisible(true);
            map.get("fast").setVisible(true);
        }
        if (model.running == RunState.stopped) {
            map.get("run").setVisible(true);
        }
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
        //System.out.println("clicked " + button.name);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0));
        int x = 0, y = 0;
        for (int i = 0; i < 500; ++i)
            g.drawOval(x - 10 * i, y - 10 * i, 20 * i, 20 * i);
        g.drawString("fps: " + Model.afps + ":" + Model.sfps + ":" + (int) Model.fps + " mc:"
                + MessageQueue.messageCount, x + 10, 10);

    }
}

class ControlButton extends JButton implements ActionListener {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;

    ControlButton(Controls controls, String name, int x, int y) throws IOException {
        super(new ImageIcon(ImageIO.read(new File("images/gui-buttons/b_" + name + ".png"))));
        this.controls = controls;
        this.name = name;
        controls.map.put(name, this);
        this.addActionListener(this);
        this.setLocation(Controls.gridWidth * x, Controls.gridHeight * y);
        this.setSize(Controls.gridWidth, Controls.gridHeight);
        this.setVisible(true);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controls.onClick(this);
        controls.refresh();
    }
}