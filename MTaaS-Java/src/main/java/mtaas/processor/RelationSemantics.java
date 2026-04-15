package mtaas.processor;

import java.util.List;
import java.util.Objects;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

final class RelationSemantics {

        final String relationName;

        final TypeElement inputMetaClass;
        final ExecutableElement inputMethod;
        final boolean inputIsStatic;
        final TypeMirror inputType; // T

        final TypeElement outputMetaClass;
        final ExecutableElement outputMethod;
        final boolean outputIsStatic;
        final TypeMirror outputType;

        final TypeElement dataGeneratorClass;
        final ExecutableElement dataGeneratorMethod;
        final boolean dataGeneratorIsStatic;
        final TypeMirror generatorParamType;

        final TypeElement artifactClass;
        final ExecutableElement artifactMethod;
        final boolean artifactIsStatic;

        final TypeElement comparerClass;

        private RelationSemantics(String relationName,
                                  TypeElement inputMetaClass, ExecutableElement inputMethod, boolean inputIsStatic, TypeMirror inputType,
                                  TypeElement outputMetaClass, ExecutableElement outputMethod, boolean outputIsStatic, TypeMirror outputType,
                                  TypeElement dataGeneratorClass, ExecutableElement dataGeneratorMethod, boolean dataGeneratorIsStatic, TypeMirror generatorParamType,
                                  TypeElement artifactClass, ExecutableElement artifactMethod, boolean artifactIsStatic,
                                  TypeElement comparerClass) {
            this.relationName = relationName;
            this.inputMetaClass = inputMetaClass;
            this.inputMethod = inputMethod;
            this.inputIsStatic = inputIsStatic;
            this.inputType = inputType;
            this.outputMetaClass = outputMetaClass;
            this.outputMethod = outputMethod;
            this.outputIsStatic = outputIsStatic;
            this.outputType = outputType;
            this.dataGeneratorClass = dataGeneratorClass;
            this.dataGeneratorMethod = dataGeneratorMethod;
            this.dataGeneratorIsStatic = dataGeneratorIsStatic;
            this.generatorParamType = generatorParamType;
            this.artifactClass = artifactClass;
            this.artifactMethod = artifactMethod;
            this.artifactIsStatic = artifactIsStatic;
            this.comparerClass = comparerClass;
        }

        static RelationSemantics build(String relationName,
                                       RelationParts rp,
                                       Elements elements,
                                       Types types,
                                       Messager messager) {
            if (rp.getArtifactEntry() == null ||
                    rp.getDataGenerator() == null ||
                    rp.getInputMetas().isEmpty() ||
                    rp.getOutputMetas().isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        diag("Can't find all required parts for relation", relationName));
                return null;
            }

            TypeElement inputMetaClass = rp.getInputMetas().get(0);
            TypeElement outputMetaClass = rp.getOutputMetas().get(0);
            TypeElement artifactClass = rp.getArtifactEntry();
            TypeElement dataGeneratorClass = rp.getDataGenerator();
            TypeElement comparerClass = rp.getComparers().isEmpty() ? null : rp.getComparers().get(0);

            // 1) InputMetamorphosis: public, 1 param, returnType == paramType
            List<ExecutableElement> inputCandidates = publicOneParamMethods(inputMetaClass).stream()
                    .filter(m -> sameType(types, m.getReturnType(), m.getParameters().get(0).asType()))
                    .toList();
            if (inputCandidates.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        diag("Can't find input metamorphosis method for relation", relationName));
                return null;
            }
            ExecutableElement inputMethod = inputCandidates.get(0);
            boolean inputIsStatic = inputMethod.getModifiers().contains(Modifier.STATIC);
            TypeMirror inputType = inputMethod.getReturnType();

            // 2) OutputMetamorphosis: public, 1 param, returnType == paramType, returnType != inputType
            List<ExecutableElement> outputCandidates = publicOneParamMethods(outputMetaClass).stream()
                    .filter(m -> sameType(types, m.getReturnType(), m.getParameters().get(0).asType()))
                    .filter(m -> !sameType(types, m.getReturnType(), inputType))
                    .toList();
            if (outputCandidates.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        diag("Can't find output metamorphosis method for relation", relationName));
                return null;
            }
            ExecutableElement outputMethod = outputCandidates.get(0);
            boolean outputIsStatic = outputMethod.getModifiers().contains(Modifier.STATIC);
            TypeMirror outputType = outputMethod.getReturnType();

            // 3) DataGenerator: public, 1 param, returnType == inputType, paramType != inputType
            List<ExecutableElement> dgCandidates = publicOneParamMethods(dataGeneratorClass).stream()
                    .filter(m -> sameType(types, m.getReturnType(), inputType))
                    .filter(m -> !sameType(types, m.getParameters().get(0).asType(), inputType))
                    .toList();
            if (dgCandidates.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        diag("Can't find data generator method for relation", relationName));
                return null;
            }
            ExecutableElement dataGeneratorMethod = dgCandidates.get(0);
            boolean dgIsStatic = dataGeneratorMethod.getModifiers().contains(Modifier.STATIC);
            TypeMirror generatorParamType = dataGeneratorMethod.getParameters().get(0).asType();

            // 4) ArtifactEntryPoint: public, 1 param, paramType == inputType, returnType == outputType
            List<ExecutableElement> artCandidates = publicOneParamMethods(artifactClass).stream()
                    .filter(m -> sameType(types, m.getParameters().get(0).asType(), inputType))
                    .filter(m -> sameType(types, m.getReturnType(), outputType))
                    .toList();
            if (artCandidates.size() != 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        diag("Can't find artifact entry point method for relation", relationName));
                return null;
            }
            ExecutableElement artifactMethod = artCandidates.get(0);
            boolean artifactIsStatic = artifactMethod.getModifiers().contains(Modifier.STATIC);

            // 5) OutputModelComparer: якщо є — мусить бути Comparator<outputType>
            if (comparerClass != null && !isComparatorOf(types, elements, comparerClass, outputType)) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "[MTaaS][" + relationName + "] Output model comparer must implement java.util.Comparator<"
                                + typeStr(outputType) + ">");
                return null;
            }

            return new RelationSemantics(
                    relationName,
                    inputMetaClass, inputMethod, inputIsStatic, inputType,
                    outputMetaClass, outputMethod, outputIsStatic, outputType,
                    dataGeneratorClass, dataGeneratorMethod, dgIsStatic, generatorParamType,
                    artifactClass, artifactMethod, artifactIsStatic,
                    comparerClass
            );
        }

        private static List<ExecutableElement> publicOneParamMethods(TypeElement te) {
            return te.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.METHOD)
                    .map(e -> (ExecutableElement) e)
                    .filter(m -> m.getModifiers().contains(Modifier.PUBLIC))
                    .filter(m -> m.getParameters().size() == 1)
                    .toList();
        }

        private static boolean sameType(Types types, TypeMirror a, TypeMirror b) {
            return types.isSameType(types.erasure(a), types.erasure(b));
        }

        private static boolean isComparatorOf(Types types, Elements elements, TypeElement clazz, TypeMirror t) {
            TypeElement cmp = elements.getTypeElement("java.util.Comparator");
            if (cmp == null) return false;
            DeclaredType want = types.getDeclaredType(cmp, t);
            return types.isAssignable(clazz.asType(), want);
        }

        static String typeStr(TypeMirror t) {
            return Objects.toString(t);
        }

        static String diag(String msg, String rel) {
            return "[MTaaS][" + rel + "] " + msg;
        }
    }

