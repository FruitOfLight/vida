import java.awt.event.KeyEvent;

public class CONST {

    // GUI
    public static final int windowWidth = 1000;
    public static final int windowHeight = 700;
    public static final int minWindowWidth = 700;
    public static final int minWindowHeight = 600;
    public static final int menuHeight = 25;
    public static final int controlsHeight = 70;
    public static final int zoomWindowWidth = 300;
    public static final int zoomWindowHeight = 300;
    public static final int informationWidth = 300;
    public static final int popupwidth = 20;

    // Graph
    public static final int vertexSize = 10;

    // Keys
    public static final int moveKey = KeyEvent.VK_SHIFT;
    public static final int deleteKey = KeyEvent.VK_CONTROL;

    // Queue
    // o kolko sa zrychli beh, pri stlaceni forward
    public static final double speedFactor = 1.2;
    public static final double messageSpeedLimit = 2.0;
}

enum RunState {
    stopped, paused, running
}

enum Preference {
    begin, end, special
}

enum DeliverState {
    born, alive, asleep, inbox, delivered
}

enum Constrast {
    textbw, borderbw, invert
}

enum PositionX {
    left, right
}

enum PositionY {
    up, down
}
