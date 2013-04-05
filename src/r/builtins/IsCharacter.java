package r.builtins;

import r.data.*;

final class IsCharacter extends IsBase {

    static final CallFactory _ = new IsCharacter("is.character");

    private IsCharacter(String name) {
        super(name);
    }

    @Override public boolean is(RAny arg) {
        return arg instanceof RString;
    }
}
