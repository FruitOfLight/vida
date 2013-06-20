package algorithms;

import enums.InitType;
import enums.Property;
import graph.Vertex;

import java.util.ArrayList;

import ui.Dialog;

public class LeaderElectionModel extends Model {

    Vertex leader;
    int deadProcessCount;

    public LeaderElectionModel() {
        super();
        leader = null;
        deadProcessCount = 0;
    }

    @Override
    public void defaultSettings() {
        leader = null;
        deadProcessCount = 0;
    }

    @Override
    void processExit(String exitValue, Vertex vertex) {
        if (exitValue.equals("false"))
            deadProcessCount++;
        if (exitValue.equals("true")) {
            //FIXME vyhod chybovu hlasku
            if (leader != null) {
                Dialog.showError("Leader had been already choosed.");
                player.stop();
                return;
            } else
                leader = vertex;
        }
        //FIXME vyhod chybovu hlasku
        if (deadProcessCount == player.graph.vertices.size()) {
            Dialog.showError("There is no leader");
            player.stop();
            return;
        }
        if (deadProcessCount == player.graph.vertices.size() - 1 && leader != null) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                player.stop();
            } else
                algorithm.finishAlgorithm(leader);
            return;
        }
    }

    @Override
    public ArrayList<Pair<Property, String>> neededSettings() {
        ArrayList<Pair<Property, String>> list = new ArrayList<Pair<Property, String>>();
        list.add(new Pair<Property, String>(Property.initiation, InitType.no.toString()));
        return list;
    }
}
