package demo.convexhull.mr.scale;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.OutputMetamorphosis;

import java.util.List;

@OutputMetamorphosis(relationName = "hull-scale")
public final class MR_Scale_Output {
    public static Object transform(Object baseOut){
        @SuppressWarnings("unchecked") List<Point2D> h = (List<Point2D>) baseOut;
        return Transforms.scale(h, 3.0);
    }
}
