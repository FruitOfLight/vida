public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)
     */

    String path = "./algorithms/echo.cpp.bin";
    Graph graph;

    Model() {

    }

    void load() {
        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
    }

    void stop() {
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
    }
}
