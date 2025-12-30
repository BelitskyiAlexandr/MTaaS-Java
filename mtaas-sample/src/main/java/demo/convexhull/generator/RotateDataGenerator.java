package demo.convexhull.generator;

import mtaas.annotations.DataGenerator;

@DataGenerator(relationName = "hull-rotate")
public final class RotateDataGenerator {
    public static Object make() {
        return BaseCloud.cloud(43L);
    }
}
