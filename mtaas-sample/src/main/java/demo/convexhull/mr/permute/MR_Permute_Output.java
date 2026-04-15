package demo.convexhull.mr.permute;

import demo.convexhull.Point2D;
import mtaas.annotations.OutputMetamorphosis;

import java.util.List;

@OutputMetamorphosis(relationName = "hull-permute")
public final class MR_Permute_Output {
    public static List<Point2D> transform(List<Point2D> baseOut){
        return baseOut;
    }
}