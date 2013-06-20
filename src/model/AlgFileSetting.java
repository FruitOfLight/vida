package model;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;


import ui.Dialog;

public class AlgFileSetting extends Setting implements ActionListener {
    private String compileCommand;
    private String runCommand;
    private String source;

    AlgFileSetting(String name) {
        super(name);
        compileCommand = "";
        runCommand = "";
        value = "";
    }

    boolean compiling = false;

    public void compile() {
        if (none())
            return;
        if (compileCommand == null || compileCommand.equals(""))
            return;
        compiling = true;
        try {
            System.out.println("Compiling: " + compileCommand);
            Process p = Runtime.getRuntime().exec(compileCommand);
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

    void read(AlgReader reader) {
        compileCommand = null;
        source = reader.getValue(name, "path").trim() + reader.getValue(name, "source").trim();
        if (source.startsWith(AlgReader.undefined) || source.startsWith(AlgReader.empty)) {
            //setting.setLocked(false);
            setString("");
            return;
        }
        String ext = source.substring(source.lastIndexOf('.'));
        if (ext.equals(".cpp") || ext.equals(".cc"))
            compileCommand = "g++ " + source + " -o " + Model.binaryPath
                    + " -O2 -std=c++0x -Wno-unused-result";
        if (ext.equals(".bin"))
            compileCommand = "cp " + source + " " + Model.binaryPath;
        setString(reader.getValue(name, "name").trim());
    }

    void print(PrintStream out) {
        if (!none()) {
            out.println(name);
            out.println("    source: " + source);
            out.println("    path: " + "");
            out.println("    name: " + value);
        }
    }

    boolean none() {
        if (value == null || !(value instanceof String))
            return true;
        return value.equals("");
    }

    @Override
    public String getString() {
        return none() ? "none" : (String) value;
    }

    @Override
    boolean setString(String s) {
        value = s;
        return true;
    }

    @Override
    public boolean getLocked() {
        return true;
    }

    JTextField tfield;

    @Override
    public void createUiElement() {
        //JPanel panel = new JPanel();
        tfield = new JTextField((String) value);
        //tfield.setEditable(false);
        tfield.setPreferredSize(new Dimension(200, IconedButton.gridHeight));

        //panel.add(new IconedButton("f_open", this));
        //panel.add(tfield);
        //panel.add(new IconedButton("f_clear", this));
        //uiElement = panel;
        uiElement = tfield;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*if (name == "f_clear")
            value = null;
        if (name == "f_open") {
            //TODO
        }
        tfield.setText((String) value);*/
    }

}

class IconedButton extends JButton {
    public String name;
    static final int gridWidth = 24;
    static final int gridHeight = 24;

    public IconedButton(String name, ActionListener listener) {
        try {
            this.setIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons/" + "b_" + name
                    + ".png"))));
            this.setRolloverIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-hover/"
                    + "b_" + name + ".png"))));
            this.setPressedIcon(new ImageIcon(ImageIO.read(new File("images/gui-buttons-pressed/"
                    + "b_" + name + ".png"))));
            this.name = name;
            this.addActionListener(listener);
            this.setSize(gridWidth, gridHeight);

            this.setBorder(BorderFactory.createEmptyBorder());
            this.setContentAreaFilled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}