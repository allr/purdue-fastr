package r.builtins;

import r.data.*;

final class IsList extends IsBase {

    static final CallFactory _ = new IsList("is.list");

    IsList(String name) {
        super(name);
    }

    @Override boolean is(RAny arg) {
        return arg instanceof RList;
    }
}
