
public class Model {
	/* TODO
	 * parametre ako synchronny? anonymny? synchronne zobudenie....
	 * maju nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr. kazdy povie, ci je sef)
	 */
	
	String path;
	Graph graph;
	Model(){
		
	}
	
	void load(){
		for (Vertex v : graph.vertices) {
			v.program = new Program(v, 0, 0);
			v.program.load(path);
		}				
	}	
}
