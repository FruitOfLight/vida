package ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import enums.Language;

public class TextBook {

    TreeMap<String, String> dictionary;
    ArrayList<String> ids;
    ArrayList<String> content;
    String file;
    Language language;

    public TextBook(String file) {
        dictionary = new TreeMap<String, String>();
        this.file = file;
        this.language = GUI.getLanguage();
        loadTextBook();
    }

    public void loadTextBook() {
        try {
            Scanner in = new Scanner(new File("texts/" + this.language.name() + "/" + file));
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] help = line.split("#", 2);
                dictionary.put(help[0], help[1]);
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void change() {
        if (this.language == GUI.getLanguage())
            return;
        this.language = GUI.getLanguage();
        loadTextBook();
    }

    public String getMatchingString(String name) {
        String s = dictionary.get(name);
        return (s == null) ? "*" + name + "*" : s;
    }

    public String getText(String name) {
        return getText(name, new Object[0]);
    }

    public String getText(String name, Object[] values) {
        String con = getMatchingString(name);
        Object[] help = con.split("%$");
        String res = "";
        for (int i = 0; i < help.length - 1; i += 2)
            res += (String) help[i] + values[(Integer) help[i + 1]];
        res += help[help.length - 1];
        return res;
    }

    public static String[] getLanguageArray(String name) {
        try {
            Scanner in = new Scanner(new File("texts/" + GUI.getLanguage() + "/" + name));
            String result = "";
            while (in.hasNextLine()) {
                result += in.nextLine();
                result += "@!";
            }
            in.close();
            return result.substring(0, result.length() - 2).split("@!");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return new String[0];
    }
}
