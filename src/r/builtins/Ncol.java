package r.builtins;

import r.data.*;

// TODO: add a replacement version
/**
 * "ncol(x)"
 * 
 * <pre>
 * x --  a vector, array or data frame
 * </pre>
 * 
 * Returns an integer of length 1 or NULL.
 */
final class Ncol extends DimensionsBase {

    static final CallFactory _ = new Ncol("ncol", new Operation() {
        @Override public RInt extract(int[] dimensions) {
            return dimensions.length > 1 ? RInt.RIntFactory.getScalar(dimensions[1]) : RInt.BOXED_NA;
        }
    });

    private Ncol(String name, Operation op) {
        super(name, op);
    }
}
