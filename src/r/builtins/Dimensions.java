package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: add a replacement version

public class Dimensions {

    private static final String[] paramNames = new String[] { "x" };
    private static final int IX = 0;

    public abstract static class Operation {
        public abstract RInt extract(int[] dimensions);
    }

    public static final class DimensionsCallFactory extends CallFactory {
        final Operation op;

        public DimensionsCallFactory(Operation op) {
            this.op = op;
        }

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);
            final boolean[] provided = a.providedParams;
            checkArgumentIsPresent(call, provided, paramNames, IX);

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

    /**
     * "dim(x)"
     * 
     * <pre>
     * x -- an R object, for example a matrix, array or data frame.
     * </pre>
     * 
     * dim has a method for data.frames, which returns the lengths of the
     * row.names attribute of x and of x (as the numbers of rows and columns
     * respectively).
     */
    public static final CallFactory DIM_FACTORY = new DimensionsCallFactory(new Operation() {
        @Override public RInt extract(int[] dimensions) {
            return RInt.RIntFactory.getArray(dimensions);
        }
    });

    /**
     * "nrow(x)"
     * 
     * <pre>
     * x --  a vector, array or data frame
     * </pre>
     * 
     * Returns an integer of length 1 or NULL.
     */
    public static final CallFactory NROW_FACTORY = new DimensionsCallFactory(new Operation() {
        @Override public RInt extract(int[] dimensions) {
            return dimensions.length > 0 ? RInt.RIntFactory.getScalar(dimensions[0]) : RInt.BOXED_NA;
        }
    });

    /**
     * "ncol(x)"
     * 
     * <pre>
     * x --  a vector, array or data frame
     * </pre>
     * 
     * Returns an integer of length 1 or NULL.
     */
    public static final CallFactory NCOL_FACTORY = new DimensionsCallFactory(new Operation() {
        @Override public RInt extract(int[] dimensions) {
            return dimensions.length > 1 ? RInt.RIntFactory.getScalar(dimensions[1]) : RInt.BOXED_NA;
        }
    });
}
