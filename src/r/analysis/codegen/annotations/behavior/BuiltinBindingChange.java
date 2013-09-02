package r.analysis.codegen.annotations.behavior;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface BuiltinBindingChange {
    String check() default "";
}
