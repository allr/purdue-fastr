package r.builtins;

import r.data.*;

final class IsList extends TypeCheck {

    static final CallFactory _ = new IsList("is.list");

    IsList(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RList;
            }
        });
    }
}
