// TODO mozno chceme aby hrana bola one directed
public class Edge {

    Vertex from, to;

    public Edge(Vertex from, Vertex to) {
        this.from = from;
        this.to = to;
        from.edges.add(this);
        to.edges.add(this);
    }

}
