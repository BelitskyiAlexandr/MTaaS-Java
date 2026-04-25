package demo.textnorm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextMetrics {
    private final int totalTokens;
    private final int vocabularySize;
    private final Map<String, Integer> tokenFrequencies;

    public TextMetrics(int totalTokens, int vocabularySize, Map<String, Integer> tokenFrequencies) {
        this.totalTokens = totalTokens;
        this.vocabularySize = vocabularySize;
        this.tokenFrequencies = new HashMap<>(tokenFrequencies);
    }

    public int getTotalTokens() { return totalTokens; }
    public int getVocabularySize() { return vocabularySize; }
    public Map<String, Integer> getTokenFrequencies() { return Collections.unmodifiableMap(tokenFrequencies); }
}
