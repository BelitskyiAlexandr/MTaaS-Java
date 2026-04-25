package demo.textnorm;

import mtaas.annotations.ArtifactEntry;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ArtifactEntry(relationName = "textnorm-duplicate")
@ArtifactEntry(relationName = "textnorm-case")
@ArtifactEntry(relationName = "textnorm-whitespace")
public class TextArtifactEntry {

    // "Pipeline": normalize -> tokenize -> count
    public static TextMetrics analyze(TextCorpus corpus) {
        Map<String, Integer> freq = new HashMap<>();
        int total = 0;

        for (String sentence : corpus.getSentences()) {
            if (sentence == null) continue;

            String normalized = normalize(sentence);
            String[] tokens = tokenize(normalized);

            for (String t : tokens) {
                if (t.isBlank()) continue;
                total++;
                freq.merge(t, 1, Integer::sum);
            }
        }

        int vocab = freq.size();
        return new TextMetrics(total, vocab, freq);
    }

    private static String normalize(String s) {
        // deliberately simple normalization
        return s.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static String[] tokenize(String s) {
        // just split by spaces
        if (s.isBlank()) return new String[0];
        return s.split(" ");
    }
}
