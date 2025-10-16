package mtaas.processor;

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
