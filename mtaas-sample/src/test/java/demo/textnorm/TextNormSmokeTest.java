package demo.textnorm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TextNormSmokeTest {
    @Test
    void relationRuns() {
        TextGenModel model = new TextGenModel();
        model.setSeed(1L);

//        // якщо у тебе генеряться класи, підстав сюди реальну назву
//         MetamorphicFunction_textnorm_duplicate fn = new MetamorphicFunction_textnorm_duplicate();
//         assertTrue(fn.run(model));

        // Якщо згенерованих класів ще немає — можна тимчасово перевірити вручну (варіант B нижче)
        TextCorpus input = TextDataGenerator.make(model);
        TextMetrics out1 = TextOutputMetamorphosisDoubleCounts.doubleCounts(TextArtifactEntry.analyze(input));
        TextMetrics out2 = TextArtifactEntry.analyze(TextInputMetamorphosisDuplicate.duplicate(input));
        assertTrue(new TextOutputModelComparer().compare(out1, out2) == 0);
    }
}
