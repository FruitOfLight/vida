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
    private Map<Integer, Integer> bid = new TreeMap<Integer, Integer>();
    private Map<Integer, MouseListener> mListeners = new TreeMap<Integer, MouseListener>();

    GlobalKeyListener() {
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            pressed.add(e.getKeyCode());
            MouseListener mlis;

            if ((mlis = mListeners.get(e.getKeyCode())) != null) {
                mlis.mouseClicked(null);
            }
            if (buttons.containsKey(e.getKeyCode())) {
                ArrayList<JButton> list = buttons.get(e.getKeyCode());
                if (bid.containsKey(e.getKeyCode())) {
                    for (int i = 0; i < list.size(); ++i) {
                        int k = isPressed(KeyEvent.VK_SHIFT) ? -1 : 1;
                        int a = (bid.get(e.getKeyCode()) + k + list.size()) % list.size();
                        bid.put(e.getKeyCode(), a);
                        if (list.get(a).isVisible()) {
                            list.get(a).doClick();
                            break;
                        }
                    }
                } else {
                    for (JButton button : buttons.get(e.getKeyCode())) {
                        if (button != null && button.isVisible()) {
                            button.doClick();
                            break;
                        }
                    }
                }

            }

        }
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            pressed.remove(e.getKeyCode());
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

    public void addBid(Integer key) {
        if (!bid.containsKey(key))
            bid.put(key, -1);
    }

    public void addMouseListener(Integer key, MouseListener mlis) {
        if (mListeners.put(key, mlis) != null) {
            System.err.println("warning: overriding mouse listener for key " + key);
        }
    }

}
