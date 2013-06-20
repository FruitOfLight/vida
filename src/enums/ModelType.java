package enums;

import model.BroadcastModel;
import model.LeaderElectionModel;
import model.Model;
import model.TraversalModel;

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