package algorithms;

import graph.Vertex;
import ui.Dialog;

public class BroadcastModel extends Model {

    int familiarProcessCount;

    public BroadcastModel() {
        super();
        familiarProcessCount = 0;
    }

    @Override
    public void defaultSettings() {
        familiarProcessCount = 0;
    }

    @Override
    public void processExit(String exitValue, Vertex vertex) {
        familiarProcessCount++;
        if (familiarProcessCount == player.graph.vertices.size()) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                player.stop();
            } else {
                algorithm.finishAlgorithm(null);
            }
        }
    }
}
