package enums;

import algorithms.BroadcastModel;
import algorithms.LeaderElectionModel;
import algorithms.Model;
import algorithms.TraversalModel;

public enum ModelType {
    def, leaderElection, broadcast, traversal;
    public static Model getNewInstance(ModelType type) {
        if (type == ModelType.leaderElection)
            return new LeaderElectionModel();
        else if (type == ModelType.broadcast)
            return new BroadcastModel();
        else if (type == ModelType.traversal)
            return new TraversalModel();
        return new Model();
    }
}