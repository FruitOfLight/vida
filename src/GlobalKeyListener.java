import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;

public class GlobalKeyListener implements KeyEventDispatcher {
    private TreeSet<Integer> pressed = new TreeSet<Integer>();
    private Map<Integer, ArrayList<JButton>> buttons = new TreeMap<Integer, ArrayList<JButton>>();

    GlobalKeyListener() {
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            //System.out.println("pressed " + e.getKeyCode());
            pressed.add(e.getKeyCode());
            if (buttons.containsKey(e.getKeyCode())) {
                for (JButton button : buttons.get(e.getKeyCode())) {
                    if (button != null && button.isVisible()) {
                        button.doClick();
                        break;
                    }
                }

            }
        }
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            //System.out.println("released " + e.getKeyCode());
            pressed.remove(e.getKeyCode());
            if (buttons.containsKey(e.getKeyCode())) {
                for (JButton button : buttons.get(e.getKeyCode())) {
                    if (button != null && button.isVisible()
                            && (button instanceof ControlSwitchButton)
                            && ((ControlSwitchButton) button).kweak) {
                        button.setSelected(false);
                        break;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPressed(Integer keyCode) {
        return pressed.contains(keyCode);
    }

    public void addButton(Integer key, JButton button) {
        if (!buttons.containsKey(key))
            buttons.put(key, new ArrayList<JButton>());
        buttons.get(key).add(button);
    }

}
