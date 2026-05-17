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
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
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
@SupportedOptions({
        MtaasProcessor.DEBUG_OPTION
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class MtaasProcessor extends AbstractProcessor {

    static final String DEBUG_OPTION = "mtaas.debug";

    private static final String INTEGRATION_ADAPTER_FQCN =
            "mtaas.integration.api.MetamorphicServiceAdapter";

    private RelationCollector collector;
    private boolean generated = false;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        boolean debugEnabled = Boolean.parseBoolean(
                processingEnv.getOptions().get(DEBUG_OPTION)
        );

        this.collector = new RelationCollector(debugEnabled);
    }

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

        Map<String, RelationParts> completeOnly = collectCompleteRelations();
        Map<String, RelationSemantics> semantics = buildRelationSemantics(completeOnly);

        emitSources(semantics);
        emitYaml(completeOnly);

        return true;
    }

    private Map<String, RelationParts> collectCompleteRelations() {
        Map<String, RelationParts> completeOnly = new LinkedHashMap<>();

        for (Map.Entry<String, RelationParts> entry : collector.byRelation.entrySet()) {
            if (isComplete(entry.getValue())) {
                completeOnly.put(entry.getKey(), entry.getValue());
            }
        }

        return completeOnly;
    }

    private Map<String, RelationSemantics> buildRelationSemantics(Map<String, RelationParts> completeOnly) {
        Map<String, RelationSemantics> semantics = new LinkedHashMap<>();

        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();

        for (Map.Entry<String, RelationParts> entry : completeOnly.entrySet()) {
            RelationSemantics relationSemantics = RelationSemantics.build(
                    entry.getKey(),
                    entry.getValue(),
                    elements,
                    types,
                    processingEnv.getMessager()
            );

            if (relationSemantics != null) {
                semantics.put(entry.getKey(), relationSemantics);
            }
        }

        return semantics;
    }

    private void emitSources(Map<String, RelationSemantics> semantics) {
        if (semantics.isEmpty()) {
            return;
        }

        try {
            CoreSourceGenerator.emitSources(processingEnv, semantics);

            if (isIntegrationEnabled()) {
                IntegrationSourceGenerator.emitSources(processingEnv, semantics);
            }
        } catch (Exception ex) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.ERROR,
                    "[MTaaS] Source generation failed: " + ex.getMessage()
            );
        }
    }

    private void emitYaml(Map<String, RelationParts> completeOnly) {
        if (completeOnly.isEmpty()) {
            return;
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
    }

    private boolean isIntegrationEnabled() {
        return processingEnv.getElementUtils().getTypeElement(INTEGRATION_ADAPTER_FQCN) != null;
    }

    private void writeOrOverwriteSpecYaml(String yaml) throws Exception {
        String resourcePath = "META-INF/mtaas/spec.yaml";

        try {
            FileObject fileObject = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);

            try (Writer writer = fileObject.openWriter()) {
                writer.write(yaml);
            }
        } catch (javax.annotation.processing.FilerException alreadyExists) {
            FileObject existing = processingEnv.getFiler()
                    .getResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);

            Path path = Path.of(existing.toUri());
            Files.createDirectories(path.getParent());
            Files.writeString(path, yaml, StandardCharsets.UTF_8);
        }
    }

    private static boolean isComplete(RelationParts relationParts) {
        return relationParts.getArtifactEntry() != null
                && relationParts.getDataGenerator() != null
                && !relationParts.getInputMetas().isEmpty()
                && !relationParts.getOutputMetas().isEmpty();
    }
}