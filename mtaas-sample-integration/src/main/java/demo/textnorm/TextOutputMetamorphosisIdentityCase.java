package demo.textnorm;

import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "textnorm-case")
public class TextOutputMetamorphosisIdentityCase {

    public static TextMetrics same(TextMetrics original) {
        return original;
    }
}
