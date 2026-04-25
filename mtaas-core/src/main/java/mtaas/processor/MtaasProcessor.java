package mtaas.processor;

import com.google.auto.service.AutoService;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes({
        "mtaas.annotations.ArtifactEntry",
        "mtaas.annotations.ArtifactEntry.List",
        "mtaas.annotations.DataGenerator",
        "mtaas.annotations.DataGenerator.List",
        "mtaas.annotations.InputMetamorphosis",
        "mtaas.annotations.InputMetamorphosis.List",
        "mtaas.annotations.OutputMetamorphosis",
        "mtaas.annotations.OutputMetamorphosis.List",
        "mtaas.annotations.OutputModelComparer",
        "mtaas.annotations.OutputModelComparer.List"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class MtaasProcessor extends AbstractProcessor {

    private static final String INTEGRATION_ADAPTER_FQCN =
            "mtaas.integration.api.MetamorphicServiceAdapter";

    private final RelationCollector collector = new RelationCollector();
    private boolean generated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        collector.collectAll(roundEnv, processingEnv.getMessager());

        if (!roundEnv.processingOver()) {
            return true;
        }

        if (generated) {
            return true;
        }
        generated = true;

        Map<String, RelationParts> all = collector.byRelation;

        Map<String, RelationParts> completeOnly = new LinkedHashMap<>();
        for (var entry : all.entrySet()) {
            if (isComplete(entry.getValue())) {
                completeOnly.put(entry.getKey(), entry.getValue());
            }
        }

        Map<String, RelationSemantics> sema = new LinkedHashMap<>();
        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();

        for (var entry : completeOnly.entrySet()) {
            RelationSemantics semantics = RelationSemantics.build(
                    entry.getKey(),
                    entry.getValue(),
                    elements,
                    types,
                    processingEnv.getMessager()
            );
            if (semantics != null) {
                sema.put(entry.getKey(), semantics);
            }
        }

        if (!sema.isEmpty()) {
            try {
                CoreSourceGenerator.emitSources(processingEnv, sema);

                if (isIntegrationEnabled()) {
                    IntegrationSourceGenerator.emitSources(processingEnv, sema);
                }
            } catch (Exception ex) {
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Source generation failed: " + ex.getMessage()
                );
            }
        } else {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] Final round: no semantically valid relations; code generation skipped."
            );
        }

        if (completeOnly.isEmpty()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] Final round: no complete relations; YAML overwrite skipped."
            );
            return true;
        }

        try {
            String yaml = YamlEmitter.emit(completeOnly);
            writeOrOverwriteSpecYaml(yaml);
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] spec.yaml written with " + completeOnly.size() + " complete relation(s)"
            );
        } catch (Exception ex) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "[MTaaS] YAML write error: " + ex.getMessage()
            );
        }

        return true;
    }

    private boolean isIntegrationEnabled() {
        return processingEnv.getElementUtils().getTypeElement(INTEGRATION_ADAPTER_FQCN) != null;
    }

    private void writeOrOverwriteSpecYaml(String yaml) throws Exception {
        String resourcePath = "META-INF/mtaas/spec.yaml";
        try {
            FileObject fo = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);
            try (Writer w = fo.openWriter()) {
                w.write(yaml);
            }
        } catch (javax.annotation.processing.FilerException alreadyExists) {
            FileObject existing = processingEnv.getFiler()
                    .getResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);

            Path p = Path.of(existing.toUri());
            Files.createDirectories(p.getParent());
            Files.writeString(p, yaml, StandardCharsets.UTF_8);
        }
    }

    private static boolean isComplete(RelationParts r) {
        return r.getArtifactEntry() != null
                && r.getDataGenerator() != null
                && !r.getInputMetas().isEmpty()
                && !r.getOutputMetas().isEmpty();
    }
}