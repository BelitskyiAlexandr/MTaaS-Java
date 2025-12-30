package demo.convexhull.generator;

import demo.convexhull.ConvexHull;
import demo.convexhull.Point2D;

import java.util.*;
public final class BaseCloud {
    public static List<Point2D> cloud(long seed){
        Random rnd = new Random(seed);
        List<Point2D> out = new ArrayList<>();
        double R=100.0;
        for (int i=0;i<240;i++){
            double a=2*Math.PI*rnd.nextDouble(); double r=R*(0.5+0.5*rnd.nextDouble());
            out.add(new Point2D(r*Math.cos(a), r*Math.sin(a)));
        }
        var hull = ConvexHull.compute(out);
        for (int k=0;k<120;k++){ double x=0,y=0; for (var p: hull){ double w=rnd.nextDouble(); x+=w*p.x; y+=w*p.y; } out.add(new Point2D(x/hull.size(), y/hull.size())); }
        return out;
    }
}


