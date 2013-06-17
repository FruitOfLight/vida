package algorithms;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class AlgReader {
    private static final String version = "Version 1.00";
    private File file;
    private TreeMap<String, String> map;
    public static final String empty = "#empty";
    public static final String error = "#error";
    public static final String[] knownSections = { "#section", "Settings", "Program", "Observer" };

    public AlgReader(File file) throws FileNotFoundException {
        if (!file.exists())
            return;
        map = new TreeMap<String, String>();
        this.file = file;
        String ext = file.getName().substring(file.getName().lastIndexOf('.'));
        setValue("Program", "path", file.getParent() + "/");
        setValue("Program", "name", file.getName());
        if (ext.equals(".bin")) {
            setValue("Program", "source", file.getName());
        }
        if (ext.equals(".cpp") || ext.equals(".cc")) {
            readFromCpp();
            setValue("Program", "source", file.getName());
        }
        if (ext.equals(".alg")) {
            readFromAlg();
        }
    }

    void readFromAlg() throws FileNotFoundException {
        Scanner in = new Scanner(file);
        String line, section = "";
        while (in.hasNext()) {
            line = in.nextLine();
            if (line.startsWith("    ")) {
                parseEntry(line, section);
            } else {
                line = line.trim();
                if (line.length() > 0) {
                    section = parseSection(line);
                }
            }
        }
    }

    void readFromCpp() throws FileNotFoundException {
        Scanner in = new Scanner(file);
        String line, section = "";
        while (in.hasNext()) {
            line = in.nextLine();
            if (line.trim().contains("/*")) {
                section = "Settings";
            }
            if (line.trim().contains("*/")) {
                break;
            }
            if (section.equals("Settings")) {
                if (line.startsWith("    "))
                    parseEntry(line, section);
            }
        }
    }

    void parseEntry(String line, String section) {
        String parts[] = line.trim().split(":", 2);
        String key = (parts.length > 0) ? parts[0] : error;
        String value = (parts.length > 1) ? parts[1] : empty;
        setValue(section, key, value);
    }

    String parseSection(String line) {
        String[] parts = line.trim().split(":", 2);
        String key = (parts.length > 0) ? parts[0] : error;
        String value = (parts.length > 1) ? parts[1] : empty;
        String section = key;
        setValue("#section", key, value);
        return section;
    }

    String getVersion() {
        return version;
    }

    void setValue(String key1, String key2, String value) {
        map.put(key1 + "#" + key2, value);
        for (String s : knownSections) {
            if (key1.equals(s))
                return;
        }
        System.err.println("Unknown keyword " + key1);
    }

    String getValue(String key1, String key2) {
        String value = map.get(key1 + "#" + key2);
        return (value == null) ? "" : value;
    }
}
