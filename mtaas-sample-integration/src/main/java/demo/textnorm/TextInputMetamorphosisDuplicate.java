package demo.textnorm;

import java.util.ArrayList;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "textnorm-duplicate")
public class TextInputMetamorphosisDuplicate {

    public static TextCorpus duplicate(TextCorpus input) {
        List<String> s = new ArrayList<>(input.getSentences());
        s.addAll(input.getSentences());
        return new TextCorpus(s);
    }
}
