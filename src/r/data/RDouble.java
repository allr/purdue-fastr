package r.data;

import r.data.internal.*;

public interface RDouble extends RNumber {
    String TYPE_STRING = "numeric";
    double NA = Double.longBitsToDouble(0x7ff00000000007a2L);

    RArray set(int i, double val);
    double getDouble(int i);

    public class RDoubleFactory {
        public static RDouble getArray(double... values) {
            return new DoubleImpl(values);
        }
    }

}
