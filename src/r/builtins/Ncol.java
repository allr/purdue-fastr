package r.builtins;

import r.data.*;

// TODO: add a replacement version

final class Ncol extends DimensionsBase {

    static final CallFactory _ = new Ncol("ncol");

    @Override RInt extract(int[] dimensions) {
        return dimensions.length > 1 ? RInt.RIntFactory.getScalar(dimensions[1]) : RInt.BOXED_NA;
    }

    private Ncol(String name) {
        super(name);
    }
}
