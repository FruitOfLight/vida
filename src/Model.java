import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.JFileChooser;

public class Model {
    private String programPath = "";
    private String binaryPath = "./box/program";
    String programName = "";

    Graph graph;
    RunState running;
    ModelSettings settings;
    JFileChooser programLoader;
    long startingTime;

    Model() {
        running = RunState.stopped;
        settings = new ModelSettings();
        programLoader = new JFileChooser("./algorithms/");
    }

    public RunState getRunState() {
        return running;
    }

    public void openProgram() {
        try {
            int value = programLoader.showOpenDialog(null);
            if (value == JFileChooser.APPROVE_OPTION) {
                File file = programLoader.getSelectedFile();
                settings.readHeader(file);
                if (settings.getGraphType() != GraphType.any
                        && settings.getGraphType() != graph.getType()) {
                    graph.emptyGraph();
                    GUI.gRepaint();
                }
                programPath = file.getPath();
                compile();
                programName = programPath.substring(programPath.lastIndexOf('/') + 1,
                        programPath.lastIndexOf('.'));
            }

            GUI.controls.refresh();
        } catch (Exception e) {
            Dialog.showError("Something went horribly wrong");
        }
    }

    public void compile() {
        if (programPath.equals(""))
            return;
        try {
            Runtime.getRuntime().exec("bash box/compile.sh " + programPath + " " + binaryPath);
        } catch (Exception e) {
            Dialog.showError("Something went horribly wrong");
        }
    }

    private void load() {
        if (running != RunState.stopped)
            stop();
        graph = GUI.graph;
        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(binaryPath + ".bin");
        }
    }

    //Timer timer;
    int timerid = 0;

    void start() {
        GUI.controls.refresh();
        startingTime = System.currentTimeMillis();
        if (running == RunState.stopped)
            load();
        running = RunState.running;
        timerid++;
        StepEvent.time = System.currentTimeMillis();
        GUI.globalTimer.schedule(new StepEvent(this, timerid), 0);
    }

    void stop() {
        GUI.controls.refresh();
        if (running == RunState.stopped)
            return;
        running = RunState.stopped;
        for (Vertex v : graph.vertices)
            v.program.kill();
        for (Edge e : graph.edges)
            e.restart();
        graph.setDefaultValues();
        MessageQueue.messageCount = 0;
        GUI.informationPanel.erase();

    }

    void pause() {
        GUI.controls.refresh();
        if (running == RunState.stopped)
            return;
        running = RunState.paused;
    }

    void pauseFromProcess(Vertex vertex) {
        GUI.model.running = RunState.paused;
        GUI.globalTimer.schedule(new AuraEvent(vertex, 7), 0);
        GUI.model.pause();
        /*GUI.graph.pauseTime = System.currentTimeMillis();
        GUI.graph.waitTime = wait;
        GUI.model.running = RunState.paused;
        GUI.model.pause();*/
    }

    private static final String version = "Version 1.00";

    public void print(PrintStream out) {
        out.println(version);
        out.println(programPath);
        out.println(programName);
    }

    public void read(Scanner in) {
        String line = in.nextLine();
        if (!line.equals(version)) {
            System.err.println("Exception while loading program");
            programName = "";
            programPath = "";
        } else {
            programPath = in.nextLine();
            programName = in.nextLine();
        }
    }

    private double sendSpeed = 1.2;
    static int fps, afps, sfps;
    static int ticks, totaltime, longest;

    void setSendSpeed(double speed) {
        sendSpeed = speed;
    }

    double getSendSpeed() {
        return sendSpeed;
    }

    String getSendSpeedString(int length) {
        String result = ((Double) getSendSpeed()).toString();
        if (result.length() > length)
            result = result.substring(0, length);
        return result;
    }

    static class StepEvent extends TimerTask {
        static long time = 0;
        Model model;
        int id;

        public StepEvent(Model model, int id) {
            this.model = model;
            this.id = id;
        }

        @Override
        public void run() {
            if (model.running == RunState.stopped || id != model.timerid) {
                return;
            }
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;
            if (delay > 0)
                fps = (int) (1000 / delay);
            ticks++;
            totaltime += delay;
            longest = Math.max(longest, (int) delay);
            if (totaltime >= 2000) {
                afps = 1000 * ticks / totaltime;
                sfps = 1000 / longest;
                ticks = 0;
                longest = 0;
                totaltime = 0;
            }

            // Spravy
            if (model.running != RunState.stopped) {
                for (Edge edge : model.graph.edges)
                    edge.queue.step(delay);

            }

            GUI.gRepaint();
            GUI.globalTimer.schedule(new StepEvent(model, id), 20);
        }
    }

    static class AuraEvent extends TimerTask {
        Vertex vertex;
        int count;

        public AuraEvent(Vertex vertex, int count) {
            this.vertex = vertex;
            this.count = count;
        }

        @Override
        public void run() {
            if (count % 2 == 1)
                vertex.setAuraColor(new Color(0, 50, 125, 50));
            else
                vertex.setAuraColor(new Color(0, 50, 125, 200));
            GUI.gRepaint();
            if (count == 0) {
                vertex.setAuraColor(new Color(255, 255, 255, 0));
                GUI.model.running = RunState.running;
                GUI.model.start();
            } else
                GUI.globalTimer.schedule(new AuraEvent(vertex, count - 1), 200);
        }
    }

}
