package demo.textnorm;

import mtaas.annotations.InputMetamorphosis;

import java.util.ArrayList;
import java.util.List;

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
