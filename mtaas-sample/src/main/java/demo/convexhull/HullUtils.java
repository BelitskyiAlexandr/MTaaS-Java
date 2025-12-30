package demo.convexhull;

import java.util.*;

public final class HullUtils {
    private HullUtils(){}
    public static final double EPS=1e-9;

    public static double signedArea(java.util.List<Point2D> poly){
        double s=0; for(int i=0;i<poly.size();i++){var a=poly.get(i); var b=poly.get((i+1)%poly.size()); s+=a.x*b.y - a.y*b.x;} return 0.5*s;
    }

    public static java.util.List<Point2D> canonicalize(java.util.List<Point2D> hull){
        if (hull.isEmpty()) return hull;
        var res = new ArrayList<>(hull);
        if (signedArea(res) < 0) Collections.reverse(res);
        int best=0; for(int i=1;i<res.size();i++) if (res.get(i).compareTo(res.get(best))<0) best=i;
        var canon = new ArrayList<Point2D>(res.size());
        for (int i=0;i<res.size();i++) canon.add(res.get((best+i)%res.size()));
        return canon;
    }

    public static boolean equalsUpToCyclicRotation(java.util.List<Point2D> a, java.util.List<Point2D> b){
        if (a.size()!=b.size()) return false;
        if (a.isEmpty()) return true;
        var ca=canonicalize(a); var cb=canonicalize(b);
        if (eqSeq(ca,cb)) return true; Collections.reverse(cb); return eqSeq(ca,cb);
    }
    private static boolean eqSeq(java.util.List<Point2D> a, java.util.List<Point2D> b){
        for(int i=0;i<a.size();i++){ if (Math.abs(a.get(i).x-b.get(i).x)>EPS) return false; if (Math.abs(a.get(i).y-b.get(i).y)>EPS) return false;}
        return true;
    }
}
