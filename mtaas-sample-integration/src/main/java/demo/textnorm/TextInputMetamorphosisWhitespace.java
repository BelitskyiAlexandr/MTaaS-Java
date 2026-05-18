package demo.textnorm;

import java.util.ArrayList;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "textnorm-whitespace")
public class TextInputMetamorphosisWhitespace {

    public static TextCorpus injectWhitespace(TextCorpus input) {
        List<String> out = new ArrayList<>();
        for (String s : input.getSentences()) {
            out.add(" \t  " + s.replace(" ", "   ") + "\n   ");
        }
        return new TextCorpus(out);
    }
}
