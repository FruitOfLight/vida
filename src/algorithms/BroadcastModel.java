package algorithms;

import java.util.Random;

import ui.Dialog;
import ui.GUI;
import enums.RunState;
import graph.Vertex;

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
    public void load() {
        this.defaultSettings();
        if (running != RunState.stopped)
            stop();
        graph = GUI.graph;
        Random gen = new Random();
        int gossip = gen.nextInt(graph.vertices.size());
        for (int i = 0; i < graph.vertices.size(); i++) {
            graph.vertices.get(i).program = new Program(graph.vertices.get(i), this);
            if (i == gossip) {
                graph.vertices.get(i).program.load(binaryPath + ".bin", 1);
            } else {
                graph.vertices.get(i).program.load(binaryPath + ".bin", 0);
            }
        }
    }

    @Override
    public void processExit(String exitValue, Vertex vertex) {
        familiarProcessCount++;
        if (familiarProcessCount == graph.vertices.size()) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                this.stop();
            } else {
                algorithm.finishAlgorithm(null);
            }
        }
    }
}
