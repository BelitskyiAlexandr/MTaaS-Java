package demo.convexhull.generator;

import mtaas.annotations.DataGenerator;

@DataGenerator(relationName = "hull-permute")
public final class PermuteDataGenerator {
    public static Object make() {
        return BaseCloud.cloud(45L);
    }
}
