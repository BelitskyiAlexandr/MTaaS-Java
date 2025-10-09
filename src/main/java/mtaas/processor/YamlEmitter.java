package mtaas.processor;

import javax.lang.model.element.TypeElement;
import java.util.Map;

//AddRelationSourceCode + AddFunctionSourceCode temp logic
final class YamlEmitter {
    static String emit(Map<String, RelationParts> relations) {
        StringBuilder sb = new StringBuilder();
        sb.append("relations:\n");
        for (var e : relations.entrySet()) {
            RelationParts r = e.getValue();
            sb.append("  - name: ").append(r.relationName).append("\n");
            sb.append("    artifactEntry: ").append(fqn(r.artifactEntry)).append("\n");
            sb.append("    dataGenerator: ").append(fqn(r.dataGenerator)).append("\n");
            sb.append("    inputMetamorphosis: ").append(fqn(r.inputMetamorphosis)).append("\n");
            sb.append("    outputMetamorphosis: ").append(fqn(r.outputMetamorphosis)).append("\n");
            sb.append("    outputModelComparer: ").append(fqn(r.outputModelComparer)).append("\n");
        }
        return sb.toString();
    }
    private static String fqn(TypeElement e) { return e == null ? "null" : e.getQualifiedName().toString(); }
}