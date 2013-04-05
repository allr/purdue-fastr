package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

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
                RAny xarg = args[xPosition];
                if (!(xarg instanceof RArray)) {
                    throw RError.getInvalidArgument(ast, "x"); // FIXME: not an R error message, GNU-R uses implicit error in R
                }
                RArray a = (RArray) xarg;
                int[] dim = a.dimensions();
                if (dim != null && dim.length == 2) {
                    return xarg; // already a matrix
                }
                int size = a.size();
                int[] ndims = new int[] {size, 1};
                if (!a.isTemporary()) {
                    a = Utils.copyArray(a);
                }
                return a.setAttributes(null).setDimensions(ndims);
            }

        };
    }

}
