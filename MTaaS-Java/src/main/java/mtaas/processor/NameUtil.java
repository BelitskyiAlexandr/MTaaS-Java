package mtaas.processor;

public final class NameUtil {
    static String slug(String s) {
        if (s == null || s.isBlank()) return "Relation";
        String t = s.replaceAll("[^A-Za-z0-9_]+","_");
        if (!Character.isJavaIdentifierStart(t.charAt(0))) t = "_"+t;
        return t;
    }
}
