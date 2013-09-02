package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Interface describing the local */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface LocalVariableWrite {
    String check() default "";
}
