package r.builtins;

import r.Truffle.Frame;
import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: add S3
// TODO: add pivoting with LAPACK and add LINPACK
final class Chol extends CallFactory {

    static final CallFactory _ = new Chol("chol", new String[]{"x", "pivot", "LINPACK"}, new String[]{"x"});

    private Chol(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw Utils.nyi("LINPACK version not implemented");
            }
        };
    }

}
