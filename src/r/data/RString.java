package r.data;

import r.data.internal.*;

public interface RString extends RArray {

    String TYPE_STRING = "character";

    StringImpl EMPTY = RStringFactory.getScalar("");
    String NA = null;

    String getString(int i);
    RArray set(int i, String val);
    RString materialize();

    public class RStringFactory {
        public static StringImpl getScalar(String value) {
            return new StringImpl(new String[]{value}, null, false);
        }
        public static StringImpl getArray(String... values) {
            return new StringImpl(values);
        }
        public static StringImpl getUninitializedArray(int size) {
            return new StringImpl(size);
        }
        public static StringImpl getUninitializedArray(int size, int[] dimensions) {
            return new StringImpl(new String[size], dimensions);
        }
        public static StringImpl getMatrixFor(String[] values, int m, int n) {
            return new StringImpl(values, new int[] {m, n}, false);
        }
        public static StringImpl copy(RString v) {
            return new StringImpl(v);
        }
        public static RString subset(RString value, RInt index) {
            return new RStringSubset(value, index);
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RStringSubset extends View.RStringView implements RString {

        final RString value;
        final int vsize;
        final RInt index;
        final int isize;

        public RStringSubset(RString value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public String getString(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RString.NA;
            } else {
                return value.getString(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return value.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            value.ref();
            index.ref();
        }
    }
}
