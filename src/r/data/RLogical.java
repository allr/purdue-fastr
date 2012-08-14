package r.data;

import r.data.internal.*;


public interface RLogical extends RArray {
    String TYPE_STRING = "logical";
    int NA = Integer.MIN_VALUE;

    public class RLogicalFactory {
        public static RLogical getArray(int... values) {
            return new LogicalImpl(values);
        }
    }
}
