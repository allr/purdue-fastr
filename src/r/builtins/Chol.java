package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.data.RDouble.RDoubleFactory;
import r.data.internal.*;
import r.errors.*;
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
        final int xPosition = ia.position("x");
        final int pivotPosition = ia.position("pivot");
        final int linpackPosition = ia.position("LINPACK");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw Utils.nyi("LINPACK version not implemented");
            }
        };
    }

}
