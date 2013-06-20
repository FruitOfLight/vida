package model;

import java.util.ArrayList;

import algorithm.Pair;
import enums.InitType;
import enums.Property;

public class BroadcastModel extends Model {

    public BroadcastModel() {
        super();
    }

    @Override
    public ArrayList<Pair<Property, String>> neededSettings() {
        ArrayList<Pair<Property, String>> list = new ArrayList<Pair<Property, String>>();
        list.add(new Pair<Property, String>(Property.initiation, InitType.one.toString()));
        return list;
    }
}
