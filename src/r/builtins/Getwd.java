package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

public class Getwd extends CallFactory {

    static final CallFactory _ = new Getwd("getwd", new String[]{}, new String[]{});

    private Getwd(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @SuppressWarnings("unused")
    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                String res = perform();
                return RString.RStringFactory.getScalar(res);
            }
        };
    }

    static String perform() {
        return System.getProperty("user.dir");
    }
}
