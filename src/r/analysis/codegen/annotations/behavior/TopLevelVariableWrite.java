package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Present if the node writes to a top-level variable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface TopLevelVariableWrite {
    String check() default "";
}
