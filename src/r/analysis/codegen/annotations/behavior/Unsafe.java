package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Marks the code as unsafe. This means that any behavior can result from this code.
 *
 * A function call is unsafe. This also eliminates dangerous builtins like eval, etc.
 *
 * TODO not all function calls should be unsafe, at least for builtins this should be decided.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface Unsafe {
    String check() default "";
}
