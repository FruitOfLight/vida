import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.TreeSet;

public class GlobalKeyListener implements KeyEventDispatcher {
    private TreeSet<Integer> pressed;

    GlobalKeyListener() {
        pressed = new TreeSet<Integer>();
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            System.out.println("pressed " + e.getKeyCode());
            pressed.add(e.getKeyCode());
        }
        if (e.getID() == KeyEvent.KEY_RELEASED) {
            System.out.println("released " + e.getKeyCode());
            pressed.remove(e.getKeyCode());
        }
        return false;
    }

    public boolean isPressed(Integer keyCode) {
        return pressed.contains(keyCode);
    }
}
