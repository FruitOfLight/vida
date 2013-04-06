public class TextBook {

    public String getString(String name, Object[] values) {
        if (GUI.language == Language.english)
            return getEnglishString(name, values);
        if (GUI.language == Language.slovak)
            return getSlovakString(name, values);
        return "";
    }

    public String getEnglishString(String name, Object[] values) {
        return "";
    }

    public String getSlovakString(String name, Object[] values) {
        return "";
    }

}
