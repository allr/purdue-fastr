package r.builtins;

import r.data.*;

final class IsCharacter extends TypeCheck {

    static final CallFactory _ = new IsCharacter("is.character");

    IsCharacter(String name) {
        super(name, new CheckAction() {
            @Override public boolean is(RAny arg) {
                return arg instanceof RString;
            }
        });
    }
}
