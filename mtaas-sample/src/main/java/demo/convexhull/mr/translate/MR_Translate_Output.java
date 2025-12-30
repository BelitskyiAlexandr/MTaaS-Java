package demo.convexhull.mr.translate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.OutputMetamorphosis;

import java.util.List;

@OutputMetamorphosis(relationName = "hull-translate")
public final class MR_Translate_Output {
    public static Object transform(Object baseOut){
        @SuppressWarnings("unchecked") List<Point2D> h = (List<Point2D>) baseOut;
        return Transforms.translate(h, 37.5, -12.25);
    }
}