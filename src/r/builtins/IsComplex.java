package r.builtins;

import r.data.*;

final class IsComplex extends IsBase {

    static final CallFactory _ = new IsComplex("is.complex");

    IsComplex(String name) {
        super(name);
    }

    @Override boolean is(RAny arg) {
        return arg instanceof RComplex;
    }
}
