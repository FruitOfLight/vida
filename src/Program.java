import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
 * Trieda starajuca sa o komunikaciu medzi nasim programom a spustenymi procesmi
 * 
 * 
 */
public class Program extends Thread {
    Vertex vertex;
    int id;
    ArrayList<Integer> ports;
    boolean running;

    Process process;
    InputStream output;
    OutputStream input;

    public Program(Vertex v, Model m) {
        super();
        vertex = v;
        vertex.program = this;
        running = false;
        this.id = 0;
        ports = new ArrayList<Integer>();
        for (int i = 0; i < v.edges.size(); ++i)
            ports.add(i);
    }

    @Override
    public void run() {
        super.run();
        System.out.println("bezim " + id);
        try {
            BufferedReader out = new BufferedReader(new InputStreamReader(
                    output));
            String line;
            running = true;
            while ((line = out.readLine()) != null) {
                if (line.charAt(0) == '@') {
                    String[] parts = line.substring(1).split(":", 2);
                    send(new Message(Integer.parseInt(parts[0].trim()),
                            parts[1].trim()));
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String path) {
        try {
            System.err.println("Loading... " + path);
            process = Runtime.getRuntime().exec(path);
            output = process.getInputStream();
            input = process.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void kill() {
        running = false;
        vertex.program = null;
        this.interrupt();
        process.destroy();
    }

    public void send(Message message) {
        System.err.println("send " + id + " " + message.port + " "
                + message.content);

        // TODO spravit efektivnejsie indexOf
        message.port = ports.indexOf(message.port);
        vertex.send(message);
    }

    public void recieve(Message message) {
        System.err.println("recieve " + id + " " + message.port + " "
                + message.content);
        PrintWriter in = new PrintWriter(input);
        in.println("@ " + ports.get(message.port) + " : " + message.content);
        in.flush();
    }
}
