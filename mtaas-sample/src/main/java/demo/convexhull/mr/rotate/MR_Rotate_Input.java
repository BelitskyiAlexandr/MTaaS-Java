package demo.convexhull.mr.rotate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "hull-rotate")
public final class MR_Rotate_Input {
    public static List<Point2D> apply(List<Point2D> in){
        return Transforms.rotate(in, Math.PI/7);
    }
}
