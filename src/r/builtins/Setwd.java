package r.builtins;

import r.Truffle.Frame;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Setwd extends CallFactory {

    static final CallFactory _ = new Setwd("setwd", new String[]{"dir"}, new String[]{"dir"});

    private Setwd(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @SuppressWarnings("unused") @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int dirPos = ai.position("dir");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                String dir = get(params, dirPos, "");
                String res = perform(dir);
                return new ScalarStringImpl(res);
            }
        };
    }

    static String perform(String dir) {
        String old = System.getProperty("user.dir");
        System.setProperty("user.dir", dir);
        return old;
    }
}
