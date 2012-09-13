package r.data;

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
    }
    public class RDoubleFactory {
        public static RDouble getArray(double... values) {
            return new DoubleImpl(values);
        }
        public static RDouble getUninitializedArray(int size) {
            return new DoubleImpl(size);
        }
        public static RDouble copy(RDouble d) {
            return new DoubleImpl(d);
        }
    }

}
