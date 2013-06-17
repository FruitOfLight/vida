package algorithms;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JOptionPane;

import ui.Dialog;
import ui.GUI;
import enums.GraphType;
import enums.ModelType;
import enums.Property;

/**
 * Model Settings zdruzuje vsetky nastavitelne veci v modeli. Veci, ktore sa daju nastavit su
 * vymenovane v enum Property.
 * 
 */

public class ModelSettings {
    private int[] properties;
    private boolean[] locked;

    public ModelSettings() {
        properties = new int[Property.values().length];
        locked = new boolean[Property.values().length];
        resetValues();
    }

    public void resetValues() {
        for (int i = 0; i < properties.length; ++i)
            properties[i] = 0;
        for (int i = 0; i < locked.length; ++i)
            locked[i] = false;
    }

    //// Zakladne pracovne funkcie
    public void setProperty(Property property, int value) {
        properties[property.ordinal()] = value;
    }

    public int getProperty(Property property) {
        return properties[property.ordinal()];
    }

    public void setLocked(Property property, boolean lock) {
        locked[property.ordinal()] = lock;
    }

    public boolean getLocked(Property property) {
        return locked[property.ordinal()];
    }

    //// Odvodene pracovne funkcie
    public void setProperty(Property property, boolean on) {
        setProperty(property, on ? 1 : 0);
    }

    public boolean isProperty(Property property) {
        return getProperty(property) != 0;
    }

    public void setGraphType(GraphType type) {
        setProperty(Property.graphType, type.ordinal());
    }

    public GraphType getGraphType() {
        return GraphType.values()[getProperty(Property.graphType)];
    }

    public void setModel(ModelType type) {
        setProperty(Property.model, type.ordinal());
    }

    public ModelType getModel() {
        return ModelType.values()[getProperty(Property.model)];
    }

    //// Zvysok
    public void showDialog() {
        Dialog.DialogProgramSettings newProgramSettings = new Dialog.DialogProgramSettings(this);
        int ok = JOptionPane.showConfirmDialog(null, newProgramSettings.getPanel(),
                "Program settings", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            newProgramSettings.apply(this);
            GUI.acceptSettings(this);
            GUI.player.graph.acceptSettings(this);
        }
    }

    public void readHeader(File f) {
        resetValues();
        try {
            // TODO dovolit vacsiu volnost, spravit podporu pre jazyky s inymi komentarmi...
            Scanner in = new Scanner(f);
            String header = "";
            String line = in.nextLine();
            if (!line.contains("/*")) {
                in.close();
                return;
            }
            header += line;
            while ((line = in.nextLine()) != null) {
                header += line;
                if (line.contains("*/"))
                    break;
            }
            in.close();

            // sparsovanie stringu

            String[] tokens = header.replaceAll("[^a-zA-Z0-9_]+", " ").trim().split("[ \t\n\r\f]+");

            for (int i = 0; i + 1 < tokens.length; i += 2) {
                Property p = Property.valueOf(tokens[i]);
                int v = nameToInt(tokens[i + 1]);
                if (p == null || v < 0)
                    continue;
                setProperty(p, v);
                setLocked(p, true);

            }

        } catch (Exception e) {
            System.out.println();
        }
    }

    private int nameToInt(String str) {
        // TODO automatizovat
        if (str.equals("no"))
            return 0;
        if (str.equals("yes"))
            return 1;
        if (str.equals("any"))
            return 0;
        if (str.equals("clique"))
            return 1;
        if (str.equals("cycle"))
            return 2;
        if (str.equals("grid"))
            return 3;
        if (str.equals("wheel"))
            return 4;
        return -1;
    }

    public void print(PrintStream out) {
        out.println("Settings");
    }

    public void read(AlgReader reader) {

    }

}
