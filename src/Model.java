public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)
     */

    String path = "./algorithms/send.cpp.bin";
    Graph graph;
    boolean running;

    Model() {
        running = false;
    }

    void load() {
        MessageQueue.getInstance().model = this;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
        running = true;
        MessageQueue.getInstance().timer.schedule(
                new MessageQueue.TimerEvent(), 0);
    }

    void stop() {
        MessageQueue.getInstance().model = null;
        running = false;
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
    }
}
