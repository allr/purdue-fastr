package r.builtins;

import r.data.*;

final class IsLogical extends TypeCheck {

    static final CallFactory _ = new IsLogical("is.logical");

    IsLogical(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RLogical;
            }
        });
    }
}
