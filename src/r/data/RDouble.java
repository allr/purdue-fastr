package r.data;

import r.*;
import r.data.RInt.*;
import r.data.internal.*;

public interface RDouble extends RNumber {
    String TYPE_STRING = "numeric";
    long NA_LONGBITS = 0x7ff00000000007a2L; // R's NA is a special instance of IEEE's NaN
    double NA = Double.longBitsToDouble(NA_LONGBITS);

    RDouble EMPTY = RDoubleFactory.getUninitializedArray(0);
    RDouble BOXED_NA = RDoubleFactory.getArray(NA);

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
        public static DoubleImpl copy(RDouble d) {
            return new DoubleImpl(d);
        }
        public static RDouble getForArray(double[] values) {  // re-uses values!
            return new DoubleImpl(values, false);
        }
    }

    public static class RIntView extends View implements RInt {

        RDouble rdbl;

        public RIntView(RDouble rdbl) {
            this.rdbl = rdbl;
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public Object get(int i) {
            return getInt(i);
        }

        public RAny boxedGet(int i) {
            return RIntFactory.getScalar(getInt(i));
        }

        @Override
        public RArray set(int i, int val) {
            return materialize().set(i, val);
        }

        public int size() {
            return rdbl.size();
        }

        @Override
        public RInt materialize() {
            return RInt.RIntFactory.copy(this);
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

}
