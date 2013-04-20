package r.builtins;

import r.data.*;

// TODO: add a replacement version

final class Nrow extends DimensionsBase {

    static final CallFactory _ = new Nrow("nrow");

    @Override RInt extract(int[] dimensions) {
        return dimensions.length > 0 ? RInt.RIntFactory.getScalar(dimensions[0]) : RInt.BOXED_NA;
    }

    private Nrow(String name) {
        super(name);
    }
}
