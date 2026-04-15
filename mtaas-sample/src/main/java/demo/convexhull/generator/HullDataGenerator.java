package demo.convexhull.generator;

import demo.convexhull.Point2D;
import java.util.List;
import mtaas.annotations.DataGenerator;

@DataGenerator(relationName = "hull-permute")
@DataGenerator(relationName = "hull-rotate")
@DataGenerator(relationName = "hull-scale")
@DataGenerator(relationName = "hull-translate")
public final class HullDataGenerator {
    public static List<Point2D> make(HullGenModel model) {
        return BaseCloud.cloud(model.seed);
    }
}