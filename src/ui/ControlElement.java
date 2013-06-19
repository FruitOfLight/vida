package ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

public interface ControlElement {
    public Component getComponent();

    public int place(boolean visible, int x, int y);

    public boolean isActive();

    public boolean isVisible();
}

class ControlBuilder {
    static private Map<String, ControlElement> map = new TreeMap<String, ControlElement>();

    static public void build(Controls c) {
        try {
            // netreba sa zlaknut, ze je toto strasne zlozite - neskor sa to bude loadovat z xml-ka
            add("topBox", new ControlBox(c));
            add("bottomBox", new ControlBox(c));
            add("runButton", new ControlClickButton(c, "s_run", KeyEvent.VK_R));
            add("stopButton", new ControlClickButton(c, "p_stop", KeyEvent.VK_R));
            getBox("bottomBox").addElement(get("runButton"));
            getBox("bottomBox").addElement(get("stopButton"));
            ControlBox box, box2;
            ControlSwitchButton csb;
            box = new ControlBox(c, get("runButton"));
            box.addElement(new ControlSeparator(c, "thin_dark"));
            box.addElement(new ControlClickButton(c, "s_load", null));
            box.addElement(new ControlClickButton(c, "s_settings", null));
            box.addElement(new ControlLabel(c, "program", 8));
            getBox("bottomBox").addElement(box);
            box = new ControlBox(c, get("stopButton"));
            box.addElement(new ControlSeparator(c, "thin_dark"));
            box.addElement(new ControlClickButton(c, "p_start", KeyEvent.VK_P));
            box.addElement(new ControlClickButton(c, "p_pause", KeyEvent.VK_P));
            box.addElement(new ControlClickButton(c, "p_slow", KeyEvent.VK_LEFT));
            box.addElement(new ControlClickButton(c, "p_fast", KeyEvent.VK_RIGHT));
            box.addElement(new ControlLabel(c, "running", 8));
            box.addElement(csb = new ControlSwitchButton(c, "p_auto-speed", KeyEvent.VK_1));
            csb.setSelected(true);
            box.addElement(csb = new ControlSwitchButton(c, "v_auto-pause", KeyEvent.VK_2));
            csb.setSelected(true);
            box.addElement(csb = new ControlSwitchButton(c, "v_bubble-messages", KeyEvent.VK_3));
            csb.setSelected(true);
            box.addElement(csb = new ControlSwitchButton(c, "v_bubble-all-vertices", KeyEvent.VK_4));
            csb.addRadio("bubble", 1);
            csb.setSelected(true);
            box.addElement(csb = new ControlSwitchButton(c, "v_bubble-important", KeyEvent.VK_5));
            csb.addRadio("bubble", 2);
            csb.setSelected(true);
            getBox("bottomBox").addElement(box);
            box = new ControlBox(c, get("runButton"));
            box.addElement(new ControlClickButton(c, "g_new", null));
            box.addElement(new ControlClickButton(c, "g_open", null));
            box.addElement(new ControlClickButton(c, "g_save", null));
            box.addElement(new ControlSwitchButton(c, "g_lock-type", KeyEvent.VK_L));
            box.addElement(new ControlLabel(c, "graph", 8));
            getBox("topBox").addElement(box);
            box = new ControlBox(c, null);
            box.addElement(csb = new ControlSwitchButton(c, "gs_vertex", KeyEvent.VK_V));
            csb.addRadio("selectors", 1);
            box.addElement(csb = new ControlSwitchButton(c, "gs_edge", KeyEvent.VK_E));
            csb.addRadio("selectors", 1);
            box.addElement(csb = new ControlSwitchButton(c, "gs_message", KeyEvent.VK_M));
            csb.addRadio("selectors", 1);
            getBox("topBox").addElement(box);
            add("gtoolBox", box = new ControlBox(c, null));
            getBox("topBox").addElement(box);
            box.addElement(new ControlSeparator(c, "thin_dark"));
            box.addElement(csb = new ControlSwitchButton(c, "gt_select", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            box = new ControlBox(c, get("runButton"));
            box.addElement(csb = new ControlSwitchButton(c, "gt_create", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            box.addElement(csb = new ControlSwitchButton(c, "gt_delete", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            box.addElement(csb = new ControlSwitchButton(c, "gt_move", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            box.addElement(csb = new ControlSwitchButton(c, "gt_init", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            getBox("gtoolBox").addElement(box);
            box = new ControlBox(c, get("stopButton"));
            box.addElement(csb = new ControlSwitchButton(c, "gt_move", KeyEvent.VK_T));
            csb.addRadio("tools", -1);
            add("speedBox", box2 = new ControlBox(c, null));
            box2.addElement(new ControlSeparator(c, "thin_dark"));
            box2.addElement(csb = new ControlSwitchButton(c, "ms_stop", KeyEvent.VK_S));
            csb.addRadio("tools", -1);
            box2.addElement(csb = new ControlSwitchButton(c, "ms_slow", KeyEvent.VK_S));
            csb.addRadio("tools", -1);
            box2.addElement(csb = new ControlSwitchButton(c, "ms_normal", KeyEvent.VK_S));
            csb.addRadio("tools", -1);
            box2.addElement(csb = new ControlSwitchButton(c, "ms_fast", KeyEvent.VK_S));
            csb.addRadio("tools", -1);
            box2.addElement(csb = new ControlSwitchButton(c, "ms_turbo", KeyEvent.VK_S));
            csb.addRadio("tools", -1);
            box.addElement(box2);
            getBox("gtoolBox").addElement(box);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void refresh() {
        get("bottomBox").place(true, 0, Controls.gridHeight + Controls.gridSpace);
        get("topBox").place(true, 0, 0);
    }

    public static ControlElement get(String s) {
        return map.get(s);
    }

    public static ControlBox getBox(String s) {
        return (ControlBox) map.get(s);
    }

    public static ControlElement add(String s, ControlElement e) {
        return map.put(s, e);
    }
}

class ControlBox implements ControlElement {
    private ArrayList<ControlElement> elements = new ArrayList<ControlElement>();
    private ControlElement look = null;
    private Controls controls;

    public ControlBox(Controls controls) {
        this.controls = controls;
    }

    public ControlBox(Controls controls, ControlElement look) {
        this(controls);
        this.look = look;
    }

    public void addElement(ControlElement e) {
        if (e.getComponent() != null)
            controls.panel.add(e.getComponent());
        elements.add(e);
    }

    public void addLook(ControlElement e) {
        look = e;
    }

    @Override
    public boolean isActive() {
        return (look == null || look.isActive());
    }

    @Override
    public boolean isVisible() {
        return isActive();
    }

    @Override
    public int place(boolean visible, int x, int y) {
        visible = visible && isActive();
        for (ControlElement e : elements) {
            x = e.place(visible, x, y);
        }
        return x;
    }

    @Override
    public Component getComponent() {
        return null;
    }

}

class ControlClickButton extends JButton implements ControlElement, ActionListener, MouseListener {
    private static final long serialVersionUID = 8676998593915111855L;
    Controls controls;
    String name;
    Integer key;

    ControlClickButton(Controls controls, String name, String imageName, Integer key)
            throws IOException {
        super();
        this.setIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons/" + "b_" + imageName
                + ".png"))));
        this.setRolloverIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-hover/" + "b_"
                + imageName + ".png"))));
        this.setPressedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-pressed/"
                + "b_" + imageName + ".png"))));
        this.controls = controls;
        this.name = name;
        this.key = key;
        this.addActionListener(this);
        this.addMouseListener(this);
        this.setSize(Controls.gridWidth, Controls.gridHeight);

        this.setVisible(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.setFocusable(false);

        controls.set(name, this);
        if (key != null)
            GUI.gkl.addButton(key, this);
    }

    ControlClickButton(Controls controls, String name, Integer key) throws IOException {
        this(controls, name, name, key);
    }

    @Override
    public String toString() {
        String s[] = name.split("_");
        return s[s.length - 1] + ((key == null) ? "" : " (" + KeyEvent.getKeyText(key) + ")");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controls.onClick(name);
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

    @Override
    public int place(boolean visible, int x, int y) {
        visible &= controls.showMe(name);
        setVisible(visible);
        if (visible) {
            controls.set(name, this);
            setLocation(x, y);
        }
        return (visible ? x + getWidth() : x);
    }

    @Override
    public boolean isActive() {
        return isVisible() && controls.showMe(name);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}

class ControlSwitchButton extends ControlClickButton {
    private static final long serialVersionUID = -2917209616766738855L;
    static private Map<String, ArrayList<ControlSwitchButton>> radioGroups = new TreeMap<String, ArrayList<ControlSwitchButton>>();

    String bindingToggle;
    private String radioName = null;
    int radioLevel = 0;
    private int radioPos = 0;

    public ControlSwitchButton(Controls controls, String name, String imageName, Integer key)
            throws IOException {
        super(controls, name, imageName, key);
        this.setIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-inactive/" + "b_"
                + imageName + ".png"))));
        this.setRolloverIcon(new ImageIcon(ImageIO.read(new File(
                "images/gui-buttons-inactive-hover/" + "b_" + imageName + ".png"))));
        this.setPressedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-pressed/"
                + "b_" + imageName + ".png"))));
        this.setSelectedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-active/"
                + "b_" + imageName + ".png"))));
        this.setRolloverSelectedIcon(new ImageIcon(ImageIO.read(new File(
                "images/gui-buttons-active-hover/" + "b_" + imageName + ".png"))));

    }

    public ControlSwitchButton(Controls controls, String name, Integer key) throws IOException {
        this(controls, name, name, key);
    }

    public void addRadio(String name, int level) {
        radioName = name;
        radioLevel = level;
        if (radioLevel == -1)
            GUI.gkl.addBid(key);
        if (!radioGroups.containsKey(radioName))
            radioGroups.put(radioName, new ArrayList<ControlSwitchButton>());
        radioPos = radioGroups.get(radioName).size();
        radioGroups.get(radioName).add(this);

    }

    public ControlSwitchButton getRadioPrevious() {
        int s = radioGroups.get(radioName).size();
        return radioGroups.get(radioName).get((radioPos + s - 1) % s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        setSelected(!isSelected());
        if (radioName != null) {
            for (ControlSwitchButton button : radioGroups.get(radioName))
                if (!name.equals(button.name)) {
                    if (button.radioLevel == radioLevel && isSelected() && button.isSelected())
                        button.setSelected(false);
                    if (button.radioLevel > radioLevel && isSelected() && !button.isSelected())
                        button.setSelected(true);
                    if (button.radioLevel < radioLevel && !isSelected() && button.isSelected())
                        button.setSelected(false);
                }
        }
    }

    @Override
    public boolean isActive() {
        return isVisible() && isSelected();
    }
}

