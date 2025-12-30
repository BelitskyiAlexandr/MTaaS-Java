package demo.convexhull.mr.scale;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import mtaas.annotations.InputMetamorphosis;

import java.util.List;

@InputMetamorphosis(relationName = "hull-scale")
public final class MR_Scale_Input {
    public static Object apply(Object in){
        @SuppressWarnings("unchecked") List<Point2D> pts = (List<Point2D>) in;
        return Transforms.scale(pts, 3.0);
    }
}

