package demo.convexhull.generator;

import mtaas.annotations.DataGenerator;

@DataGenerator(relationName="hull-translate")
public final class TranslateDataGenerator {
    public static Object make(){ return BaseCloud.cloud(42L); }
}
