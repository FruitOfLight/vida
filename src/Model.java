import java.util.ArrayList;

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
        if (running) stop();
        MessageQueue.getInstance().model = this;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
        running = true;
        MessageQueue.getInstance().timer.schedule(
                new MessageQueue.QueueEvent(), 0);
        MessageQueue.getInstance().timer.schedule(new MessageQueue.MessageDrawEvent(), 1);
    }

    void stop() {
        if (!running) return;
        MessageQueue.getInstance().model = null;
        MessageQueue.getInstance().list = new ArrayList<Message>();
        running = false;
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        MessageQueue.getInstance().clear();
    }
}
