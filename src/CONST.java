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

    // Keys
    public static final int shiftKey = 16;
    public static final int controlKey = 17;

    // Queue
    // o kolko sa zrychli beh, pri stlaceni forward
    public static final double speedFactor = 1.2;

}

enum RunState {
    stopped, paused, running
}

enum Preference {
    begin, end, special, wrap
}

enum MessageState {
    born, main, sleep, dead
}

enum Anonym {
    anonymOn, anonymOff
}

enum Synchroned {
    synchronedOn, synchronedOff
}

enum GraphType {
    none, cycle, clique
}
