package demo.textnorm;

import java.util.HashMap;
import java.util.Map;
import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "textnorm-duplicate")
public class TextOutputMetamorphosisDoubleCounts {

    public static TextMetrics doubleCounts(TextMetrics original) {
        Map<String, Integer> doubled = new HashMap<>();
        for (Map.Entry<String, Integer> e : original.getTokenFrequencies().entrySet()) {
            doubled.put(e.getKey(), e.getValue() * 2);
        }
        return new TextMetrics(
                original.getTotalTokens() * 2,
                original.getVocabularySize(),
                doubled
        );
    }
}
