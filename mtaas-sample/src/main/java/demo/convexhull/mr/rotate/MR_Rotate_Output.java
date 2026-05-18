package demo.convexhull.mr.rotate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;

import java.util.List;

import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "hull-rotate")
public final class MR_Rotate_Output {
    public static List<Point2D> transform(List<Point2D> baseOut) {
        return Transforms.rotate(baseOut, Math.PI / 7);
    }
}
