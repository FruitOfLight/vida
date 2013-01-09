import java.util.ArrayList;


public class Vertex {
	
	int x,y;
	ArrayList<Vertex> neigh;
	Program program;
	
	public Vertex(int x, int y)
	{
		neigh = new ArrayList<Vertex>();
		this.x=x;
		this.y=y;
	}
	
	void send(Message message){
		
	}
	void recieve(Message message){
		
	}
	

}
