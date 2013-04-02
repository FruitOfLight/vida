import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;

public class GlobalKeyListener implements KeyEventDispatcher {
    private TreeSet<Integer> pressed = new TreeSet<Integer>();
    private Map<Integer, ArrayList<JButton>> buttons = new TreeMap<Integer, ArrayList<JButton>>();
    private Map<Integer, MouseListener> mListeners = new TreeMap<Integer, MouseListener>();

    GlobalKeyListener() {
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            //System.out.println("pressed " + e.getKeyCode());
            pressed.add(e.getKeyCode());
            MouseListener mlis;

            if ((mlis = mListeners.get(e.getKeyCode())) != null) {
                mlis.mouseClicked(null);
            }
            if (buttons.containsKey(e.getKeyCode())) {
                // TODO fuuj :) toto si zasluhuje prerobit
                int specialCase = 0;
                for (JButton button : buttons.get(e.getKeyCode())) {
                    if (button != null && button.isVisible()) {

                        if ((button instanceof ControlSwitchButton)
                                && ((ControlSwitchButton) button).radioLevel == -1) {
                            if (((ControlSwitchButton) button).getRadioPrevious().isSelected())
                                specialCase = 1;
                            else {
                                specialCase = 2;
                                continue;
                            }
                        }
                        button.doClick();
                        break;
                    }
                }
                if (specialCase == 2) {
                    for (JButton button : buttons.get(e.getKeyCode())) {
                        if (button != null && button.isVisible()) {
                            if ((button instanceof ControlSwitchButton)
                                    && ((ControlSwitchButton) button).radioLevel == -1) {
                                button.doClick();
                                break;
                            }
                        }
                    }
                }
            }

        }
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            //System.out.println("released " + e.getKeyCode());
            pressed.remove(e.getKeyCode());
            /*if (buttons.containsKey(e.getKeyCode())) {
                for (JButton button : buttons.get(e.getKeyCode())) {
                    if (button != null && button.isVisible()
                            && (button instanceof ControlSwitchButton)
                            && ((ControlSwitchButton) button).kweak) {
                        button.doClick();
                        break;
                    }
                }
            }*/
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

    public void addMouseListener(Integer key, MouseListener mlis) {
        if (mListeners.put(key, mlis) != null) {
            System.err.println("warning: overriding mouse listener for key " + key);
        }
    }

}
