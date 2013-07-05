package r.analysis;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
public @interface LinearOrder {
    int index();
}
