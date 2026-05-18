package demo.convexhull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HullUtils {

    public static final double EPS = 1e-9;

    private HullUtils() {
    }

    public static double signedArea(List<Point2D> polygon) {
        double area = 0.0;

        for (int i = 0; i < polygon.size(); i++) {
            Point2D current = polygon.get(i);
            Point2D next = polygon.get((i + 1) % polygon.size());

            area += current.x * next.y - current.y * next.x;
        }

        return 0.5 * area;
    }

    public static List<Point2D> canonicalize(List<Point2D> hull) {
        if (hull.isEmpty()) {
            return hull;
        }

        List<Point2D> result = new ArrayList<>(hull);

        if (signedArea(result) < 0) {
            Collections.reverse(result);
        }

        int bestIndex = 0;
        for (int i = 1; i < result.size(); i++) {
            if (result.get(i).compareTo(result.get(bestIndex)) < 0) {
                bestIndex = i;
            }
        }

        List<Point2D> canonical = new ArrayList<>(result.size());
        for (int i = 0; i < result.size(); i++) {
            canonical.add(result.get((bestIndex + i) % result.size()));
        }

        return canonical;
    }

    public static boolean equalsUpToCyclicRotation(List<Point2D> first, List<Point2D> second) {
        if (first.size() != second.size()) {
            return false;
        }

        if (first.isEmpty()) {
            return true;
        }

        List<Point2D> canonicalFirst = canonicalize(first);
        List<Point2D> canonicalSecond = canonicalize(second);

        if (equalsSequence(canonicalFirst, canonicalSecond)) {
            return true;
        }

        Collections.reverse(canonicalSecond);
        return equalsSequence(canonicalFirst, canonicalSecond);
    }

    private static boolean equalsSequence(List<Point2D> first, List<Point2D> second) {
        for (int i = 0; i < first.size(); i++) {
            Point2D a = first.get(i);
            Point2D b = second.get(i);

            if (Math.abs(a.x - b.x) > EPS) {
                return false;
            }

            if (Math.abs(a.y - b.y) > EPS) {
                return false;
            }
        }

        return true;
    }
}
