import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Model {
    private String programPath = "";
    protected String binaryPath = "./box/program";
    String programName = "";

    Graph graph;
    RunState running;
    ModelSettings settings;
    JFileChooser programLoader;
    long startingTime;
    Algorithm algorithm;

    //statistics
    int overallMessageCount;

    Model() {

        running = RunState.stopped;
        settings = new ModelSettings();
        programLoader = new JFileChooser("./algorithms/");
        programLoader.setFileFilter(new FileNameExtensionFilter("Algorihms", "cpp"));
        algorithm = null;
        overallMessageCount = 0;
    }

    public void statisticMessage() {
        overallMessageCount++;
    }

    public void defaultSettings() {
    }

    public RunState getRunState() {
        return running;
    }

    public void openProgram() {
        try {
            graph = GUI.graph;
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
                programName = programPath.substring(programPath.lastIndexOf('/') + 1,
                        programPath.lastIndexOf('.'));
                compile();
            }

            GUI.controls.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
            Dialog.showError("Something went horribly wrong");
        }
    }

    boolean compiling = false;

    public void compile() {
        compiling = true;
        if (programPath.equals(""))
            return;
        try {
            System.out.println("Compiling: " + programPath + " " + binaryPath);
            Process p = Runtime.getRuntime().exec(
                    "./box/compile.sh " + programPath + " " + binaryPath);
            BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = err.readLine()) != null) {
                if ("debug".equals("debug")) {
                    System.err.println("Compiling: " + line);
                }
            }
            err.close();
            System.out.println("Compiling: done.");

        } catch (Exception e) {
            System.out.println(e.toString());
            Dialog.showError("Something went horribly wrong");
        }
        compiling = false;
    }

    public void load() {
    }

    int timerid = 0;

    void start() {
        GUI.controls.refresh();
        startingTime = System.currentTimeMillis();
        boolean start = false;
        if (running == RunState.stopped) {
            if (compiling) {
                Dialog.showMessage("Program is still compiling, try it again, please");
                return;
            }
            load();
            start = true;
        }
        running = RunState.running;
        StepEvent.time = System.currentTimeMillis();
        GUI.globalTimer.schedule(new StepEvent(this, timerid), 0);
        if (algorithm != null && start)
            algorithm.startAlgorithm();
    }

    void stop() {
        if (algorithm != null)
            algorithm.defaultSettings();
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
        GUI.gRepaint();
    }

    void pause() {
        GUI.controls.refresh();
        if (running == RunState.stopped)
            return;
        running = RunState.paused;
    }

    void processExit(String exitValue, Vertex vertex) {
    }

    void pauseFromProcess(Vertex vertex) {
        GUI.model.running = RunState.paused;
        GUI.globalTimer.schedule(new AuraEvent(vertex, 7), 0);
        GUI.model.pause();
    }

    private static final String version = "Version 1.00";

    public void print(PrintStream out) {
        out.println(version);
        out.println(programPath);
        out.println(programName);
        if (algorithm == null)
            out.println("null");
        else
            algorithm.print(out);
    }

    public void read(Scanner in) {
        String line = in.nextLine();
        if (!line.equals(version)) {
            System.err.println("Exception while loading program");
            programName = "";
            programPath = "";
            algorithm = null;
        } else {
            programPath = in.nextLine();
            programName = in.nextLine();
            String alg = in.nextLine();
            if (alg.equals("null"))
                algorithm = null;
            else if (alg.equals("LECNlogN"))
                algorithm = new CliqueLEAlgorithm();
            else if (alg.equals("BFS"))
                algorithm = new BFSAlgorithm();
        }
    }

    private double sendSpeed = 1.2;
    static int fps, afps, sfps;

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
            } else
                GUI.globalTimer.schedule(new AuraEvent(vertex, count - 1), 200);
        }
    }

}
