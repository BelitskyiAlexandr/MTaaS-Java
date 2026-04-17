package demo.textnorm;

import mtaas.annotations.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DataGenerator(relationName = "textnorm-duplicate")
@DataGenerator(relationName = "textnorm-case")
@DataGenerator(relationName = "textnorm-whitespace")
public class TextDataGenerator {

    private static final String[] DICTIONARY = new String[] {
            "java", "mtaas", "metamorphic", "testing", "pipeline", "token",
            "normalize", "compare", "artifact", "relation", "input", "output",
            "model", "generator", "cloud", "service"
    };

    public static TextCorpus make(TextGenModel model) {
        Random rnd = new Random(model.getSeed());
        List<String> sentences = new ArrayList<>();

        for (int i = 0; i < model.getSentences(); i++) {
            int len = randBetween(rnd, model.getMinTokensPerSentence(), model.getMaxTokensPerSentence());
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                if (j > 0) sb.append(' ');
                sb.append(DICTIONARY[rnd.nextInt(DICTIONARY.length)]);
            }
            sentences.add(sb.toString());
        }

        return new TextCorpus(sentences);
    }

    private static int randBetween(Random rnd, int min, int max) {
        if (max < min) return min;
        if (max == min) return min;
        return min + rnd.nextInt(max - min + 1);
    }
}
