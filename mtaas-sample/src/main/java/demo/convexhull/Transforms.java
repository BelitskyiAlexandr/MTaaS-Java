package demo.convexhull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Transforms {

    private Transforms() {
    }

    public static <T> List<T> map(List<T> input, Function<T, T> mapper) {
        List<T> result = new ArrayList<>(input.size());

        for (T item : input) {
            result.add(mapper.apply(item));
        }

        return result;
    }

    public static List<Point2D> translate(List<Point2D> input,
                                          double dx,
                                          double dy) {
        return map(input, point -> point.add(dx, dy));
    }

    public static List<Point2D> rotate(List<Point2D> input,
                                       double theta) {
        return map(input, point -> point.rotate(theta));
    }

    public static List<Point2D> scale(List<Point2D> input,
                                      double factor) {
        return map(input, point -> point.scale(factor));
    }
}
