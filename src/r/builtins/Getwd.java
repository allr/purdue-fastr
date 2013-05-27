package r.builtins;

import r.Truffle.Frame;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Getwd extends CallFactory {

    static final CallFactory _ = new Getwd("getwd", new String[]{}, new String[]{});

    private Getwd(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @SuppressWarnings("unused") @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                String res = perform();
                return new ScalarStringImpl(res);
            }
        };
    }

    static String perform() {
        return System.getProperty("user.dir");
    }
}
