package mtaas.processor;

import javax.lang.model.element.TypeElement;
import lombok.Getter;

@Getter
public final class RelationParts {
    private String relationName;
    private TypeElement inputMetamorphosis;
    private TypeElement outputMetamorphosis;
    private TypeElement artifactEntry;
    private TypeElement dataGenerator;
    private TypeElement outputModelComparer;

    RelationParts(String relationName) {
        this.relationName = relationName;
    }

    public void put(Role role, TypeElement type) {
        switch (role) {
            case INPUT_METAMORPHOSIS -> this.inputMetamorphosis = type;
            case OUTPUT_METAMORPHOSIS -> this.outputMetamorphosis = type;
            case ARTIFACT_ENTRY -> this.artifactEntry = type;
            case DATA_GENERATOR -> this.dataGenerator = type;
            case OUTPUT_MODEL_COMPARER -> this.outputModelComparer = type;
            default -> { }
        }
    }

    public boolean isReady() {
        return artifactEntry != null && dataGenerator != null
                && inputMetamorphosis != null && outputMetamorphosis != null;
    }

    public boolean hasComparer() {
        return outputModelComparer != null;
    }
}
