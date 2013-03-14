package r.builtins;

import r.data.*;

final class IsRaw extends IsBase {

    static final CallFactory _ = new IsRaw("is.raw");

    IsRaw(String name) {
        super(name);
    }

    @Override boolean is(RAny arg) {
        return arg instanceof RRaw;
    }
}
