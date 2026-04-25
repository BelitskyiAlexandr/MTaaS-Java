package demo.textnorm;

import mtaas.annotations.InputMetamorphosis;

import java.util.ArrayList;
import java.util.List;

@InputMetamorphosis(relationName = "textnorm-duplicate")
public class TextInputMetamorphosisDuplicate {

    public static TextCorpus duplicate(TextCorpus input) {
        List<String> s = new ArrayList<>(input.getSentences());
        s.addAll(input.getSentences());
        return new TextCorpus(s);
    }
}
