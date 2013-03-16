import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;

public class Model {
    String path = "";
    Graph graph;
    RunState running;
    ModelSettings settings;

    Model() {
        running = RunState.stopped;
        settings = new ModelSettings();
    }

    public void openProgram() {
        try {
            JFileChooser programLoader = new JFileChooser("./");
            String path = "";
            int value = programLoader.showOpenDialog(null);
            if (value == JFileChooser.APPROVE_OPTION) {
                GUI.graph.emptyGraph();
                GUI.graph.canvas.repaint();
                File file = programLoader.getSelectedFile();
                settings.readHeader(file);
                path = file.getPath();
            }
            Program.compile(path);
            this.path = path + ".bin";
            GUI.controls.panel.repaint();
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

    Timer timer;

    void start() {
        if (running == RunState.stopped)
            load();
        running = RunState.running;
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        StepEvent.time = System.currentTimeMillis();
        timer.schedule(new StepEvent(this), 0);
    }

    void stop() {
        if (running == RunState.stopped)
            return;
        running = RunState.stopped;
        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        GUI.informationPanel.erase();
    }

    void pause() {
        running = RunState.paused;
    }

    public void print(PrintStream out) {
        out.println(path);
    }

    public void read(Scanner in) {
        path = in.nextLine();
    }

    private double sendSpeed = 1.2;

    void setSendSpeed(double speed) {
        sendSpeed = speed;
    }

    double getSendSpeed() {
        return sendSpeed;
    }

    static class StepEvent extends TimerTask {
        static long time = 0;
        Model model;

        public StepEvent(Model model) {
            this.model = model;
        }

        @Override
        public void run() {
            if (model.running == RunState.stopped) {
                return;
            }
            long prevTime = time;
            time = System.currentTimeMillis();
            long delay = time - prevTime;
            if (delay > 0)
                GUI.frame.setTitle("ViDA    fps: " + 1000 / delay);

            // Spravy
            if (model.running == RunState.running) {
                for (Edge edge : model.graph.edges)
                    edge.queue.step(delay);
            }

            model.graph.canvas.repaint();
            model.timer.schedule(new StepEvent(model), 30);
        }
    }

}
