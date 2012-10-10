package r.data;

import r.*;
import r.data.internal.*;

public interface RDouble extends RNumber {
    String TYPE_STRING = "numeric";
    long NA_LONGBITS = 0x7ff00000000007a2L; // R's NA is a special instance of IEEE's NaN
    double NA = Double.longBitsToDouble(NA_LONGBITS);
    double EPSILON = Math.pow(2.0, -52.0);
    double NEG_INF = Double.NEGATIVE_INFINITY;

    DoubleImpl EMPTY = RDoubleFactory.getUninitializedArray(0);
    DoubleImpl BOXED_NA = RDoubleFactory.getArray(NA);
    DoubleImpl BOXED_NEG_INF = RDoubleFactory.getScalar(Double.NEGATIVE_INFINITY);

    RArray set(int i, double val);
    double getDouble(int i);

    public class RDoubleUtils {
        public static boolean isNA(double d) {
            return Double.doubleToRawLongBits(d) == NA_LONGBITS;
        }
        public static boolean fitsRInt(double d) {
            return d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE;
        }
        public static boolean isNAorNaN(double d) {
            return Double.isNaN(d);
        }
        public static boolean isFinite(double d) {
            return !isNAorNaN(d) && !Double.isInfinite(d);
        }
    }
    public class RDoubleFactory {
        public static DoubleImpl getScalar(double value) {
            return new DoubleImpl(new double[]{value}, false);
        }
        public static DoubleImpl getArray(double... values) {
            return new DoubleImpl(values);
        }
        public static DoubleImpl getUninitializedArray(int size) {
            return new DoubleImpl(size);
        }
        public static DoubleImpl getNAArray(int size) {
            DoubleImpl d = getUninitializedArray(size);
            for (int i = 0; i < size; i++) {
                d.set(i, NA);
            }
            return d;
        }
        public static DoubleImpl copy(RDouble d) {
            return new DoubleImpl(d);
        }
        public static RDouble getForArray(double[] values) {  // re-uses values!
            return new DoubleImpl(values, false);
        }
        public static RDouble exclude(int excludeIndex, RDouble orig) {
            return new RDoubleExclusion(excludeIndex, orig);
        }
        public static RDouble subset(RDouble value, RInt index) {
            return new RDoubleSubset(value, index);
        }
    }

    public static class RIntView extends View.RIntView implements RInt {

        RDouble rdbl;

        public RIntView(RDouble rdbl) {
            this.rdbl = rdbl;
        }

        @Override
        public int size() {
            return rdbl.size();
        }

        @Override
        public RAttributes getAttributes() {
            return rdbl.getAttributes();
        }

        @Override
        public RLogical asLogical() {
            return rdbl.asLogical();
        }

        @Override
        public RDouble asDouble() {
            return rdbl;
        }

        @Override
        public int getInt(int i) {
            return Convert.double2int(rdbl.getDouble(i));
        }
    }

    public static class RLogicalView extends View.RLogicalView implements RLogical {

        RDouble rdbl;

        public RLogicalView(RDouble rdbl) {
            this.rdbl = rdbl;
        }

        @Override
        public int size() {
            return rdbl.size();
        }

        @Override
        public RAttributes getAttributes() {
            return rdbl.getAttributes();
        }

        @Override
        public RInt asInt() {
            return rdbl.asInt();
        }

        @Override
        public RDouble asDouble() {
            return rdbl;
        }

        @Override
        public int getLogical(int i) {
            return Convert.double2logical(rdbl.getDouble(i));
        }
    }

    public static class RDoubleExclusion extends View.RDoubleView implements RDouble {

        final RDouble orig;
        final int excludeIndex;
        final int size;

        public RDoubleExclusion(int excludeIndex, RDouble orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double getDouble(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getDouble(i);
            } else {
                return orig.getDouble(i + 1);
            }
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RDoubleSubset extends View.RDoubleView implements RDouble {

        final RDouble value;
        final int vsize;
        final RInt index;
        final int isize;

        public RDoubleSubset(RDouble value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public double getDouble(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getDouble(j - 1);
            }
        }
    }
}
