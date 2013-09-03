package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Generic variable write. Applies to both functions and variables alike. Further distinction should be made by more
 * specific annotations like LocalVariableWrite, etc.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface VariableWrite {
    String check() default "";
}
