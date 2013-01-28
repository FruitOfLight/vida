
public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)
     */

    String path = "./algorithms/randsend.cpp.bin";
    Graph graph;
    RunState running;

    Model() {
        running = RunState.stopped;
    }

    void load() {
        if (running != RunState.stopped) stop();
        MessageQueue.getInstance().model = this;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
        running = RunState.running;
        MessageQueue.getInstance().start();
    }

    void stop() {
        if (running == RunState.stopped) return;
        MessageQueue.getInstance().model = null;
        running = RunState.stopped;
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        MessageQueue.getInstance().clear();
    }
}
