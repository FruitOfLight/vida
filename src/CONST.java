public class CONST {

    // GUI
    public static final int windowWidth = 1000;
    public static final int windowHeight = 700;
    public static final int graphWidth = 700;
    public static final int menuHeight = 25;
    public static final int queueHeight = 75;
    public static final int graphHeight = 500;
    public static final int controlsHeight = 50;
    public static final int controlsWidth = 700;

    // Graph
    public static final int vertexSize = 10;

    // Queue
    public static final double speedFactor = 1.2;
    // o kolko sa zrychli beh, pri stlaceni forward

    // Model
    // public static final int running = 2;
    // public static final int paused = 1;
    // public static final int stoped = 0;

}

enum RunState {
    stopped, paused, running
}
