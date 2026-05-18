package mtaas.processor;

public final class NameUtil {

    private NameUtil() {
    }

    static String slug(String value) {
        if (value == null || value.isBlank()) {
            return "Relation";
        }

        String normalized = value.replaceAll("[^A-Za-z0-9_]+", "_");

        if (!Character.isJavaIdentifierStart(normalized.charAt(0))) {
            normalized = "_" + normalized;
        }

        return normalized;
    }
}