package demo.convexhull.mr.permute;

import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "hull-permute")
public final class MR_Permute_Output {
    public static Object transform(Object baseOut){
        return baseOut;
    }
}