package demo.convexhull.mr.translate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.InputMetamorphosis;

import java.util.List;

@InputMetamorphosis(relationName = "hull-translate")
public final class MR_Translate_Input {
    public static Object apply(Object in){
        @SuppressWarnings("unchecked") List<Point2D> pts = (List<Point2D>) in;
        return Transforms.translate(pts, 37.5, -12.25);
    }
}