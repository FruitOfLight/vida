import java.io.PrintStream;
import java.util.Scanner;

public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)?
     */

    String path = "";
    Graph graph;
    RunState running;

    Model() {
        running = RunState.stopped;
    }

    private void load() {
        if (running != RunState.stopped)
            stop();
        MessageQueue.getInstance().model = this;
        graph = GUI.graph;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
    }

    void stop() {
        if (running == RunState.stopped)
            return;
        running = RunState.stopped;
        MessageQueue.getInstance().model = null;

        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        MessageQueue.getInstance().clear();
        GUI.informationPanel.erase();
    }

    void start() {
        if (running == RunState.stopped)
            load();
        running = RunState.running;
        MessageQueue.getInstance().start();
    }

    void pause() {
        running = RunState.paused;
    }

    public void print(PrintStream out) {
        out.println(path);
    }

    public void read(Scanner in) {
        path = in.nextLine();
    }

}
