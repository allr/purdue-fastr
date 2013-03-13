package r.builtins;

import r.data.*;

final class IsDouble extends TypeCheck {

    static final CallFactory _ = new IsDouble("is.double");

    IsDouble(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RDouble;
            }
        });
    }
}
