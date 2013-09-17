package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// TODO: S3
// TODO: dimnames
final class AsMatrix extends CallFactory {

    static final CallFactory _ = new AsMatrix("as.matrix", new String[]{"x", "..."}, new String[]{"x"});

    private AsMatrix(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");

        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return asMatrix(args[xPosition], ast);
            }
        };
    }

    public static RArray asMatrix(RAny x, ASTNode ast) {
        if (!(x instanceof RArray)) {
            throw RError.getInvalidArgument(ast, "x"); // FIXME: not an R error message, GNU-R uses implicit error in R
        }
        return asMatrix((RArray) x);
    }

    public static RArray asMatrix(RArray a) {
        int[] dim = a.dimensions();
        if (dim != null && dim.length == 2) {
            return a; // already a matrix
        } else {
            return castToMatrix(a);
        }
    }

    public static RArray castToMatrix(RArray a) {
        int size = a.size();
        int[] ndims = new int[] {size, 1};
        RArray na;
        if (!a.isTemporary()) {
            na = Utils.copyArray(a);
        } else {
            na = a;
        }
        return na.setAttributes(null).setDimensions(ndims);
    }
}
