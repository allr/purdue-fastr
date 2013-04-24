package r.builtins;

import r.*;
import r.Truffle.*;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Crossprod extends CallFactory {

    static final CallFactory _ = new Crossprod("crossprod", new String[]{"x", "y"}, new String[]{"x"});

    private Crossprod(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xpos = ia.position("x");
        final int ypos = ia.position("y");

        if (ypos != -1) {
            return new Builtin.Builtin2(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1) {
                    RAny x;
                    RAny y;
                    if (xpos == 0) {
                        x = arg0;
                        y = arg1;
                    } else {
                        x = arg1;
                        y = arg0;
                    }
                    if (y instanceof RNull) {
                        return crossprod(x, x, ast);
                    } else {
                        return crossprod(x, y, ast);
                    }
                }
            };
        } else {
            return new Builtin.Builtin1(call, names, exprs) {
                @Override public RAny doBuiltIn(Frame frame, RAny x) {
                    return crossprod(x, x, ast);
                }
            };
        }
    }

    public static RAny crossprod(RAny l, RAny r, ASTNode ast) {

        throw Utils.nyi("crossprod to be implemented");
    }
}
