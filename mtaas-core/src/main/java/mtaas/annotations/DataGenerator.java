package mtaas.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(DataGenerator.List.class)
public @interface DataGenerator {
    String relationName();

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface List {
        DataGenerator[] value();
    }
}

