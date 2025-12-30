package demo.convexhull.mr.permute;

import demo.convexhull.Point2D;
import mtaas.annotations.InputMetamorphosis;
import java.util.*;

@InputMetamorphosis(relationName = "hull-permute")
public final class MR_Permute_Input {
    public static Object apply(Object in){
        @SuppressWarnings("unchecked") List<Point2D> pts = (List<Point2D>) in;
        var copy = new ArrayList<>(pts);
        Collections.shuffle(copy, new java.util.Random(777));
        return copy;
    }
}