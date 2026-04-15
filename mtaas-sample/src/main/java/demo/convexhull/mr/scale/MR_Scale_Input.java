package demo.convexhull.mr.scale;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import java.util.List;
import mtaas.annotations.InputMetamorphosis;

@InputMetamorphosis(relationName = "hull-scale")
public final class MR_Scale_Input {
    public static List<Point2D> apply(List<Point2D> in){
        return Transforms.scale(in, 3.0);
    }
}

