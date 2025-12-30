package demo.textnorm;

import mtaas.annotations.OutputModelComparer;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@OutputModelComparer(relationName = "textnorm-duplicate")
public class TextOutputModelComparer implements Comparator<TextMetrics> {

    @Override
    public int compare(TextMetrics a, TextMetrics b) {
        if (Objects.equals(a, b)) return 0;
        if (a == null || b == null) return -1;

        if (a.getTotalTokens() != b.getTotalTokens()) return -1;
        if (a.getVocabularySize() != b.getVocabularySize()) return -1;

        Map<String, Integer> fa = a.getTokenFrequencies();
        Map<String, Integer> fb = b.getTokenFrequencies();

        if (fa.size() != fb.size()) return -1;

        for (Map.Entry<String, Integer> e : fa.entrySet()) {
            Integer vb = fb.get(e.getKey());
            if (!Objects.equals(e.getValue(), vb)) return -1;
        }
        return 0;
    }
}
