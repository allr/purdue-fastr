package r.builtins;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.RDouble.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: add S3 support
@SuppressWarnings("unused") final class Det extends CallFactory {

    static final CallFactory _ = new Det("det", new String[]{"x", "..."}, new String[]{"x"});

    private Det(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw Utils.nyi("det to be implemented");
            }
        };
    }

}
