package r.builtins;

import r.data.*;

final class IsDouble extends IsBase {

    static final CallFactory _ = new IsDouble("is.double");

    IsDouble(String name) {
        super(name);
    }

    @Override boolean is(RAny arg) {
        return arg instanceof RDouble;
    }
}
