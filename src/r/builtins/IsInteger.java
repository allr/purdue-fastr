package r.builtins;

import r.data.*;

final class IsInteger extends TypeCheck {

    static final CallFactory _ = new IsInteger("is.integer");

    IsInteger(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RInt;
            }
        });
    }
}
