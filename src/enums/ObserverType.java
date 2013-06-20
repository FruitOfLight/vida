package enums;

import model.Player;
import algorithm.BFSObserver;
import algorithm.CliqueLEObserver;
import algorithm.Observer;

public enum ObserverType {
    none, program, BFS, CliqueLE;
    public static Observer getNewInstance(Player player, ObserverType type) {
        //if (type == ModelType.program)
        //    return new 
        if (type == ObserverType.BFS)
            return new BFSObserver(player);
        else if (type == ObserverType.CliqueLE)
            return new CliqueLEObserver(player);
        return new Observer(player);
    }
}
