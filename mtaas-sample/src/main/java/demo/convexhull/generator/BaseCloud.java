package demo.convexhull.generator;

import demo.convexhull.ConvexHull;
import demo.convexhull.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class BaseCloud {

    private BaseCloud() {
    }

    /**
     * Generates a deterministic point cloud with both boundary and inner points.
     */
    public static List<Point2D> cloud(long seed) {
        Random random = new Random(seed);

        List<Point2D> points = new ArrayList<>();

        double radius = 100.0;

        for (int i = 0; i < 240; i++) {
            double angle = 2 * Math.PI * random.nextDouble();
            double distance = radius * (0.5 + 0.5 * random.nextDouble());

            points.add(
                    new Point2D(
                            distance * Math.cos(angle),
                            distance * Math.sin(angle)
                    )
            );
        }

        List<Point2D> hull = ConvexHull.compute(points);

        for (int i = 0; i < 120; i++) {
            double x = 0;
            double y = 0;

            for (Point2D point : hull) {
                double weight = random.nextDouble();

                x += weight * point.x;
                y += weight * point.y;
            }

            points.add(
                    new Point2D(
                            x / hull.size(),
                            y / hull.size()
                    )
            );
        }

        return points;
    }
}
