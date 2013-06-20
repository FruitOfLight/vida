package algorithms;

import enums.InitType;
import enums.Property;
import graph.Vertex;

import java.util.ArrayList;

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

    @Override
    public ArrayList<Pair<Property, String>> neededSettings() {
        ArrayList<Pair<Property, String>> list = new ArrayList<Pair<Property, String>>();
        list.add(new Pair<Property, String>(Property.initiation, InitType.one.toString()));
        return list;
    }
}
