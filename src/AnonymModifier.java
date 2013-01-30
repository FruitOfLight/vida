public class AnonymModifier extends GraphModifier {

	public AnonymModifier() {

	}

	public void visit(Graph graph_) {
		this.graph = graph_;
		for (Vertex vertex : graph.vertices) {
			vertex.setID(0);
		}
	}
}