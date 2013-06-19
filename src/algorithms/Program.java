package algorithms;

import enums.Property;
import graph.Message;
import graph.Vertex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ui.GUI;

//TODO bugfix, ked program posle na neexistujuci port

/*
 * Trieda starajuca sa o komunikaciu medzi nasim programom a spustenymi procesmi
 * 
 * 
 */
public class Program extends Thread {
    Vertex vertex;
    Player player;
    int id;
    public ArrayList<Integer> ports;
    boolean running;

    Process process;
    InputStream output;
    OutputStream input;
    PrintWriter in;

    boolean exited;

    public Program(Vertex v, Player p) {
        super();
        player = p;
        vertex = v;
        vertex.program = this;
        running = false;
        exited = false;
        this.id = vertex.getID();
        ports = new ArrayList<Integer>();
        for (int i = 0; i < v.edges.size(); ++i) {
            ports.add(i);
        }
    }

    @Override
    public void run() {
        super.run();
        System.out.println("bezim " + id);
        try {
            BufferedReader out = new BufferedReader(new InputStreamReader(output));
            String line;
            running = true;
            while ((line = out.readLine()) != null) {
                if (line.charAt(0) == '@') {
                    String[] parts = line.substring(1).split(":", 2);
                    if (player.model.canSendMessage(vertex, Integer.parseInt(parts[0].trim())))
                        send(Integer.parseInt(parts[0].trim()), parts[1].trim());
                }
                if (line.charAt(0) == '#') {
                    int p = 1;
                    while (line.charAt(p) == '#')
                        p++;
                    GUI.informationPanel.printInformation(vertex, line.substring(1).trim());
                    vertex.shout(line.substring(1).trim(), p);
                }
                if (line.charAt(0) == '$') {
                    String parts[] = line.substring(1).split(":", 2);
                    vertex.setVariable(parts[0].trim(), parts[1].trim());
                }
                if (line.charAt(0) == '%') {
                    player.pauseFromProcess(vertex);
                }
                if (line.charAt(0) == '*' && player.model.algorithm != null) {
                    player.model.algorithm.recieveUpdate(vertex, line.substring(1).trim());
                }
                if (line.charAt(0) == '&' && !exited) {
                    exited = true;
                    player.model.processExit(line.substring(1).trim(), vertex);
                }
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(String path, int initValue) {
        try {
            System.err.println("Loading... " + path);
            process = Runtime.getRuntime().exec(path);
            output = process.getInputStream();
            input = process.getOutputStream();
            in = new PrintWriter(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.init();
        this.start();
    }

    public void kill() {
        running = false;
        vertex.program = null;
        this.interrupt();
        process.destroy();
    }

    // program sa dozvie pociatocne hodnoty, ako napriklad pocet portov
    public void init() {
        // pocet portov a ich hodnoty
        in.print("* ports : " + ports.size());
        ArrayList<Integer> portsz = new ArrayList<Integer>();
        for (Integer i : ports)
            portsz.add(i);
        long seed = System.nanoTime();
        Collections.shuffle(portsz, new Random(seed));
        for (int p : portsz) {
            in.print(" " + p);
        }
        in.println();

        in.println("* id : " + ((GUI.player.model.settings.isProperty(Property.anonym)) ? id : 0));
        in.println("* initvalue : " + vertex.getInitial());
        in.println("* start");
        in.flush();
    }

    public void send(int port, String content) {
        // svoj port zmenim na port vrchola
        // TODO spravit efektivnejsie nez cez indexOf
        port = ports.indexOf(port);
        vertex.send(new Message(port, content));
    }

    public void receive(Message message) {
        player.model.statisticMessage();
        in.println("@ " + ports.get(message.toPort) + " : " + message.rawContent);
        in.flush();
    }
}
