package demo.textnorm;

import mtaas.annotations.InputMetamorphosis;

import java.util.ArrayList;
import java.util.List;

@InputMetamorphosis(relationName = "textnorm-case")
public class TextInputMetamorphosisCase {

    public static TextCorpus flipCase(TextCorpus input) {
        List<String> out = new ArrayList<>();
        for (String s : input.getSentences()) {
            out.add(flip(s));
        }
        return new TextCorpus(out);
    }

    private static String flip(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) sb.append(Character.toLowerCase(c));
            else if (Character.isLowerCase(c)) sb.append(Character.toUpperCase(c));
            else sb.append(c);
        }
        return sb.toString();
    }
}
