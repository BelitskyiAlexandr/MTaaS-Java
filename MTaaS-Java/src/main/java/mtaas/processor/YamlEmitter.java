package mtaas.processor;

import java.util.Map;
import javax.lang.model.element.TypeElement;

//AddRelationSourceCode + AddFunctionSourceCode temp logic
final class YamlEmitter {
    static String emit(Map<String, RelationParts> relations) {
        StringBuilder sb = new StringBuilder();
        sb.append("relations:\n");
        for (var e : relations.entrySet()) {
            RelationParts r = e.getValue();
            sb.append("  - name: ").append(r.getRelationName()).append("\n");
            sb.append("    artifactEntry: ").append(fqn(r.getArtifactEntry())).append("\n");
            sb.append("    dataGenerator: ").append(fqn(r.getDataGenerator())).append("\n");
            sb.append("    inputMetamorphosis: ").append(fqn(r.getInputMetamorphosis()))
                    .append("\n");
            sb.append("    outputMetamorphosis: ").append(fqn(r.getOutputMetamorphosis()))
                    .append("\n");
            sb.append("    outputModelComparer: ").append(fqn(r.getOutputModelComparer()))
                    .append("\n");
        }
        return sb.toString();
    }

    private static String fqn(TypeElement e) {
        return e == null ? "null" : e.getQualifiedName().toString();
    }
}
