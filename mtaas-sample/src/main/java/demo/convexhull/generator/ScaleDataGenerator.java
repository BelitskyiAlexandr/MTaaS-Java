package demo.convexhull.generator;

import mtaas.annotations.DataGenerator;

@DataGenerator(relationName = "hull-scale")
public final class ScaleDataGenerator {
    public static Object make() {
        return BaseCloud.cloud(44L);
    }
}
