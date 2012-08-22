package r.data;

import r.data.internal.*;


public interface RInt extends RNumber {
    int NA = Integer.MIN_VALUE;
    String TYPE_STRING = "integer";

    int getInt(int i);
    RArray set(int i, int val);

    public class RIntFactory {
        public static RInt getArray(int... values) {
            return new IntImpl(values);
        }
    }

}
