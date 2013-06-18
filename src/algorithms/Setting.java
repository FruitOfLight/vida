package algorithms;

import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

public abstract class Setting {
    protected Object value;
    protected String name;
    protected boolean locked;

    Setting(String name) {
        this.name = name;
    }

    void setLocked(boolean locked) {
        this.locked = locked;
    }

    boolean getLocked() {
        return locked;
    }

    Object getValue() {
        return value;
    }

    void setValue(Object value) {
        this.value = value;
    }

    String getName() {
        return name;
    }

    JComponent getUiElement() {
        return new JTextField(getString());
    }

    void read(JComponent component) {
        setString(((JTextField) component).getText());
    }

    String getString() {
        return value.toString();
    }

    void setString(String s) {
        value = s;
    }

}

class IntSetting extends Setting {
    IntSetting(String name) {
        super(name);
    }

    @Override
    void setString(String s) {
        value = Integer.parseInt(s);
    }
}

class StringSetting extends Setting {
    StringSetting(String name) {
        super(name);
    }
}

class FileSetting extends Setting {
    FileSetting(String name) {
        super(name);
    }

    @Override
    void setString(String s) {
        value = new File(s);
    }
}

class ComboSetting extends Setting {
    String[] e;

    ComboSetting(String name, String[] e) {
        super(name);
        this.e = e;
    }

    /*    @Override TODO
        void setString(String s) {
       
        }*/

    @Override
    JComponent getUiElement() {
        JComboBox<String> cb = new JComboBox<String>(e);
        cb.setSelectedItem(getString());
        return cb;
    }

    @Override
    void read(JComponent component) {
        setString((String) (((JComboBox<?>) component).getSelectedItem()));
    }
}