
public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)
     */

    String path = "./algorithms/randsend.cpp.bin";
    Graph graph;
    int running;

    Model() {
        running = CONST.stoped;
    }

    void load() {
        if (running != CONST.stoped) stop();
        MessageQueue.getInstance().model = this;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
        running = CONST.running;
        MessageQueue.getInstance().start();
    }

    void stop() {
        if (running == CONST.stoped) return;
        MessageQueue.getInstance().model = null;
        running = CONST.stoped;
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        MessageQueue.getInstance().clear();
    }
}
