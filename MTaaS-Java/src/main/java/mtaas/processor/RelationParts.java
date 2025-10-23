package mtaas.processor;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RelationParts {
    final String relationName;
    private TypeElement artifactEntry;
    private TypeElement dataGenerator;
    private final LinkedHashSet<TypeElement> inputMetas = new LinkedHashSet<>();
    private final LinkedHashSet<TypeElement> outputMetas = new LinkedHashSet<>();
    private final LinkedHashSet<TypeElement> comparers   = new LinkedHashSet<>();

    RelationParts(String relationName) {
        this.relationName = relationName;
    }

    void put(Role role, TypeElement type) {
        switch (role) {
            case ARTIFACT_ENTRY -> {
                if (artifactEntry != null && !artifactEntry.equals(type)) {
                    throw new IllegalStateException("Duplicate ArtifactEntry for relation: " + relationName);
                }
                artifactEntry = type;
            }
            case DATA_GENERATOR -> {
                if (dataGenerator != null && !dataGenerator.equals(type)) {
                    throw new IllegalStateException("Duplicate DataGenerator for relation: " + relationName);
                }
                dataGenerator = type;
            }
            case INPUT_METAMORPHOSIS -> inputMetas.add(type);
            case OUTPUT_METAMORPHOSIS -> outputMetas.add(type);
            case OUTPUT_MODEL_COMPARER -> comparers.add(type);
        }
    }

    TypeElement getArtifactEntry() { return artifactEntry; }
    TypeElement getDataGenerator() { return dataGenerator; }
    List<TypeElement> getInputMetas()  { return sorted(inputMetas); }
    List<TypeElement> getOutputMetas() { return sorted(outputMetas); }
    List<TypeElement> getComparers() { return sorted(comparers); }

    private static List<TypeElement> sorted(Set<TypeElement> set) {
        ArrayList<TypeElement> list = new ArrayList<>(set);
        list.sort(Comparator.comparing(e -> e.getQualifiedName().toString()));
        return list;
    }
}
