abstract public class GraphModifier {

    abstract public void visit(Graph graph_);

    Graph graph;
}

class AnonymModifier extends GraphModifier {

    public AnonymModifier() {

    }

    public void visit(Graph graph_) {
        this.graph = graph_;
        for (Vertex vertex : graph.vertices) {
            vertex.setID(0);
        }
    }
}

class UnanonymModifier extends GraphModifier {

    public void visit(Graph graph_) {
        this.graph = graph_;
        for (int i = 0; i < graph.vertices.size(); i++)
            graph.vertices.get(i).setID(graph.getNewVertexID(i));
    }
}
