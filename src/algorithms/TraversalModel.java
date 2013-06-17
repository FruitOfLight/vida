package algorithms;

import graph.Vertex;
import ui.Dialog;

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
    public void processExit(String exitValue, Vertex vertex) {
        discoveredCount++;
        if (discoveredCount == player.graph.vertices.size()) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                player.stop();
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
