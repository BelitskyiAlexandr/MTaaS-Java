package mtaas.processor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
        // Спочатку звичайні анотації
        collectByAnnotation(roundEnv, ArtifactEntry.class, Role.ARTIFACT_ENTRY, messager);
        collectByAnnotation(roundEnv, DataGenerator.class, Role.DATA_GENERATOR, messager);
        collectByAnnotation(roundEnv, InputMetamorphosis.class, Role.INPUT_METAMORPHOSIS, messager);
        collectByAnnotation(roundEnv, OutputMetamorphosis.class, Role.OUTPUT_METAMORPHOSIS, messager);
        collectByAnnotation(roundEnv, OutputModelComparer.class, Role.OUTPUT_MODEL_COMPARER, messager);

        // Потім container-анотації для repeatable
        collectByContainerAnnotation(roundEnv, ArtifactEntry.List.class, Role.ARTIFACT_ENTRY, messager);
        collectByContainerAnnotation(roundEnv, DataGenerator.List.class, Role.DATA_GENERATOR, messager);
        collectByContainerAnnotation(roundEnv, InputMetamorphosis.List.class, Role.INPUT_METAMORPHOSIS, messager);
        collectByContainerAnnotation(roundEnv, OutputMetamorphosis.List.class, Role.OUTPUT_METAMORPHOSIS, messager);
        collectByContainerAnnotation(roundEnv, OutputModelComparer.List.class, Role.OUTPUT_MODEL_COMPARER, messager);

        debugDumpCollectedRelations(messager);
    }

    private <A extends Annotation> void collectByAnnotation(RoundEnvironment roundEnv,
                                                            Class<A> annotationType,
                                                            Role role,
                                                            Messager messager) {

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "[MTaaS][DEBUG] Scanning direct annotation @" + annotationType.getCanonicalName()
        );

        for (Element e : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (e.getKind() != ElementKind.CLASS) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Annotation @" + annotationType.getSimpleName() + " supports only classes",
                        e
                );
                continue;
            }

            TypeElement type = (TypeElement) e;

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] Found element for @" + annotationType.getSimpleName()
                            + ": " + type.getQualifiedName()
            );

            List<String> relationNames = readRelationNames(type, annotationType, messager);

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] relationNames for " + type.getQualifiedName()
                            + ", role=" + role + " => " + relationNames
            );

            if (relationNames.isEmpty()) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Can't read relationName from @" + annotationType.getSimpleName()
                                + " on " + type.getQualifiedName(),
                        type
                );
                continue;
            }

            addTypeToRelations(type, relationNames, role, messager);
        }
    }

    private <A extends Annotation> void collectByContainerAnnotation(RoundEnvironment roundEnv,
                                                                     Class<A> containerAnnotationType,
                                                                     Role role,
                                                                     Messager messager) {

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "[MTaaS][DEBUG] Scanning container annotation @" + containerAnnotationType.getCanonicalName()
        );

        for (Element e : roundEnv.getElementsAnnotatedWith(containerAnnotationType)) {
            if (e.getKind() != ElementKind.CLASS) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Annotation @" + containerAnnotationType.getSimpleName() + " supports only classes",
                        e
                );
                continue;
            }

            TypeElement type = (TypeElement) e;

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] Found element for container @" + containerAnnotationType.getSimpleName()
                            + ": " + type.getQualifiedName()
            );

            List<String> relationNames = readRelationNamesFromContainerOnly(type, containerAnnotationType, messager);

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] container relationNames for " + type.getQualifiedName()
                            + ", role=" + role + " => " + relationNames
            );

            if (relationNames.isEmpty()) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Can't read relationNames from container @" + containerAnnotationType.getSimpleName()
                                + " on " + type.getQualifiedName(),
                        type
                );
                continue;
            }

            addTypeToRelations(type, relationNames, role, messager);
        }
    }

    private void addTypeToRelations(TypeElement type,
                                    List<String> relationNames,
                                    Role role,
                                    Messager messager) {

        for (String relationName : relationNames) {
            if (relationName == null || relationName.isBlank()) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Empty relationName for role=" + role
                                + " on " + type.getQualifiedName(),
                        type
                );
                continue;
            }

            RelationParts parts = byRelation.computeIfAbsent(relationName, RelationParts::new);

            try {
                parts.put(role, type);
                messager.printMessage(
                        Diagnostic.Kind.NOTE,
                        "[MTaaS][DEBUG] Added role=" + role
                                + ", relation=" + relationName
                                + ", class=" + type.getQualifiedName()
                );
            } catch (IllegalStateException dup) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS][" + relationName + "] " + dup.getMessage()
                                + " (role=" + role + ", class=" + type.getQualifiedName() + ")",
                        type
                );
            }
        }
    }

    /**
     * Читає relationName для конкретного типу анотації:
     * - якщо на класі стоїть одиночна анотація -> повертає її relationName
     * - якщо на класі стоїть container repeatable-анотації -> теж повертає всі relationName
     *
     * Це корисно, бо іноді компілятор віддає repeatable анотацію як контейнер.
     */
    private <A extends Annotation> List<String> readRelationNames(TypeElement type,
                                                                  Class<A> annotationType,
                                                                  Messager messager) {

        String annTypeName = annotationType.getCanonicalName();
        String containerTypeName = annTypeName + "$List";

        List<String> names = new ArrayList<>();

        debugAnnotationMirrors(type, messager);

        for (AnnotationMirror am : type.getAnnotationMirrors()) {
            String applied = am.getAnnotationType().toString();

            if (applied.equals(annTypeName)) {
                String rn = AnnotationValueReader.readRelationName(am);
                if (rn != null && !rn.isBlank()) {
                    names.add(rn);
                }
                continue;
            }

            if (applied.equals(containerTypeName)) {
                names.addAll(AnnotationValueReader.readRelationNamesFromContainer(am, messager));
            }
        }

        return unique(names);
    }

    /**
     * Читає relationNames тільки з container-анотації.
     * Потрібно для collectByContainerAnnotation(...), щоб не дублювати direct-анотації.
     */
    private <A extends Annotation> List<String> readRelationNamesFromContainerOnly(TypeElement type,
                                                                                   Class<A> containerAnnotationType,
                                                                                   Messager messager) {

        String containerTypeName = containerAnnotationType.getCanonicalName();
        List<String> names = new ArrayList<>();

        debugAnnotationMirrors(type, messager);

        for (AnnotationMirror am : type.getAnnotationMirrors()) {
            String applied = am.getAnnotationType().toString();

            if (applied.equals(containerTypeName)) {
                names.addAll(AnnotationValueReader.readRelationNamesFromContainer(am, messager));
            }
        }

        return unique(names);
    }

    private void debugAnnotationMirrors(TypeElement type, Messager messager) {
        for (AnnotationMirror am : type.getAnnotationMirrors()) {
            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] " + type.getQualifiedName()
                            + " has annotation mirror: "
                            + am.getAnnotationType().toString()
                            + " => " + am
            );
        }
    }

    private void debugDumpCollectedRelations(Messager messager) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                "[MTaaS][DEBUG] Collected relations count = " + byRelation.size()
        );

        for (Map.Entry<String, RelationParts> entry : byRelation.entrySet()) {
            String relationName = entry.getKey();
            RelationParts parts = entry.getValue();

            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] relation=" + relationName
                            + ", artifactEntry=" + fqn(parts.getArtifactEntry())
                            + ", dataGenerator=" + fqn(parts.getDataGenerator())
                            + ", inputMetamorphoses=" + fqns(parts.getInputMetas())
                            + ", outputMetamorphoses=" + fqns(parts.getOutputMetas())
                            + ", outputModelComparers=" + fqns(parts.getComparers())
            );
        }
    }

    private static List<String> unique(List<String> input) {
        List<String> result = new ArrayList<>();
        for (String item : input) {
            if (item == null) {
                continue;
            }
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    private static String fqn(TypeElement e) {
        return e == null ? "null" : e.getQualifiedName().toString();
    }

    private static String fqns(List<TypeElement> list) {
        List<String> names = new ArrayList<>();
        for (TypeElement e : list) {
            names.add(e == null ? "null" : e.getQualifiedName().toString());
        }
        return names.toString();
    }
}