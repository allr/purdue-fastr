package r.builtins;

import r.data.*;

// FIXME should issue a warning
final class Max extends ExtremeBase {
    static final CallFactory _ = new Max("max", new String[]{"...", "na.rm"}, new String[]{});

    Max(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override boolean moreExtreme(int a, int b) {
        return a > b;
    }

    @Override boolean moreExtreme(double a, double b) {
        return a > b;
    }

    @Override boolean moreExtreme(String a, String b) {
        return a.compareTo(b) > 0;
    }

    @Override int extreme(int a, int b) {
        return Math.max(a, b);
    }

    @Override double extreme(double a, double b) {
        return Math.max(a, b);
    }

    @Override String extreme(String a, String b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    @Override RDouble emptySetExtreme() {
        return RDouble.BOXED_NEG_INF;
    }
}
