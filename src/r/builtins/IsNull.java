package r.builtins;

import r.data.*;

final class IsNull extends TypeCheck {

    static final CallFactory _ = new IsNull("is.null");

    IsNull(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RNull; // or == RNull.getNull()
            }
        });
    }
}
