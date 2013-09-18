package r.nodes.ast;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PrettyName {
    String value();
}
