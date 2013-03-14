package r.builtins;

import r.data.*;

final class IsNull extends IsBase {

    static final CallFactory _ = new IsNull("is.null");

    IsNull(String name) {
        super(name);
    }

    @Override public boolean is(RAny arg) {
        return arg instanceof RNull; // or == RNull.getNull()
    }
}
