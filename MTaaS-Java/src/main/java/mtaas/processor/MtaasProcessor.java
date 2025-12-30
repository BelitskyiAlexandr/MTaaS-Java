package mtaas.processor;

import com.google.auto.service.AutoService;
import java.io.Writer;
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
        "mtaas.annotations.DataGenerator",
        "mtaas.annotations.InputMetamorphosis",
        "mtaas.annotations.OutputMetamorphosis",
        "mtaas.annotations.OutputModelComparer"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class MtaasProcessor extends AbstractProcessor {
    private final RelationCollector collector = new RelationCollector();
    private boolean generated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        collector.collectAll(roundEnv, processingEnv.getMessager());

        if (!roundEnv.processingOver()) {
            return true;
        }

        Map<String, RelationParts> all = collector.byRelation;

        Map<String, RelationParts> completeOnly = new LinkedHashMap<>();
        for (var e : all.entrySet()) {
            if (isComplete(e.getValue())) {
                completeOnly.put(e.getKey(), e.getValue());
            }
        }

        if (completeOnly.isEmpty()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] Final round: no COMPLETE relations; skipping YAML/code generation to avoid overwriting with partial state.");
            return true;
        }

        Map<String, RelationSemantics> sema = new LinkedHashMap<>();
        var elements = processingEnv.getElementUtils();
        var types    = processingEnv.getTypeUtils();
        for (var e : completeOnly.entrySet()) {
            RelationSemantics s = RelationSemantics.build(
                    e.getKey(), e.getValue(), elements, types, processingEnv.getMessager());
            if (s != null) {
                sema.put(e.getKey(), s);
            }
        }
        if (!sema.isEmpty()) {
            try {
                SourceGenerator.emitSources(processingEnv, sema);
            } catch (Exception ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "[MTaaS] Source generation failed: " + ex.getMessage());
            }
        }

        try {
            String yaml = YamlEmitter.emit(completeOnly);
            FileObject fo = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/mtaas/spec.yaml");
            try (Writer w = fo.openWriter()) {
                w.write(yaml);
            }
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] spec.yaml written with " + completeOnly.size() + " complete relation(s)");
        } catch (javax.annotation.processing.FilerException fe) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "[MTaaS] spec.yaml already exists, skipping: " + fe.getMessage());
        } catch (Exception ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "[MTaaS] YAML write error: " + ex.getMessage());
        }
        return true;
    }

    private static boolean isComplete(RelationParts r) {
        return r.getArtifactEntry() != null
                && r.getDataGenerator() != null
                && !r.getInputMetas().isEmpty()
                && !r.getOutputMetas().isEmpty();
    }
}