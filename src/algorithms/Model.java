package algorithms;

import graph.Vertex;

import java.io.File;
import java.io.PrintStream;

import javax.swing.JFileChooser;

import ui.Dialog;
import ui.GUI;

/*
 * Zdruzuje vlasnosti model, spusta program, stara sa o nastavenia
 * Okrem toho sa stara o beh programu
 */
public class Model {
    static final String binaryPath = "./box/program.bin";

    JFileChooser programLoader;
    public ModelSettings settings;
    public ModelProgram program;
    public Player player;
    public Algorithm algorithm;

    //statistics
    int overallMessageCount;

    public Model() {
        settings = new ModelSettings();
        program = new ModelProgram();
        player = GUI.player;
    }

    public void statisticMessage() {
        overallMessageCount++;
    }

    public void defaultSettings() {
    }

    public void openAlgorithm() {
        int value = programLoader.showOpenDialog(null);
        if (value == JFileChooser.APPROVE_OPTION) {
            File file = programLoader.getSelectedFile();
            AlgReader algReader = new AlgReader(file);
            read(algReader);
            program.compile();
        }
    }

    public void load() {
        this.defaultSettings();
    }

    void processExit(String exitValue, Vertex vertex) {
    }

    // ma zmysel pri traverzale napr.
    public boolean canSendMessage(Vertex vertex, int port) {
        return true;
    }

    private static final String version = "Version 1.00";

    public void print(PrintStream out) {
        out.println(version);
        out.println();
        settings.print(out);
        out.println();
        program.print(out);
        out.println();
        //observer.print();
    }

    public void read(AlgReader reader) {
        if (reader.getVersion() != version) {
            Dialog.showError("Unknown format!");
            return;
        }
        settings.read(reader);
        program.read(reader);
    }

}