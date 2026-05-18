package demo.textnorm;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import mtaas.annotations.ArtifactEntry;

@ArtifactEntry(relationName = "textnorm-duplicate")
@ArtifactEntry(relationName = "textnorm-case")
@ArtifactEntry(relationName = "textnorm-whitespace")
public class TextArtifactEntry {

    public static TextMetrics analyze(TextCorpus corpus) {
        Map<String, Integer> frequencies = new HashMap<>();

        int totalTokens = 0;

        for (String sentence : corpus.getSentences()) {
            if (sentence == null) {
                continue;
            }

            String normalized = normalize(sentence);
            String[] tokens = tokenize(normalized);

            for (String token : tokens) {
                if (token.isBlank()) {
                    continue;
                }

                totalTokens++;

                frequencies.merge(token, 1, Integer::sum);
            }
        }

        int vocabularySize = frequencies.size();

        return new TextMetrics(
                totalTokens,
                vocabularySize,
                frequencies
        );
    }

    private static String normalize(String value) {
        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private static String[] tokenize(String value) {
        if (value.isBlank()) {
            return new String[0];
        }

        return value.split(" ");
    }
}
