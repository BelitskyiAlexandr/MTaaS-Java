package mtaas.processor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.tools.Diagnostic;

public final class AnnotationValueReader {

    public static String readRelationName(AnnotationMirror am) {
        String rn = readNamedElement(am, "relationName");
        if (rn != null && !rn.isBlank()) return rn;

        String rel = readNamedElement(am, "relation");
        if (rel != null && !rel.isBlank()) return rel;

        String value = readNamedElement(am, "value");
        if (value != null && !value.isBlank()) return value;

        return "";
    }

    public static List<String> readRelationNamesFromContainer(
            AnnotationMirror container,
            Messager messager
    ) {
        List<String> names = new ArrayList<>();

        for (var entry : container.getElementValues().entrySet()) {
            if (!entry.getKey().getSimpleName().contentEquals("value")) {
                continue;
            }

            Object raw = entry.getValue().getValue();

            if (!(raw instanceof List<?> list)) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Repeatable annotation container has non-list 'value': " + raw
                );
                return names;
            }

            for (Object item : list) {
                if (!(item instanceof AnnotationValue av)) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "[MTaaS] Unexpected element inside repeatable annotation container: " + item
                    );
                    continue;
                }

                Object nested = av.getValue();
                if (!(nested instanceof AnnotationMirror am)) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "[MTaaS] Repeatable annotation item is not AnnotationMirror: " + nested
                    );
                    continue;
                }

                String rn = readRelationName(am);
                if (rn == null || rn.isBlank()) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "[MTaaS] Empty relationName inside repeatable annotation: " + am
                    );
                    continue;
                }

                names.add(rn);
            }
        }

        return names;
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