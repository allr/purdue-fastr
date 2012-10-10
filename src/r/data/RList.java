package r.data;

import r.data.internal.*;

public interface RList extends RArray {

    String TYPE_STRING = "list";

    public class RListFactory {
        public static ListImpl getArray(RAny... values) {
            return new ListImpl(values);
        }
        public static ListImpl getUninitializedArray(int size) {
            return new ListImpl(size);
        }
        public static ListImpl getForArray(RAny[] values) {  // re-uses values!
            return new ListImpl(values, false);
        }
    }
}
