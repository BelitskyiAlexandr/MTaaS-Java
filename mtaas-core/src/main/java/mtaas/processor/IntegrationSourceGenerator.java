package mtaas.processor;

import java.io.Writer;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
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
        emitRegistry(processingEnv, sema);
    }

    private static void emitAdapter(ProcessingEnvironment processingEnv,
                                    RelationSemantics s) throws Exception {

        String slug = NameUtil.slug(s.relationName);
        String packageName = "mtaas.generated." + slug;
        String adapterSimpleName = "MetamorphicServiceAdapter_" + slug;
        String functionSimpleName = "MetamorphicFunction_" + slug;
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
                .createSourceFile(packageName + "." + adapterSimpleName);

        try (Writer writer = file.openWriter()) {
            writer.write(sb.toString());
        }
    }

    private static void emitRegistry(ProcessingEnvironment processingEnv,
                                     Map<String, RelationSemantics> sema) throws Exception {

        String packageName = "mtaas.generated.registry";
        String registrySimpleName = "MetamorphicServiceRegistry";

        StringBuilder sb = new StringBuilder();

        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import java.util.LinkedHashMap;\n");
        sb.append("import java.util.Map;\n");
        sb.append("import ").append(ADAPTER_INTERFACE_FQCN).append(";\n");

        for (RelationSemantics s : sema.values()) {
            String slug = NameUtil.slug(s.relationName);
            sb.append("import mtaas.generated.")
                    .append(slug)
                    .append(".MetamorphicServiceAdapter_")
                    .append(slug)
                    .append(";\n");
        }

        sb.append("\n");
        sb.append("public final class ").append(registrySimpleName).append(" {\n\n");

        sb.append("    private ").append(registrySimpleName).append("() {\n");
        sb.append("    }\n\n");

        sb.append("    public static Map<String, MetamorphicServiceAdapter> adapters() {\n");
        sb.append("        Map<String, MetamorphicServiceAdapter> map = new LinkedHashMap<>();\n");

        for (RelationSemantics s : sema.values()) {
            String slug = NameUtil.slug(s.relationName);
            sb.append("        map.put(\"")
                    .append(escapeJava(s.relationName))
                    .append("\", new MetamorphicServiceAdapter_")
                    .append(slug)
                    .append("());\n");
        }

        sb.append("        return map;\n");
        sb.append("    }\n");
        sb.append("}\n");

        JavaFileObject file = processingEnv.getFiler()
                .createSourceFile(packageName + "." + registrySimpleName);

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