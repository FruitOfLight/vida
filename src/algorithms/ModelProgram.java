package algorithms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import ui.Dialog;

public class ModelProgram {
    String compileCommand;
    String runCommand;
    String source;
    public String name;

    ModelProgram() {
        compileCommand = "";
        runCommand = "";
        name = "";
    }

    boolean compiling = false;

    public void compile() {
        compiling = true;
        if (compileCommand == null)
            return;
        if (compileCommand.equals("")) {
            compiling = false;
            return;
        }
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
        source = reader.getValue("Program", "source").trim();
        String path = reader.getValue("Program", "path").trim();
        String ext = source.substring(source.lastIndexOf('.'));
        if (ext.equals(".cpp") || ext.equals(".cc"))
            compileCommand = "g++ " + path + source + " -o " + Model.binaryPath + " -O2";
        if (ext.equals(".bin"))
            compileCommand = "cp " + path + source + " " + Model.binaryPath;
        name = reader.getValue("Program", "name").trim();
    }

    void print(PrintStream out) {
        out.println("  source: " + source);
    }

}
