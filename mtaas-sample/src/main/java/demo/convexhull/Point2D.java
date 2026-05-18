package demo.convexhull;

public final class Point2D implements Comparable<Point2D> {

    public final double x;
    public final double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D add(double dx, double dy) {
        return new Point2D(x + dx, y + dy);
    }

    public Point2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        return new Point2D(
                cos * x - sin * y,
                sin * x + cos * y
        );
    }

    public Point2D scale(double factor) {
        return new Point2D(factor * x, factor * y);
    }

    @Override
    public int compareTo(Point2D other) {
        int compareX = Double.compare(x, other.x);

        if (compareX != 0) {
            return compareX;
        }

        return Double.compare(y, other.y);
    }
}
