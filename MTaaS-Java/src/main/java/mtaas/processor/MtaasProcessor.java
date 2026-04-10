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
    private final RelationCollector collector = new RelationCollector();

    // Стає корисним як захист від повторної генерації у разі нетипових сценаріїв
    private boolean generated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1) Збір в кожному раунді
        collector.collectAll(roundEnv, processingEnv.getMessager());

        // 2) Генерація тільки в фінальному раунді
        if (!roundEnv.processingOver()) {
            // Повертаємо true — ми “забрали” ці анотації (це норм для single-processor проекту)
            return true;
        }

        // Якщо якимось чином process викличеться ще раз після processingOver()
        if (generated) {
            return true;
        }
        generated = true;

        Map<String, RelationParts> all = collector.byRelation;

        // 3) Фільтруємо complete relations
        Map<String, RelationParts> completeOnly = new LinkedHashMap<>();
        for (var e : all.entrySet()) {
            if (isComplete(e.getValue())) {
                completeOnly.put(e.getKey(), e.getValue());
            }
        }

        // 4) Генерація коду: тільки для complete relations
        Map<String, RelationSemantics> sema = new LinkedHashMap<>();
        var elements = processingEnv.getElementUtils();
        var types = processingEnv.getTypeUtils();

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
                processingEnv.getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Source generation failed: " + ex.getMessage()
                );
            }
        } else {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] Final round: no SEMANTICALLY valid relations; code generation skipped."
            );
        }

        // 5) YAML: важливий фікс — ПЕРЕЗАПИС, а не "skipping"
        //    Важливо: якщо completeOnly пустий — ми все одно перезаписуємо YAML пустим/без relations?
        //    Щоб не затирати попередній YAML "порожнім", залишаємо твою логіку:
        //    якщо completeOnly пустий — НЕ чіпаємо YAML.
        if (completeOnly.isEmpty()) {
            processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS] Final round: no COMPLETE relations; skipping YAML overwrite to avoid erasing previous file."
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

    /**
     * Пише META-INF/mtaas/spec.yaml у CLASS_OUTPUT.
     * Якщо файл вже існує, перезаписує його (фікс твоєї проблеми з "already exists, skipping").
     */
    private void writeOrOverwriteSpecYaml(String yaml) throws Exception {
        String resourcePath = "META-INF/mtaas/spec.yaml";
        try {
            // Перша спроба: створити (працює на clean build)
            FileObject fo = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);
            try (Writer w = fo.openWriter()) {
                w.write(yaml);
            }
        } catch (javax.annotation.processing.FilerException alreadyExists) {
            // Друга спроба: знайти існуючий і перезаписати через filesystem path
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
        // OutputModelComparer у тебе не обов'язковий для complete,
        // але якщо ти хочеш — додай: && !r.getComparers().isEmpty()
    }
}
