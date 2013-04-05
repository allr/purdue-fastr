package r.builtins;

import r.data.*;

final class IsMatrix extends IsBase {

    static final CallFactory _ = new IsMatrix("is.matrix");

    private IsMatrix(String name) {
        super(name);
    }

    @Override public boolean is(RAny arg) {
        return isMatrix(arg);
    }

    public static boolean isMatrix(RAny arg) {
        if (!(arg instanceof RArray)) {
            return false;
        }
        RArray a = (RArray) arg;
        int[] dim = a.dimensions();
        return (dim != null && dim.length == 2);
    }
}
