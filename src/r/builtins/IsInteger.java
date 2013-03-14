package r.builtins;

import r.data.*;

final class IsInteger extends IsBase {

    static final CallFactory _ = new IsInteger("is.integer");

    IsInteger(String name) {
        super(name);
    }

    @Override public boolean is(RAny arg) {
        return arg instanceof RInt;
    }

}