class ControlLabel extends JLabel implements ControlElement {
    private static final long serialVersionUID = 8676998593915111855L;
    String name;
    Controls controls;

    ControlLabel(Controls controls, String name, int width) throws IOException {
        this.controls = controls;
        this.name = "l_" + name;
        this.setSize(width * Controls.gridWidth, Controls.gridHeight);
        this.setVisible(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        controls.set(this.name, this);
    }

    @Override
    public int place(boolean visible, int x, int y) {
        refresh();
        setVisible(visible);
        if (visible)
            setLocation(x, y);
        return (visible ? x + getWidth() : x);
    }

    @Override
    public boolean isActive() {
        return isVisible();
    }

    public void refresh() {
        setText(controls.getContent(name));
    }

    @Override
    public Component getComponent() {
        return this;
    }
}

class ControlSeparator extends JLabel implements ControlElement {
    private static final long serialVersionUID = -4919973923314221708L;
    Controls controls;

    public ControlSeparator(Controls controls, String style) throws IOException {
        this.controls = controls;
        this.setIcon(new ImageIcon(ImageIO
                .read(new File("images/gui-separator/s_" + style + ".png"))));
        this.setSize(4, Controls.gridHeight);
        this.setVisible(false);
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public int place(boolean visible, int x, int y) {
        setVisible(visible);
        if (visible)
            setLocation(x - getWidth() / 2 + 1, y);
        return x;
    }

    @Override
    public boolean isActive() {
        return isVisible();
    }
}