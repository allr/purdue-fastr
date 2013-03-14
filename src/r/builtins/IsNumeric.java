package r.builtins;

import r.data.*;

final class IsNumeric extends IsBase {

    static final CallFactory _ = new IsNumeric("is.numeric");

    IsNumeric(String name) {
        super(name);
    }

    @Override boolean is(RAny arg) {
        return arg instanceof RNumber;
    }
}
