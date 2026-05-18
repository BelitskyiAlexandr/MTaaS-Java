package demo.textnorm;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import mtaas.annotations.OutputModelComparer;

@OutputModelComparer(relationName = "textnorm-duplicate")
public class TextOutputModelComparer implements Comparator<TextMetrics> {

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
            Integer secondValue = secondFrequencies.get(entry.getKey());

            if (!Objects.equals(entry.getValue(), secondValue)) {
                return -1;
            }
        }

        return 0;
    }
}
