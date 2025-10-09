package mtaas.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes({
        "mtaas.annotations.ArtifactEntry",
        "mtaas.annotations.DataGenerator",
        "mtaas.annotations.InputMetamorphosis",
        "mtaas.annotations.OutputMetamorphosis",
        "mtaas.annotations.OutputModelComparer"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public final class MtaasProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        RelationCollector collector = new RelationCollector();
        collector.collectAll(roundEnv, processingEnv.getMessager());

        for (RelationParts relation : collector.byRelation.values()) {
            if (!relation.isReady()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Not all required parts for relation '" + relation.relationName + "'");
                continue;
            }
            // тут перевірки методи, типи тощо
        }

        // Генерація YAML (як у C# AddSource)
        String yaml = YamlEmitter.emit(collector.byRelation);

        try {
            FileObject file = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/mtaas/spec.yaml");
            try (Writer w = file.openWriter()) { w.write(yaml); }
        } catch (Exception ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Write error: " + ex.getMessage());
        }

        return false;
    }
}
