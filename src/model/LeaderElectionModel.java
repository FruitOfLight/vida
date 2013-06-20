package model;

import enums.InitType;
import enums.Property;
import graph.Vertex;

import java.util.ArrayList;

import ui.Dialog;
import algorithm.Pair;

public class LeaderElectionModel extends Model {

    Vertex leader;

    public LeaderElectionModel() {
        super();
    }

    @Override
    public void defaultSettings() {
        super.defaultSettings();
        leader = null;
    }

    @Override
    public void processExit(String exitValue, Vertex vertex) {
        super.processExit(exitValue, vertex);
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
            if (leader == null) {
                Dialog.showError("There is no leader");
                player.stop();
            }
        }
    }

    @Override
    public ArrayList<Pair<Property, String>> neededSettings() {
        ArrayList<Pair<Property, String>> list = new ArrayList<Pair<Property, String>>();
        list.add(new Pair<Property, String>(Property.initiation, InitType.no.toString()));
        return list;
    }
}
