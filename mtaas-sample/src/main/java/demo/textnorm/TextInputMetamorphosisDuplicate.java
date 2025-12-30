package demo.textnorm;

import mtaas.annotations.InputMetamorphosis;

import java.util.ArrayList;
import java.util.List;

@InputMetamorphosis(relationName = "textnorm-duplicate")
public class TextInputMetamorphosisDuplicate {

    // MR: duplicate corpus -> output token counts should double, vocab should remain
    public static TextCorpus duplicate(TextCorpus input) {
        List<String> s = new ArrayList<>(input.getSentences());
        s.addAll(input.getSentences());
        return new TextCorpus(s);
    }
}
