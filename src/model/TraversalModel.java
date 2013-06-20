package model;

import enums.InitType;
import enums.Property;
import graph.Vertex;

import java.util.ArrayList;

import algorithm.Pair;

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
        super.defaultSettings();
        token = null;
        tokenMessages = 0;
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

    @Override
    public ArrayList<Pair<Property, String>> neededSettings() {
        ArrayList<Pair<Property, String>> list = new ArrayList<Pair<Property, String>>();
        list.add(new Pair<Property, String>(Property.initiation, InitType.one.toString()));
        return list;
    }
}
