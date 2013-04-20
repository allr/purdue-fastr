package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

class RepInt extends Rep {

    static final CallFactory _ = new RepInt("rep.int", new String[]{"x", "times"}, new String[]{"x", "times"});

    RepInt(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final boolean xfirst = ia.position("x") == 0;
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                return genericRepInt(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
            }
        };
    }
}
