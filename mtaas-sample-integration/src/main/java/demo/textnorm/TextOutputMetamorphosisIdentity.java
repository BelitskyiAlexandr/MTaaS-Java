package demo.textnorm;

import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "textnorm-whitespace")
public class TextOutputMetamorphosisIdentity {

    public static TextMetrics same(TextMetrics original) {
        return original;
    }
}
