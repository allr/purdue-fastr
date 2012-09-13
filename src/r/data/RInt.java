package r.data;

import r.data.internal.*;


public interface RInt extends RNumber {
    int NA = Integer.MIN_VALUE;
    String TYPE_STRING = "integer";

    RInt BOXED_NA = RIntFactory.getArray(NA);
    RInt EMPTY = RIntFactory.getUninitializedArray(0);

    int getInt(int i);
    RArray set(int i, int val);

    public class RIntFactory {
        public static IntImpl getArray(int... values) {
            return new IntImpl(values);
        }
        public static IntImpl getUninitializedArray(int size) {
            return new IntImpl(size);
        }
        public static IntImpl copy(RInt i) {
            return new IntImpl(i);
        }
    }

}
