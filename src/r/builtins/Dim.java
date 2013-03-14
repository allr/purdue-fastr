package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

// TODO: add a replacement version
/**
 * "dim(x)"
 * 
 * <pre>
 * x -- an R object, for example a matrix, array or data frame.
 * </pre>
 * 
 * dim has a method for data.frames, which returns the lengths of the row.names attribute of x and of x (as the numbers
 * of rows and columns respectively).
 */
final class Dim extends DimensionsBase {

    static final CallFactory _ = new Dim("dim");

    @Override public RInt extract(int[] dimensions) {
        return RInt.RIntFactory.getArray(dimensions);
    }

    private Dim(String name) {
        super(name);
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
