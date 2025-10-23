package mtaas.processor;

import javax.lang.model.element.AnnotationMirror;

public final class AnnotationValueReader {

    public static String readRelationName(AnnotationMirror am) {
        String rn = readNamedElement(am, "relationName");
        if (rn != null && !rn.isBlank()) return rn;

        String rel = readNamedElement(am, "relation");
        if (rel != null && !rel.isBlank()) {
            return rel;
        }
        String value = readNamedElement(am, "value");
        if (value != null && !value.isBlank()) {
            return value;
        }
        return "";
    }

    private static String readNamedElement(AnnotationMirror am, String name) {
        for (var e : am.getElementValues().entrySet()) {
            if (e.getKey().getSimpleName().contentEquals(name)) {
                Object v = e.getValue().getValue();
                return (v == null) ? null : v.toString();
            }
        }
        return null;
    }
}
