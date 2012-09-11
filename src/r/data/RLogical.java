package r.data;

import r.data.internal.*;


public interface RLogical extends RArray {
    String TYPE_STRING = "logical";
    int TRUE = 1;
    int FALSE = 0;
    int NA = Integer.MIN_VALUE;
    RLogical BOXED_TRUE = RLogicalFactory.getArray(TRUE);
    RLogical BOXED_FALSE = RLogicalFactory.getArray(FALSE);
    RLogical BOXED_NA = RLogicalFactory.getArray(NA);

    int getLogical(int il);
    RLogical set(int i, int val);

    public class RLogicalFactory {
        public static RLogical getArray(int... values) {
            return new LogicalImpl(values);
        }

        public static RLogical getEmptyArray(int size) {
            return new LogicalImpl(size);
        }

        public static RLogical getNAArray(int size) {
            RLogical l = getEmptyArray(size);
            for(int i = 0; i < size ; i++) {
                l.set(size, NA);
            }
            return l;
        }
    }
}
