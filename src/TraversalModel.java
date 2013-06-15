
import java.util.Random;

public class TraversalModel extends Model {

    Vertex token;
    int tokenMessages;
    int discoveredCount;

    public TraversalModel() {
        super();
        defaultSettings();
    }

    @Override
    public void defaultSettings() {
        token = null;
        tokenMessages = 0;
        discoveredCount = 0;
    }

    @Override
    public void load() {
        this.defaultSettings();
        if (running != RunState.stopped)
            stop();
        graph = GUI.graph;
        Random gen = new Random();
        int firstToken = gen.nextInt(graph.vertices.size());
        token = graph.vertices.get(firstToken);
        for (int i = 0; i < graph.vertices.size(); i++) {
            graph.vertices.get(i).program = new Program(graph.vertices.get(i), this);
            if (i == firstToken) {
                graph.vertices.get(i).program.load(binaryPath + ".bin", 1);
            } else {
                graph.vertices.get(i).program.load(binaryPath + ".bin", 0);
            }
        }
    }

    @Override
    public void processExit(String exitValue, Vertex vertex) {
        discoveredCount++;
        if (discoveredCount == graph.vertices.size()) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                this.stop();
            } else {
                algorithm.finishAlgorithm(null);
            }
        }
    }

    @Override
    public boolean canSendMessage(Vertex vertex, int port) {
        if (vertex == token) {
            token = vertex.edges.get(port).to;
            tokenMessages++;
            return true;
        }
        return false;
    }
}
