package demo.convexhull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ConvexHull {

    private ConvexHull() {
    }

    public static List<Point2D> compute(List<Point2D> pts) {
        if (pts == null || pts.isEmpty()) {
            return java.util.List.of();
        }

        var p = new ArrayList<>(pts);
        Collections.sort(p);

        var lower = new ArrayList<Point2D>();
        for (var pt : p) {
            while (lower.size() >= 2
                    && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), pt) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(pt);
        }

        var upper = new ArrayList<Point2D>();
        for (int i = p.size() - 1; i >= 0; i--) {
            var pt = p.get(i);
            while (upper.size() >= 2
                    && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), pt) <= 0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(pt);
        }

        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);

        var hull = new ArrayList<Point2D>(lower.size() + upper.size());
        hull.addAll(lower);
        hull.addAll(upper);
        return hull;
    }

    private static double cross(Point2D a, Point2D b, Point2D c) {
        double x1 = b.x - a.x;
        double y1 = b.y - a.y;
        double x2 = c.x - b.x;
        double y2 = c.y - b.y;
        return x1 * y2 - y1 * x2;
    }
}
