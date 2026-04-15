package demo.convexhull.mr.permute;

import demo.convexhull.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "hull-permute")
public final class MR_Permute_Input {
    public static List<Point2D> apply(List<Point2D> in){
        var copy = new ArrayList<>(in);
        Collections.shuffle(copy, new java.util.Random(777));
        return copy;
    }
}