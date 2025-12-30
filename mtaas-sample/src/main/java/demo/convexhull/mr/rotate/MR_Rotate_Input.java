package demo.convexhull.mr.rotate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.InputMetamorphosis;
import java.util.List;

@InputMetamorphosis(relationName = "hull-rotate")
public final class MR_Rotate_Input {
    public static Object apply(Object in){
        @SuppressWarnings("unchecked") List<Point2D> pts = (List<Point2D>) in;
        return Transforms.rotate(pts, Math.PI/7);
    }
}
