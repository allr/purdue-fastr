package r.builtins;

import r.data.*;

/**
 * "min"
 * 
 * <pre>
 * ... -- numeric or character arguments
 * na.rm -- a logical indicating whether missing values should be removed.
 * </pre>
 */
final class Min extends ExtremeBase {
    static final CallFactory _ = new Min("min", new String[]{"...", "na.rm"}, new String[]{});

    Min(String name, String[] params, String[] required) {
        super(name, params, required, new Operation() {
            @Override public boolean moreExtreme(int a, int b) {
                return a < b;
            }

            @Override public boolean moreExtreme(double a, double b) {
                return a < b;
            }

            @Override public boolean moreExtreme(String a, String b) {
                return a.compareTo(b) < 0;
            }

            @Override public int extreme(int a, int b) {
                return Math.min(a, b);
            }

            @Override public double extreme(double a, double b) {
                return Math.min(a, b);
            }

            @Override public String extreme(String a, String b) {
                return a.compareTo(b) <= 0 ? a : b;
            }

            @Override public RDouble emptySetExtreme() {
                return RDouble.BOXED_POS_INF;
            }
        });
    }

}
