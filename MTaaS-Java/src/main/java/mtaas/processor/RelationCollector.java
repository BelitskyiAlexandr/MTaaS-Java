package mtaas.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import mtaas.annotations.ArtifactEntry;
import mtaas.annotations.DataGenerator;
import mtaas.annotations.InputMetamorphosis;
import mtaas.annotations.OutputMetamorphosis;
import mtaas.annotations.OutputModelComparer;

public final class RelationCollector {

    final Map<String, RelationParts> byRelation = new LinkedHashMap<>();

    void collectAll(RoundEnvironment roundEnv, Messager messager) {
        collectByAnnotation(roundEnv, InputMetamorphosis.class, Role.INPUT_METAMORPHOSIS, messager);
        collectByAnnotation(roundEnv, OutputMetamorphosis.class, Role.OUTPUT_METAMORPHOSIS,
                messager);
        collectByAnnotation(roundEnv, ArtifactEntry.class, Role.ARTIFACT_ENTRY, messager);
        collectByAnnotation(roundEnv, DataGenerator.class, Role.DATA_GENERATOR, messager);
        collectByAnnotation(roundEnv, OutputModelComparer.class, Role.OUTPUT_MODEL_COMPARER,
                messager);
    }

    private <A extends java.lang.annotation.Annotation>
            void collectByAnnotation(RoundEnvironment roundEnv,
                             Class<A> annotationType,
                             Role role,
                             Messager messager) {

        for (Element e : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (e.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR,"Annotation @"
                        + annotationType.getSimpleName() + " supports only for classes");
                continue;
            }

            TypeElement type = (TypeElement) e;
            String relationName = readRelationName(type, annotationType);

            RelationParts parts = byRelation.computeIfAbsent(relationName, RelationParts::new);
            try {
                parts.put(role, type);
            } catch (IllegalStateException dup) {
                messager.printMessage(Diagnostic.Kind.ERROR, dup.getMessage());
            }
        }
    }

    private <A extends java.lang.annotation.Annotation>
            String readRelationName(TypeElement type, Class<A> annotationType) {
        for (AnnotationMirror am : type.getAnnotationMirrors()) {
            if (am.getAnnotationType().toString().equals(annotationType.getCanonicalName())) {
                return AnnotationValueReader.readRelationName(am);
            }
        }
        return "";
    }
}
