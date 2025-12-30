package demo.convexhull.mr.rotate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.OutputMetamorphosis;
import java.util.List;

@OutputMetamorphosis(relationName = "hull-rotate")
public final class MR_Rotate_Output {
    public static Object transform(Object baseOut){
        @SuppressWarnings("unchecked") List<Point2D> h = (List<Point2D>) baseOut;
        return Transforms.rotate(h, Math.PI/7);
    }
}