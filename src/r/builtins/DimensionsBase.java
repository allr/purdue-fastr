package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// TODO: add a replacement version

/**
 * <pre>
 * x --  an R object, for example a matrix, array or data frame.
 * </pre>
 */
abstract class DimensionsBase extends CallFactory {

    DimensionsBase(String name, Operation op) {
        super(name, new String[]{"x"}, null);
        this.op = op;
    }

    public abstract static class Operation {
        public abstract RInt extract(int[] dimensions);
    }

    final Operation op;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);

        return new BuiltIn.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x) {
                if (!(x instanceof RArray)) { return RNull.getNull(); }
                RArray ax = (RArray) x;
                int[] dim = ax.dimensions();
                if (dim == null) { return RNull.getNull(); }
                return op.extract(dim);
            }
        };
    }

    /**
     * Return the dimensions of the argument arg or NULL.
     */
    public static RAny getDim(RAny arg) {
        if (!(arg instanceof RArray)) { return RNull.getNull(); }
        RArray arr = (RArray) arg;
        int[] dim = arr.dimensions();
        if (dim == null) { return RNull.getNull(); }
        return RInt.RIntFactory.getArray(dim);
    }

}
