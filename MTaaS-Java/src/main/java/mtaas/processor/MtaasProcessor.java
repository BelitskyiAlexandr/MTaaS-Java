package mtaas.processor;

import com.google.auto.service.AutoService;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.FilerException;
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

        if (roundEnv.processingOver() && !generated && !collector.byRelation.isEmpty()) {
            String yaml = YamlEmitter.emit(collector.byRelation);
            try {
                FileObject file = processingEnv.getFiler()
                        .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/mtaas/spec.yaml");
                try (Writer w = file.openWriter()) {
                    w.write(yaml);
                }
                generated = true;
            } catch (javax.annotation.processing.FilerException fe) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "spec.yaml already exists, skipping: " + fe.getMessage());
                generated = true;
            } catch (Exception ex) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Write error: " + ex.getMessage());
            }
        }
        return true;
    }
}
