package r.builtins;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class QrSolve extends CallFactory {

    static final CallFactory _ = new QrSolve("qr.solve", new String[]{"a", "b", "tol"}, new String[]{"a"});

    private QrSolve(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int aPosition = ia.position("a");
        final int bPosition = ia.position("b");
        final int tolPosition = ia.position("tol");

        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw Utils.nyi();
            }
        };
    }

}
