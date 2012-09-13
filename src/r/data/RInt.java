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
        public static RInt getArray(int... values) {
            return new IntImpl(values);
        }
        public static RInt getUninitializedArray(int size) {
            return new IntImpl(size);
        }
        public static RInt copy(RInt i) {
            return new IntImpl(i);
        }
        public static RInt getForArray(int[] values) {  // re-uses values!
            return new IntImpl(values, false);
        }
    }

}
