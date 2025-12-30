package demo.textnorm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextCorpus {
    private final List<String> sentences;

    public TextCorpus(List<String> sentences) {
        this.sentences = new ArrayList<>(sentences);
    }

    public List<String> getSentences() {
        return Collections.unmodifiableList(sentences);
    }
}
