import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.JFileChooser;

public class Model {
    String path = "";
    Graph graph;
    RunState running;
    ModelSettings settings;
    long startingTime;

    Model() {
        running = RunState.stopped;
        settings = new ModelSettings();
    }

    public RunState getRunState() {
        return running;
    }

    public void openProgram() {
        try {
            JFileChooser programLoader = new JFileChooser("./");
            String path = "";
            int value = programLoader.showOpenDialog(null);
            if (value == JFileChooser.APPROVE_OPTION) {
                GUI.graph.emptyGraph();
                GUI.gRepaint();
                File file = programLoader.getSelectedFile();
                settings.readHeader(file);
                path = file.getPath();
            }
            Program.compile(path);
            this.path = path + ".bin";
            GUI.controls.refresh();
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
            v.program.load(path);
        }
    }

    //Timer timer;
    int timerid = 0;

    void start() {
        startingTime = System.currentTimeMillis();
        if (running == RunState.stopped)
            load();
        running = RunState.running;
        timerid++;
        StepEvent.time = System.currentTimeMillis();
        GUI.globalTimer.schedule(new StepEvent(this, timerid), 0);
    }

    void stop() {
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

    public void print(PrintStream out) {
        out.println(path);
    }

    public void read(Scanner in) {
        path = in.nextLine();
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
            GUI.globalTimer.schedule(new StepEvent(model, id), 15);
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
