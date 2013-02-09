import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFileChooser;

public class Model {
    /*
     * TODO parametre ako synchronny? anonymny? synchronne zobudenie.... maju
     * nejake vstupne hodnoty? maju s.o.d.? caka sa od nich nejaky vystup (napr.
     * kazdy povie, ci je sef)?
     */

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
        MessageQueue.getInstance().model = this;
        graph = GUI.graph;

        for (Vertex v : graph.vertices) {
            v.program = new Program(v, this);
            v.program.load(path);
        }
    }

    void stop() {
        if (running == RunState.stopped)
            return;
        running = RunState.stopped;
        MessageQueue.getInstance().model = null;

        for (Vertex v : graph.vertices) {
            v.program.kill();
        }
        MessageQueue.getInstance().clear();
        GUI.informationPanel.erase();
    }

    void start() {
        if (running == RunState.stopped)
            load();
        running = RunState.running;
        MessageQueue.getInstance().start();
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

}
