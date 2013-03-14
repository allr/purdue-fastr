package r.builtins;

import r.data.*;

final class IsLogical extends IsBase {

    static final CallFactory _ = new IsLogical("is.logical");

    IsLogical(String name) {
        super(name);
    }

    @Override public boolean is(RAny arg) {
        return arg instanceof RLogical;
    }
}
