package r.data;

import r.data.internal.*;


public interface RLogical extends RArray {
    String TYPE_STRING = "logical";
    int TRUE = 1;
    int FALSE = 0;
    int NA = Integer.MIN_VALUE;

    int getLogical(int il);
    RLogical set(int i, int val);

    public class RLogicalFactory {
        public static RLogical getArray(int... values) {
            return new LogicalImpl(values);
        }

        public static RLogical getEmptyArray(int size) {
            return new LogicalImpl(size);
        }
    }
}
