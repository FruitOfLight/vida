public class TraversalModel extends Model {

    public TraversalModel() {
        super();
    }

    @Override
    public void defaultSettings() {
    }

    @Override
    public void load() {
    }

    @Override
    public void processExit(String exitValue, Vertex vertex) {
    }

    @Override
    public boolean canSendMessage(Vertex vertex, int port) {
        return true;
    }
}
