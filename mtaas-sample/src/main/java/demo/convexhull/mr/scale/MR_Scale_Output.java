package demo.convexhull.mr.scale;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;

import java.util.List;

import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "hull-scale")
public final class MR_Scale_Output {
    public static List<Point2D> transform(List<Point2D> baseOut) {
        return Transforms.scale(baseOut, 3.0);
    }
}
