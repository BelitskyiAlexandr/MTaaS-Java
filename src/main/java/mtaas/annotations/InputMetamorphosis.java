package mtaas.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(InputMetamorphosis.List.class)
public @interface InputMetamorphosis {
    String relationName();

    // Container for @Repeatable
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface List {
        InputMetamorphosis[] value();
    }
}
