package demo.convexhull;

import mtaas.annotations.OutputModelComparer;

import java.util.Comparator;
import java.util.List;

@OutputModelComparer(relationName = "hull-permute")
//@OutputModelComparer(relationName = "hull-rotate")
//@OutputModelComparer(relationName = "hull-scale")
//@OutputModelComparer(relationName = "hull-translate")
public final class HullComparer implements Comparator<Object> {

    @Override
    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
        List<Point2D> a = (List<Point2D>) o1;
        List<Point2D> b = (List<Point2D>) o2;
        return HullUtils.equalsUpToCyclicRotation(a, b) ? 0 : 1;
    }
}
