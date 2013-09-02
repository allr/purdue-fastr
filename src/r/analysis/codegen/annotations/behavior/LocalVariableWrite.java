package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Present if the node writes to local variable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface LocalVariableWrite {
    String check() default "";
}
