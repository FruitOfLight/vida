package ui;

import java.io.File;
import java.util.Scanner;

public class TextBook {

    public static Object[] editValues(Object[] val, int i) {
        Object[] res = new Object[val.length - i];
        for (int j = i; j < val.length; j++)
            res[j - i] = val[i];
        return res;
    }

    public static String getLanguageString(String name) {
        return getLanguageString(name, new Object[0]);
    }

    public static String getMatchedString(String[] id, String[] value, String pattern) {
        return getMatchedString(id, value, pattern, new Object[0]);
    }

    public static String getMatchedString(String[] id, String[] value, String pattern,
            Object[] addValues) {
        for (int i = 0; i < id.length; i++)
            if (id[i].equals(pattern))
                return addValues(value[i], addValues);
        return "";
    }

    public static String getLanguageString(String name, Object[] values) {
        try {
            Scanner in = new Scanner(new File("texts/" + GUI.language + "/" + name));
            String content = in.nextLine();
            in.close();
            return addValues(content, values);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return "";
    }

    static String addValues(String content, Object[] values) {
        String[] parts = content.split("%d");
        String result = "";
        for (int i = 0; i < parts.length - 1; i++)
            result += parts[i] + values[i];
        result += parts[parts.length - 1];
        return result;
    }

    public static String[] getLanguageArray(String name) {
        try {
            Scanner in = new Scanner(new File("texts/" + GUI.language + "/" + name));
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
