import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


public class Graph {
	
	public ArrayList<Vertex> vertices;
	public ArrayList<Edge> edges;
	
	public Graph(String from)
	{
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		nacitaj(from);
	}
	
	public void nacitaj(String from)
	{
		File file = new File(from);
		Scanner input = null;
		try {
			input = new Scanner(file);
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
		finally
		{
			if(input!=null) input.close(); 
		}
	}

}
