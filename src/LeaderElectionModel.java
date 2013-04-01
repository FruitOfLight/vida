public class LeaderElectionModel extends Model {

    Vertex leader;
    int deadProcessCount;

    public LeaderElectionModel() {
        super();
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
                this.stop();
                return;
            } else
                leader = vertex;
        }
        //FIXME vyhod chybovu hlasku
        if (deadProcessCount == graph.vertices.size()) {
            Dialog.showError("There is no leader");
            this.stop();
            return;
        }
        if (deadProcessCount == graph.vertices.size() - 1 && leader != null) {
            if (algorithm == null) {
                Dialog.showMessage("Well done.");
                this.stop();
            } else
                algorithm.finishAlgorithm(leader);
            return;
        }
    }
}
