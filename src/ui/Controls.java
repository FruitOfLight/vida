package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import algorithms.Player;
import enums.RunState;
import enums.ToolTarget;
import enums.ToolType;
import graph.MessageQueue;

public class Controls implements Drawable {
    JPanel panel;
    Canvas canvas;
    Player player;

    static final int gridWidth = 30;
    static final int gridSpace = 10;
    static final int gridHeight = 30;

    public Controls() {
        player = GUI.player;
        panel = new JPanel();
        canvas = new Canvas(this);
        panel.setLayout(null);
        hintLabel = new JLabel("");
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setFont(new Font(null, Font.PLAIN, 10));
        hintLabel.setSize(100, 15);
        panel.add(hintLabel);

        canvas.setSize(150, CONST.controlsHeight);
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
            player.start();
        } else if (name.equals("s_run")) {
            player.start();
        } else if (name.equals("p_pause")) {
            player.pause();
        } else if (name.equals("p_stop")) {
            player.stop();
        } else if (name.equals("p_fast")) {
            player.setSendSpeed(player.getSendSpeed() * CONST.speedFactor);
        } else if (name.equals("p_slow")) {
            player.setSendSpeed(player.getSendSpeed() / CONST.speedFactor);
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
            System.err.println("Unknown action " + name);
        }
        refresh();
        //System.out.println("clicked " + button.name);
    }

    public boolean showMe(String name) {
        if (name.equals("p_start")) {
            return player.state == RunState.paused;
        } else if (name.equals("s_run")) {
            return player.state == RunState.stopped;
        } else if (name.equals("p_pause")) {
            return player.state == RunState.running;
        } else if (name.equals("p_stop")) {
            return player.state != RunState.stopped;
        } else if (name.equals("gs_message")) {
            return player.state != RunState.stopped;
        } else {
            return true;
        }
    }

    public String getContent(String name) {
        if (name.equals("l_graph")) {
            return player.graph.getTypeString();
        } else if (name.equals("l_program")) {
            return player.model.program.name.equals("") ? "none" : player.model.program.name;
        } else if (name.equals("l_running")) {
            String s = player.getSendSpeedString(5) + " : " + player.getSendSpeedString(-5);
            if (player.state == RunState.running)
                return "Playing " + s;
            else if (player.state == RunState.paused)
                return "Paused (" + s + ")";
            else
                return "Stopped (" + s + ")";
        } else {
            return name;
        }
    }

    private Map<String, ControlElement> elements = new TreeMap<String, ControlElement>();

    public void set(String name, ControlElement element) {
        elements.put(name, element);
    }

    public ControlElement get(String name) {
        return elements.get(name);
    }

    private void setAutoTarget(ToolTarget target, ControlElement e) {
        Tool.autoTarget[target.ordinal()] = e.isVisible();
    }

    private void setAutoType(ToolType type, ControlElement e) {
        Tool.autoType[type.ordinal()] = e.isVisible();
    }

    public Tool getTool() {
        // TODO cele getTool by nemal robit controls, ale samotne tlacitka, zatial sa to vsak nemoze prerabat

        // ked nie je nic selecnute, vybera sa automaticky z tychto volieb
        setAutoTarget(ToolTarget.vertex, get("gs_vertex"));
        setAutoTarget(ToolTarget.edge, get("gs_edge"));
        setAutoTarget(ToolTarget.message, get("gs_message"));

        setAutoType(ToolType.select, get("gt_select"));
        setAutoType(ToolType.create, get("gt_create"));
        setAutoType(ToolType.delete, get("gt_delete"));
        setAutoType(ToolType.move, get("gt_move"));

        ToolType type = ToolType.any;
        ToolTarget target = ToolTarget.any;
        int value = 0;
        if (get("gs_vertex").isActive())
            target = ToolTarget.vertex;
        if (get("gs_edge").isActive())
            target = ToolTarget.edge;
        if (get("gs_message").isActive())
            target = ToolTarget.message;

        if (get("gt_select").isActive())
            type = ToolType.select;
        if (get("gt_create").isActive())
            type = ToolType.create;
        if (get("gt_delete").isActive())
            type = ToolType.delete;
        if (get("gt_move").isActive())
            type = ToolType.move;
        if (get("ms_stop").isActive()) {
            type = ToolType.speed;
            value = -2;
        }
        if (get("ms_slow").isActive()) {
            type = ToolType.speed;
            value = -1;
        }
        if (get("ms_normal").isActive()) {
            type = ToolType.speed;
            value = 0;
        }
        if (get("ms_fast").isActive()) {
            type = ToolType.speed;
            value = 1;
        }
        if (get("ms_turbo").isActive()) {
            type = ToolType.speed;
            value = 2;
        }

        return new Tool(type, target, value);
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 0, 0));
        g.drawString("fps: " + Player.afps + ":" + Player.sfps + ":" + (int) Player.fps + " mc:"
                + MessageQueue.messageCount, 10, 20);
        try {
            ((ControlLabel) get("l_graph")).refresh();
            ((ControlLabel) get("l_program")).refresh();
            ((ControlLabel) get("l_running")).refresh();
        } catch (NullPointerException e) {
            System.err.println("refreshing labels error ");
            //e.printStackTrace();
        }
    }

    Component hintElement;
    JLabel hintLabel;

    public void hintOn(Component c) {
        hintElement = c;
        hintLabel.setText(c.toString());
        int width = hintLabel.getFontMetrics(hintLabel.getFont()).stringWidth(hintLabel.getText());
        hintLabel.setSize(width, hintLabel.getHeight());
        hintLabel.setLocation(Math.max(2, c.getX() + (c.getWidth() - hintLabel.getWidth()) / 2),
                Controls.gridHeight - 2);
        hintLabel.setVisible(true);
    }

    public void hintOff(Component c) {
        if (hintElement != c)
            return;
        hintElement = null;
        hintLabel.setVisible(false);
    }
}
