package demo.textnorm;

public class TextGenModel {
    private int sentences = 8;
    private int minTokensPerSentence = 4;
    private int maxTokensPerSentence = 10;
    private long seed = 42L;

    public int getSentences() {
        return sentences;
    }

    public void setSentences(int sentences) {
        this.sentences = sentences;
    }

    public int getMinTokensPerSentence() {
        return minTokensPerSentence;
    }

    public void setMinTokensPerSentence(int minTokensPerSentence) {
        this.minTokensPerSentence = minTokensPerSentence;
    }

    public int getMaxTokensPerSentence() {
        return maxTokensPerSentence;
    }

    public void setMaxTokensPerSentence(int maxTokensPerSentence) {
        this.maxTokensPerSentence = maxTokensPerSentence;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
