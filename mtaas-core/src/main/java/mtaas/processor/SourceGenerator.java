package mtaas.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class SourceGenerator {
    static void emitSources(ProcessingEnvironment env, Map<String, RelationSemantics> map) throws IOException {
        for (var e : map.entrySet()) {
            String name = e.getKey();
            RelationSemantics s = e.getValue();
            String pkg = "mtaas.generated." + NameUtil.slug(name);

            String relName = sName("MetamorphicRelation_", name);
            String funName = sName("MetamorphicFunction_", name);

            write(env, pkg + "." + relName, buildRelationSource(pkg, relName, s));
            write(env, pkg + "." + funName, buildFunctionSource(pkg, funName, relName, s));
        }
    }

    private static String sName(String prefix, String relation) {
        return prefix + NameUtil.slug(relation);
    }

    private static void write(ProcessingEnvironment env, String fqn, String src) throws IOException {
        JavaFileObject jfo = env.getFiler().createSourceFile(fqn);
        try (Writer w = jfo.openWriter()) { w.write(src); }
    }

    private static String fqn(TypeElement e) { return e.getQualifiedName().toString(); }

    // ====== RELATION ======
    private static String buildRelationSource(String pkg, String simple, RelationSemantics s) {
        String inOwner = fqn(s.inputMetaClass);
        String outOwner = fqn(s.outputMetaClass);
        String artOwner = fqn(s.artifactClass);

        String inCall  = s.inputIsStatic
                ? inOwner + "." + s.inputMethod.getSimpleName()
                : "inputMeta." + s.inputMethod.getSimpleName();
        String outCall = s.outputIsStatic
                ? outOwner + "." + s.outputMethod.getSimpleName()
                : "outputMeta." + s.outputMethod.getSimpleName();
        String artCall = s.artifactIsStatic
                ? artOwner + "." + s.artifactMethod.getSimpleName()
                : "artifact." + s.artifactMethod.getSimpleName();

        StringBuilder sb = new StringBuilder(2048);
        sb.append("package ").append(pkg).append(";\n\n");
        sb.append("public final class ").append(simple).append(" {\n");

        // інстансні поля тільки якщо метод не static
        if (!s.inputIsStatic)  sb.append("  private final ").append(inOwner).append(" inputMeta = new ").append(inOwner).append("();\n");
        if (!s.outputIsStatic) sb.append("  private final ").append(outOwner).append(" outputMeta = new ").append(outOwner).append("();\n");
        if (!s.artifactIsStatic) sb.append("  private final ").append(artOwner).append(" artifact = new ").append(artOwner).append("();\n");
        if (s.comparerClass != null) {
            sb.append("  private final java.util.Comparator<").append(RelationSemantics.typeStr(s.outputType)).append("> comparer = new ")
                    .append(fqn(s.comparerClass)).append("();\n");
        }

        sb.append("  public boolean validate(").append(RelationSemantics.typeStr(s.inputType)).append(" input) {\n");
        sb.append("    ").append(RelationSemantics.typeStr(s.outputType)).append(" output1 = ").append(outCall)
                .append("(").append(artCall).append("(input));\n");
        sb.append("    ").append(RelationSemantics.typeStr(s.outputType)).append(" output2 = ").append(artCall)
                .append("(").append(inCall).append("(input));\n");

        if (s.comparerClass != null) {
            sb.append("    return comparer.compare(output1, output2) == 0;\n");
        } else {
            sb.append("    return java.util.Objects.equals(output1, output2);\n");
        }
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }

    private static String buildFunctionSource(String pkg, String simple, String relSimple, RelationSemantics s) {
        String dgOwner = fqn(s.dataGeneratorClass);

        String genCall = s.dataGeneratorIsStatic
                ? dgOwner + "." + s.dataGeneratorMethod.getSimpleName()
                : "generator." + s.dataGeneratorMethod.getSimpleName();

        StringBuilder sb = new StringBuilder(1536);
        sb.append("package ").append(pkg).append(";\n\n");
        sb.append("public final class ").append(simple).append(" {\n");

        if (!s.dataGeneratorIsStatic) {
            sb.append("  private final ").append(dgOwner).append(" generator = new ").append(dgOwner).append("();\n");
        }

        sb.append("  public boolean run(").append(RelationSemantics.typeStr(s.generatorParamType)).append(" model) {\n");
        sb.append("    ").append(RelationSemantics.typeStr(s.inputType)).append(" input = ").append(genCall).append("(model);\n");
        sb.append("    ").append(relSimple).append(" relation = new ").append(relSimple).append("();\n");
        sb.append("    return relation.validate(input);\n");
        sb.append("  }\n");

        sb.append("}\n");
        return sb.toString();
    }
}