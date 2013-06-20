package model;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;


/*
 * Trieda stvorena pre jednoduche pridavanie nastaveni roznych typov
 */
public abstract class Setting {
    public static String[] toStrings(Object[] array) {
        String[] res = new String[array.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = array[i].toString();
        }
        return res;
    }

    protected JComponent uiElement;
    protected Object value;
    protected String name;
    protected boolean locked;

    Setting(String name) {
        this.name = name;
        locked = false;
        createUiElement();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean getLocked() {
        return locked;
    }

    Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void createUiElement() {
        uiElement = new JTextField();
    }

    public JComponent updateUiElement(String string) {
        Object oldvalue = getValue();
        setString(string);
        updateUiElement();
        setValue(oldvalue);
        return uiElement;
    }

    public JComponent updateUiElement() {
        ((JTextField) uiElement).setText(getString());
        return uiElement;
    }

    public void read() {
        setString(((JTextField) uiElement).getText());
    }

    public String getString() {
        return value.toString();
    }

    // vrati, ci sa to podarilo
    boolean setString(String s) {
        // TODO ...
        value = s;
        return true;
    }
}

class BoolSetting extends Setting {
    static String[] positive = { "yes", "true", "on", AlgReader.empty };
    static String[] negative = { "no", "false", "off" };

    BoolSetting(String name) {
        super(name);
        value = false;
    }

    @Override
    boolean setString(String str) {
        for (String s : positive)
            if (str.equals(s)) {
                value = true;
                return true;
            }
        for (String s : negative)
            if (str.equals(s)) {
                value = false;
                return true;
            }
        System.err.println("Setting: boolean parse error\n");
        return false;
    }

    @Override
    public String getString() {
        return ((Boolean) value) ? "yes" : "no";
    }

    @Override
    public void createUiElement() {
        uiElement = new JCheckBox("");
    }

    @Override
    public JComponent updateUiElement() {
        ((JCheckBox) uiElement).setSelected((Boolean) value);
        return uiElement;
    }

    @Override
    public void read() {
        setValue((Boolean) (((JCheckBox) uiElement).isSelected()));
    }
}

class IntSetting extends Setting {
    IntSetting(String name) {
        super(name);
        value = 0;
    }

    @Override
    boolean setString(String s) {
        try {
            value = Integer.parseInt(s);
        } catch (Exception e) {
            System.err.println("Setting: integer parse error\n");
            return false;
        }
        return true;
    }
}

class StringSetting extends Setting {
    StringSetting(String name) {
        super(name);
        value = "";
    }
}

class ComboSetting extends Setting {
    String[] e;

    ComboSetting(String name, String[] e) {
        super(name);
        this.e = e;
        value = e[0];
        uiElement = new JComboBox<String>(e);
    }

    /*    @Override TODO
        void setString(String s) {
       
        }*/

    @Override
    public void createUiElement() {
        uiElement = null;
    }

    @Override
    public JComponent updateUiElement() {
        ((JComboBox<?>) uiElement).setSelectedItem(value);
        return uiElement;
    }

    @Override
    public void read() {
        setString((String) (((JComboBox<?>) uiElement).getSelectedItem()));
    }
}