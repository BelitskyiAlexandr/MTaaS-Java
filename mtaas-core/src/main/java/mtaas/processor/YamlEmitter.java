package mtaas.processor;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;

final class YamlEmitter {
    static String emit(Map<String, RelationParts> relations) {
        StringBuilder sb = new StringBuilder();
        sb.append("relations:\n");
        relations.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    String name = e.getKey();
                    RelationParts r = e.getValue();
                    sb.append("  - name: ").append(escape(name)).append("\n");
                    sb.append("    artifactEntry: ").append(fqn(r.getArtifactEntry())).append("\n");
                    sb.append("    dataGenerator: ").append(fqn(r.getDataGenerator())).append("\n");
                    writeList(sb, "inputMetamorphoses",  r.getInputMetas());
                    writeList(sb, "outputMetamorphoses", r.getOutputMetas());
                    writeList(sb, "outputModelComparers", r.getComparers());
                    sb.append("\n");
                });
        return sb.toString();
    }

    private static void writeList(StringBuilder sb, String key, List<TypeElement> list) {
        sb.append("    ").append(key).append(":\n");
        if (list.isEmpty()) {
            sb.append("      []\n");
        } else {
            for (TypeElement e : list) {
                sb.append("      - ").append(fqn(e)).append("\n");
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        if (s.isBlank() || s.contains(":") || s.contains("#") || s.contains(" ")) {
            return "\"" + s.replace("\"", "\\\"") + "\"";
        }
        return s;
    }
    private static String fqn(TypeElement e) {
        return e == null ? "null" : e.getQualifiedName().toString();
    }
}
