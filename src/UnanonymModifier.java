public class UnanonymModifier extends GraphModifier {

	public void visit(Graph graph_) {
		this.graph = graph_;
		for (int i = 0; i < graph.vertices.size(); i++)
			graph.vertices.get(i).setID(graph.getNewVertexID(i));
	}
}
