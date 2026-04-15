package demo.convexhull.mr.translate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "hull-translate")
public final class MR_Translate_Input {
    public static List<Point2D> apply(List<Point2D> in){
        return Transforms.translate(in, 37.5, -12.25);
    }
}