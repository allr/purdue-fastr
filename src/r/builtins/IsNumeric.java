package r.builtins;

import r.data.*;

final class IsNumeric extends TypeCheck {

    static final CallFactory _ = new IsNumeric("is.numeric");

    IsNumeric(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RNumber;
            }
        });
    }
}
