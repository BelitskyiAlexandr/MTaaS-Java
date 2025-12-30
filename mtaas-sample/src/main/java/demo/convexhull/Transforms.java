package demo.convexhull;

import java.util.*;
import java.util.function.Function;

public final class Transforms {
    private Transforms(){}
    public static <T> java.util.List<T> map(java.util.List<T> in, Function<T,T> f){
        var out = new ArrayList<T>(in.size()); for (var p: in) out.add(f.apply(p)); return out;
    }
    public static java.util.List<Point2D> translate(java.util.List<Point2D> in, double dx,double dy){
        return map(in, p -> p.add(dx,dy));
    }
    public static java.util.List<Point2D> rotate(java.util.List<Point2D> in, double t){
        return map(in, p -> p.rotate(t));
    }
    public static java.util.List<Point2D> scale(java.util.List<Point2D> in, double k){
        return map(in, p -> p.scale(k));
    }
}

