package r.builtins;

import r.data.*;

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
