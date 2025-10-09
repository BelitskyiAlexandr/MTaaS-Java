package mtaas.processor;

import javax.lang.model.element.TypeElement;

/**
 * Ролі учасників метаморфічного відношення MTaaS.
 * <ul>
 *   <li>INPUT_METAMORPHOSIS — клас, що перетворює вхідні дані (вхідна метаморфоза).</li>
 *   <li>OUTPUT_METAMORPHOSIS — клас, що перетворює вихід (вихідна метаморфоза).</li>
 *   <li>ARTIFACT_ENTRY — точка входу артефакту (наприклад, сервіс/раннер/конвеєр).</li>
 *   <li>DATA_GENERATOR — клас, що генерує вхідні дані/варіанти для тестування.</li>
 *   <li>OUTPUT_MODEL_COMPARER — клас-компаратор вихідних моделей (oracle-перевірка).</li>
 * </ul>
 */
enum Role {
    INPUT_METAMORPHOSIS,
    OUTPUT_METAMORPHOSIS,
    ARTIFACT_ENTRY,
    DATA_GENERATOR,
    OUTPUT_MODEL_COMPARER
}

public final class RelationParts {
    String relationName;
    TypeElement inputMetamorphosis;
    TypeElement outputMetamorphosis;
    TypeElement artifactEntry;
    TypeElement dataGenerator;
    TypeElement outputModelComparer;

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
