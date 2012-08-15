package r.nodes;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Precedence {
    int value() default 0;
    int MIN = 0;
    int MAX = Integer.MAX_VALUE - 100;
}
