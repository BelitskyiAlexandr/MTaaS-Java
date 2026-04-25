package mtaas.processor;

import java.io.Writer;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

final class IntegrationSourceGenerator {

    private static final String ADAPTER_INTERFACE_FQCN =
            "mtaas.integration.api.MetamorphicServiceAdapter";

    private IntegrationSourceGenerator() {
    }

    static void emitSources(ProcessingEnvironment processingEnv,
                            Map<String, RelationSemantics> sema) throws Exception {
        for (RelationSemantics s : sema.values()) {
            emitAdapter(processingEnv, s);
        }
    }

    private static void emitAdapter(ProcessingEnvironment processingEnv,
                                    RelationSemantics s) throws Exception {

        String packageName = "mtaas.generated." + NameUtil.slug(s.relationName);
        String adapterSimpleName = "MetamorphicServiceAdapter_" + NameUtil.slug(s.relationName);
        String functionSimpleName = "MetamorphicFunction_" + NameUtil.slug(s.relationName);
        String modelType = s.generatorParamType.toString();
        String relationNameLiteral = escapeJava(s.relationName);

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(packageName).append(";\n\n");

        sb.append("import ").append(ADAPTER_INTERFACE_FQCN).append(";\n");
        sb.append("import ").append(modelType).append(";\n\n");

        sb.append("public final class ").append(adapterSimpleName)
                .append(" implements MetamorphicServiceAdapter {\n\n");

        sb.append("    @Override\n");
        sb.append("    public String relationName() {\n");
        sb.append("        return \"").append(relationNameLiteral).append("\";\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public Class<?> inputType() {\n");
        sb.append("        return ").append(simpleName(modelType)).append(".class;\n");
        sb.append("    }\n\n");

        sb.append("    @Override\n");
        sb.append("    public boolean execute(Object model) {\n");
        sb.append("        return new ").append(functionSimpleName)
                .append("().run((").append(simpleName(modelType)).append(") model);\n");
        sb.append("    }\n");

        sb.append("}\n");

        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(packageName + "." + adapterSimpleName, new TypeElement[0]);

        try (Writer writer = file.openWriter()) {
            writer.write(sb.toString());
        }
    }

    private static String simpleName(String fqcn) {
        int idx = fqcn.lastIndexOf('.');
        return idx >= 0 ? fqcn.substring(idx + 1) : fqcn;
    }

    private static String escapeJava(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}