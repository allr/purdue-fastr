package r.builtins;

import r.data.*;

final class IsComplex extends TypeCheck {

    static final CallFactory _ = new IsComplex("is.complex");

    IsComplex(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RComplex;
            }
        });
    }
}
