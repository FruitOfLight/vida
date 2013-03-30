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
}

class ControlBuilder {
    static private Map<String, ControlElement> map = new TreeMap<String, ControlElement>();

    static public void build(Controls c) {
        try {
            add("topBox", new ControlBox(c));
            add("bottomBox", new ControlBox(c));
            add("playButton", new ControlClickButton(c, "s_run", KeyEvent.VK_R));
            add("stopButton", new ControlClickButton(c, "p_stop", KeyEvent.VK_R));
            getBox("bottomBox").addElement(get("playButton"));
            getBox("bottomBox").addElement(get("stopButton"));
            ControlBox box;
            box = new ControlBox(c, get("playButton"));
            box.addElement(new ControlClickButton(c, "s_load", null));
            box.addElement(new ControlClickButton(c, "s_settings", null));
            box.addElement(new ControlLabel(c, "program", 8));
            box.addElement(new ControlSwitchButton(c, "v_bubble", null));
            getBox("bottomBox").addElement(box);
            box = new ControlBox(c, get("stopButton"));
            box.addElement(new ControlClickButton(c, "p_start", null));
            box.addElement(new ControlClickButton(c, "p_pause", null));
            box.addElement(new ControlClickButton(c, "p_slow", null));
            box.addElement(new ControlClickButton(c, "p_fast", null));
            box.addElement(new ControlLabel(c, "running", 8));
            box.addElement(new ControlSwitchButton(c, "v_bubble", null));
            getBox("bottomBox").addElement(box);
            box = new ControlBox(c, get("playButton"));
            box.addElement(new ControlClickButton(c, "g_new", null));
            box.addElement(new ControlClickButton(c, "g_open", null));
            box.addElement(new ControlClickButton(c, "g_save", null));
            box.addElement(new ControlLabel(c, "graph", 8));
            getBox("topBox").addElement(box);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void refresh() {
        get("topBox").place(true, 0, 0);
        get("bottomBox").place(true, 0, Controls.gridHeight + Controls.gridSpace);
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
    String onClick, showName, name;
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
        this.showName = this.onClick = this.name = name;
        this.key = key;
        this.addActionListener(this);
        this.addMouseListener(this);
        this.setSize(Controls.gridWidth, Controls.gridHeight);

        this.setVisible(false);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.setFocusable(false);
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
        controls.onClick(onClick);
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
        visible &= controls.showMe(showName);
        setVisible(visible);
        if (visible)
            setLocation(x, y);
        return (visible ? x + getWidth() : x);
    }

    @Override
    public boolean isActive() {
        return isVisible() && controls.showMe(showName);
    }

    @Override
    public Component getComponent() {
        return this;
    }
}

class ControlSwitchButton extends ControlClickButton {
    private static final long serialVersionUID = -2917209616766738855L;
    String bindingToggle;
    ArrayList<ControlSwitchButton> deactivateList = new ArrayList<ControlSwitchButton>();

    public ControlSwitchButton(Controls controls, String name, Integer key, boolean kweak)
            throws IOException {
        super(controls, name, key);
        this.setIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-inactive/" + "b_"
                + name + ".png"))));
        this.setRolloverIcon(new ImageIcon(ImageIO.read(new File(
                "images/gui-buttons-inactive-hover/" + "b_" + name + ".png"))));
        this.setPressedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-pressed/"
                + "b_" + name + ".png"))));
        this.setSelectedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-active/"
                + "b_" + name + ".png"))));
        this.setRolloverSelectedIcon(new ImageIcon(ImageIO.read(new File(
                "images/gui-buttons-active-hover/" + "b_" + name + ".png"))));
    }

    public ControlSwitchButton(Controls controls, String name, Integer key) throws IOException {
        this(controls, name, key, false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        setSelected(!isSelected());
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
    }

    @Override
    public int place(boolean visible, int x, int y) {
        setText(controls.getContent(name));
        setVisible(visible);
        if (visible)
            setLocation(x, y);
        return (visible ? x + getWidth() : x);
    }

    @Override
    public boolean isActive() {
        return isVisible();
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
