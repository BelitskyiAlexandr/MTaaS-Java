package demo.textnorm;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import mtaas.annotations.OutputModelComparer;

@OutputModelComparer(relationName = "textnorm-case")
public class TextOutputModelComparerCase implements Comparator<TextMetrics> {

    @Override
    public int compare(TextMetrics first, TextMetrics second) {
        if (Objects.equals(first, second)) {
            return 0;
        }

        if (first == null || second == null) {
            return -1;
        }

        if (first.getTotalTokens() != second.getTotalTokens()) {
            return -1;
        }

        if (first.getVocabularySize() != second.getVocabularySize()) {
            return -1;
        }

        Map<String, Integer> firstFrequencies = first.getTokenFrequencies();
        Map<String, Integer> secondFrequencies = second.getTokenFrequencies();

        if (firstFrequencies.size() != secondFrequencies.size()) {
            return -1;
        }

        for (Map.Entry<String, Integer> entry : firstFrequencies.entrySet()) {
            if (!Objects.equals(entry.getValue(), secondFrequencies.get(entry.getKey()))) {
                return -1;
            }
        }

        return 0;
    }
}
