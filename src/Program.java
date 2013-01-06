import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Program extends Thread{
		String alias;
		Process process;
		InputStream output;
		OutputStream input;
		public Program(String alias){
			super();
			this.alias = alias;
		}
		
		@Override
		public void run(){
			super.run();
			try {
				BufferedReader out = new BufferedReader(new InputStreamReader(output));
				String line;
				while ((line = out.readLine()) != null) {
					send(new Message(alias, "?", line));
		        }
		        out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void load(String path){
			try {
				System.err.println("Loading... "+ path);
				process = Runtime.getRuntime().exec(path);
				output = process.getInputStream();
				input = process.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void kill(){
			this.interrupt();
			process.destroy();			
		}
		public void send(Message message){
			System.out.println("Message from "+message.from+" on port "+message.fromPort+" :"+message.content);
		}
		public void recieve(Message message){
			System.out.println("Message from "+message.from + " to " + message.to + " @ " + message.fromPort + " : " + message.content);
			PrintWriter in = new PrintWriter(input);
			in.println("@ " + message.fromPort + " : " + message.content);
			in.flush();
		}
}
