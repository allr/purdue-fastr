package r.builtins;

import r.data.*;

final class IsRaw extends TypeCheck {

    static final CallFactory _ = new IsRaw("is.raw");

    IsRaw(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RRaw;
            }
        });
    }
}
