package algorithms;

import java.io.File;

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

    protected Object value;
    protected String name;
    protected boolean locked;

    Setting(String name) {
        this.name = name;
        locked = false;
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

    public JComponent getUiElement() {
        return new JTextField(getString());
    }

    public void read(JComponent component) {
        setString(((JTextField) component).getText());
    }

    String getString() {
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
    String getString() {
        return ((Boolean) value) ? "yes" : "no";
    }

    @Override
    public JComponent getUiElement() {
        return new JCheckBox("", (Boolean) value);
    }

    @Override
    public void read(JComponent component) {
        setValue((Boolean) (((JCheckBox) component).isSelected()));
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

class FileSetting extends Setting {
    FileSetting(String name) {
        super(name);
    }

    @Override
    boolean setString(String s) {
        value = new File(s);
        return true;
    }
}

class ComboSetting extends Setting {
    String[] e;

    ComboSetting(String name, String[] e) {
        super(name);
        this.e = e;
        value = e[0];
    }

    /*    @Override TODO
        void setString(String s) {
       
        }*/

    @Override
    public JComponent getUiElement() {
        JComboBox<String> cb = new JComboBox<String>(e);
        cb.setSelectedItem(getString());
        return cb;
    }

    @Override
    public void read(JComponent component) {
        setString((String) (((JComboBox<?>) component).getSelectedItem()));
    }
}