package model;

import enums.Property;
import graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import ui.Dialog;
import ui.GUI;
import algorithm.Pair;

/*
 * Zdruzuje vlasnosti model, spusta program, stara sa o nastavenia
 * Okrem toho sa stara o beh programu
 */
public class Model {
    static final String binaryPath = "./box/program.bin";

    JFileChooser programLoader;
    public ModelSettings settings;
    public AlgFileSetting program;
    public AlgFileSetting observer;
    public Player player;

    //statistics
    public int overallMessageCount;

    public Model() {
        programLoader = new JFileChooser("./algorithms/");
        programLoader.setFileFilter(new FileNameExtensionFilter("Algorihms", "alg", "cpp", "cc",
                "bin"));
        settings = new ModelSettings(this);
        player = GUI.player;
    }

    public void statisticMessage() {
        overallMessageCount++;
    }

    int deadProcessCount;

    public void defaultSettings() {
        deadProcessCount = 0;
    }

    public void openAlgorithm() {
        int value = programLoader.showOpenDialog(null);
        if (value == JFileChooser.APPROVE_OPTION) {
            File file = programLoader.getSelectedFile();
            AlgReader algReader = null;

            try {
                algReader = new AlgReader(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                read(algReader);
            } catch (Exception e) {
                settings.clear();
            }
            GUI.controls.refresh();
            GUI.gRepaint();
            program.compile();
        }
        GUI.controls.refresh();
        GUI.gRepaint();
    }

    public void load() {
        this.defaultSettings();
    }

    public void processExit(String exitValue, Vertex vertex) {
        deadProcessCount++;
        if (deadProcessCount == player.graph.vertices.size()) {
            player.observer.onFinish();
        }
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

    public ArrayList<Pair<Property, String>> neededSettings() {
        return new ArrayList<Pair<Property, String>>();
    }
}