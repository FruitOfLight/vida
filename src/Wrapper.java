import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Wrapper {
	private static Wrapper instance = new Wrapper();
	public static Wrapper getInstance() { return instance; }
	public static Random random = new Random();;
	
	ArrayList<Program> programs;
	private Wrapper(){
		programs = new ArrayList<Program>();
	}
	public void addProgram(String path, String alias){
		Program program = new Program(alias);
		programs.add(program);
		program.load(path);
		program.start();
	}
	public void removeProgram(String alias){
		for (int i = 0; i < programs.size(); i++) {
			if (programs.get(i).alias.equals(alias)){
				programs.get(i).kill();
				programs.remove(i);
				break;
			}
		}
	}
	public void sendMessage(Message message){
		for (int i = 0; i < programs.size(); i++) {
			if (programs.get(i).alias.equals(message.to)){
				programs.get(i).recieve(message);
			}
		}				
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// + ./algorithms/echo
		Wrapper wrapper = Wrapper.getInstance();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String linestring;
		while ((linestring = in.readLine()) != null){
			String[] line;
			line = linestring.split(" ");
			String command = line[0];
			String[] arguments = new String[line.length-1];
			for (int i = 0; i < arguments.length; i++)
				arguments[i] = line[i+1];
			
			if (command.equals("+")) wrapper.addProgram(arguments[0], arguments[1]);
			if (command.equals("-")) wrapper.removeProgram(arguments[0]);
			if (command.equals("@")) wrapper.sendMessage( 
					new Message("server", arguments[0], arguments[1]) );
		}
	}

}

