import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;


public class Graph {
	
	public ArrayList<Vertex> vertices;
	public ArrayList<Edge> edges;
	
	public Graph()
	{
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
	}
	
	public void read(Scanner input)
	{
		try {
			int n=input.nextInt(),m=input.nextInt();
			for(int i=0; i<n; i++)
			{
				int x=input.nextInt(),y=input.nextInt();
				vertices.add(new Vertex(x,y));
			}
			for(int i=0; i<m; i++)
			{
				int f=input.nextInt(),t=input.nextInt();
				edges.add(new Edge(f,t));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void print(PrintStream output)
	{
		output.println(vertices.size() +  " " + edges.size());
		for(int i=0; i<vertices.size(); i++)
			output.println(vertices.get(i).x + " " + vertices.get(i).y);
		for(int i=0; i<edges.size(); i++)
			output.println(edges.get(i).from + " " + edges.get(i).to);
	}

}
