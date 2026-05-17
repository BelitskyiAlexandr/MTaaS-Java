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

    private final boolean debugEnabled;

    public RelationCollector(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    void collectAll(RoundEnvironment roundEnv, Messager messager) {
        collectByAnnotation(roundEnv, ArtifactEntry.class, Role.ARTIFACT_ENTRY, messager);
        collectByAnnotation(roundEnv, DataGenerator.class, Role.DATA_GENERATOR, messager);
        collectByAnnotation(roundEnv, InputMetamorphosis.class, Role.INPUT_METAMORPHOSIS, messager);
        collectByAnnotation(roundEnv, OutputMetamorphosis.class, Role.OUTPUT_METAMORPHOSIS, messager);
        collectByAnnotation(roundEnv, OutputModelComparer.class, Role.OUTPUT_MODEL_COMPARER, messager);

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
        debug(messager, "Scanning direct annotation @" + annotationType.getCanonicalName());

        for (Element element : roundEnv.getElementsAnnotatedWith(annotationType)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Annotation @" + annotationType.getSimpleName() + " supports only classes",
                        element
                );
                continue;
            }

            TypeElement type = (TypeElement) element;

            debug(
                    messager,
                    "Found element for @" + annotationType.getSimpleName()
                            + ": " + type.getQualifiedName()
            );

            List<String> relationNames = readRelationNames(type, annotationType, messager);

            debug(
                    messager,
                    "relationNames for " + type.getQualifiedName()
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
        debug(messager, "Scanning container annotation @" + containerAnnotationType.getCanonicalName());

        for (Element element : roundEnv.getElementsAnnotatedWith(containerAnnotationType)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS] Annotation @" + containerAnnotationType.getSimpleName() + " "
                                + "supports only classes", element
                );
                continue;
            }

            TypeElement type = (TypeElement) element;

            debug(
                    messager,
                    "Found element for container @" + containerAnnotationType.getSimpleName()
                            + ": " + type.getQualifiedName()
            );

            List<String> relationNames = readRelationNamesFromContainerOnly(type, containerAnnotationType, messager);

            debug(
                    messager,
                    "container relationNames for " + type.getQualifiedName()
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
                debug(
                        messager,
                        "Added role=" + role
                                + ", relation=" + relationName
                                + ", class=" + type.getQualifiedName()
                );
            } catch (IllegalStateException ex) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[MTaaS][" + relationName + "] " + ex.getMessage()
                                + " (role=" + role + ", class=" + type.getQualifiedName() + ")",
                        type
                );
            }
        }
    }

    private <A extends Annotation> List<String> readRelationNames(TypeElement type,
                                                                  Class<A> annotationType,
                                                                  Messager messager) {
        String annotationTypeName = annotationType.getCanonicalName();
        String containerTypeName = annotationTypeName + "$List";

        List<String> names = new ArrayList<>();

        debugAnnotationMirrors(type, messager);

        for (AnnotationMirror annotationMirror : type.getAnnotationMirrors()) {
            String appliedAnnotation = annotationMirror.getAnnotationType().toString();

            if (appliedAnnotation.equals(annotationTypeName)) {
                String relationName = AnnotationValueReader.readRelationName(annotationMirror);
                if (relationName != null && !relationName.isBlank()) {
                    names.add(relationName);
                }
                continue;
            }

            if (appliedAnnotation.equals(containerTypeName)) {
                names.addAll(AnnotationValueReader.readRelationNamesFromContainer(annotationMirror, messager));
            }
        }

        return unique(names);
    }

    private <A extends Annotation> List<String> readRelationNamesFromContainerOnly(TypeElement type,
                                                                                   Class<A> containerAnnotationType,
                                                                                   Messager messager) {
        String containerTypeName = containerAnnotationType.getCanonicalName();
        List<String> names = new ArrayList<>();

        debugAnnotationMirrors(type, messager);

        for (AnnotationMirror annotationMirror : type.getAnnotationMirrors()) {
            String appliedAnnotation = annotationMirror.getAnnotationType().toString();

            if (appliedAnnotation.equals(containerTypeName)) {
                names.addAll(AnnotationValueReader.readRelationNamesFromContainer(annotationMirror, messager));
            }
        }

        return unique(names);
    }

    private void debugAnnotationMirrors(TypeElement type, Messager messager) {
        if (!debugEnabled) {
            return;
        }

        for (AnnotationMirror annotationMirror : type.getAnnotationMirrors()) {
            debug(
                    messager,
                    type.getQualifiedName()
                            + " has annotation mirror: "
                            + annotationMirror.getAnnotationType()
                            + " => " + annotationMirror
            );
        }
    }

    private void debugDumpCollectedRelations(Messager messager) {
        if (!debugEnabled) {
            return;
        }

        debug(messager, "Collected relations count = " + byRelation.size());

        for (Map.Entry<String, RelationParts> entry : byRelation.entrySet()) {
            String relationName = entry.getKey();
            RelationParts parts = entry.getValue();

            debug(
                    messager,
                    "relation=" + relationName
                            + ", artifactEntry=" + fqn(parts.getArtifactEntry())
                            + ", dataGenerator=" + fqn(parts.getDataGenerator())
                            + ", inputMetamorphoses=" + fqns(parts.getInputMetas())
                            + ", outputMetamorphoses=" + fqns(parts.getOutputMetas())
                            + ", outputModelComparers=" + fqns(parts.getComparers())
            );
        }
    }

    private void debug(Messager messager, String message) {
        if (debugEnabled) {
            messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "[MTaaS][DEBUG] " + message
            );
        }
    }

    private static List<String> unique(List<String> input) {
        List<String> result = new ArrayList<>();
        for (String item : input) {
            if (item != null && !result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    private static String fqn(TypeElement element) {
        return element == null ? "null" : element.getQualifiedName().toString();
    }

    private static String fqns(List<TypeElement> elements) {
        List<String> names = new ArrayList<>();
        for (TypeElement element : elements) {
            names.add(element == null ? "null" : element.getQualifiedName().toString());
        }
        return names.toString();
    }
}