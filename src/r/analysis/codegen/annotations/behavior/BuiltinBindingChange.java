package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

/** Present if the execution of the node may lead to a change of a binding of any builtin.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface BuiltinBindingChange {
    String check() default "";
}
