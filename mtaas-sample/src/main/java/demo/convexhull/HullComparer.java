package demo.convexhull;

import mtaas.annotations.OutputModelComparer;

import java.util.Comparator;
import java.util.List;

@OutputModelComparer(relationName = "hull-permute")
@OutputModelComparer(relationName = "hull-rotate")
@OutputModelComparer(relationName = "hull-scale")
@OutputModelComparer(relationName = "hull-translate")
public final class HullComparer implements Comparator<List<Point2D>> {
    @Override
    public int compare(List<Point2D> o1, List<Point2D> o2) {
        return HullUtils.equalsUpToCyclicRotation(o1, o2) ? 0 : 1;
    }
}
