package demo.convexhull.mr.translate;

import demo.convexhull.Point2D;
import demo.convexhull.Transforms;
import java.util.List;
import mtaas.annotations.OutputMetamorphosis;

@OutputMetamorphosis(relationName = "hull-translate")
public final class MR_Translate_Output {
    public static List<Point2D> transform(List<Point2D> baseOut){
        return Transforms.translate(baseOut, 37.5, -12.25);
    }
}