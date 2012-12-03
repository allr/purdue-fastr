package r.data;

import r.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

public interface RString extends RArray {

    String TYPE_STRING = "character";

    StringImpl EMPTY = (StringImpl) RStringFactory.getUninitializedArray(0);
    String NA = null;
    ScalarStringImpl BOXED_NA = RStringFactory.getScalar(NA);

    String getString(int i);
    RString set(int i, String val);
    RString materialize();

    public class RStringUtils {
        public static RDouble stringToDouble(RString value, RContext context, ASTNode ast) {
            boolean introducedNA = false;

            int size = value.size();
            double[] dcontent = new double[size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                if (str != RString.NA) {
                    try {
                        dcontent[i] = Double.parseDouble(str);  // FIXME: use R rules
                    } catch (NumberFormatException e) {
                        dcontent[i] = RDouble.NA;
                        introducedNA = true;
                    }
                } else {
                    dcontent[i] = RDouble.NA;
                }
            }
            if (introducedNA) {
                context.warning(ast, RError.NA_INTRODUCED_COERCION);
            }
            return RDouble.RDoubleFactory.getFor(dcontent); // drops dimensions
        }
    }

    public class RStringFactory {
        public static ScalarStringImpl getScalar(String value) {
            return new ScalarStringImpl(value);
        }
        public static RString getArray(String... values) {
            if (values.length == 1) {
                return new ScalarStringImpl(values[0]);
            }
            return new StringImpl(values);
        }
        public static RString getArray(String[] values, int[] dimensions) {
            if (dimensions == null && values.length == 1) {
                return new ScalarStringImpl(values[0]);
            }
            return new StringImpl(values, dimensions);
        }
        public static RString getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarStringImpl(NA);
            }
            return new StringImpl(size);
        }
        public static RString getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarStringImpl(NA);
            }
            return new StringImpl(new String[size], dimensions, false);
        }
        public static RString getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RString getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarStringImpl(NA);
            }
            String[] content = new String[size];
            for (int i = 0; i < size; i++) {
                content[i] = NA;
            }
            return new StringImpl(content, dimensions, false);
        }
        public static StringImpl getMatrixFor(String[] values, int m, int n) {
            return new StringImpl(values, new int[] {m, n}, false);
        }
        public static RString copy(RString s) {
            if (s.size() == 1 && s.dimensions() == null) {
                return new ScalarStringImpl(s.getString(0));
            }
            return new StringImpl(s, false);
        }
        public static RString getFor(String[] values) { // re-uses values!
            return getFor(values, null);

        }
        public static RString getFor(String[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 1 && dimensions == null) {
                return new ScalarStringImpl(values[0]);
            }
            return new StringImpl(values, dimensions, false);
        }
        public static RString exclude(int excludeIndex, RString orig) {
            return new RStringExclusion(excludeIndex, orig);
        }
        public static RString subset(RString value, RInt index) {
            return new RStringSubset(value, index);
        }
    }

    public static class RStringExclusion extends View.RStringView implements RString {

        final RString orig;
        final int excludeIndex;
        final int size;

        public RStringExclusion(int excludeIndex, RString orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public String getString(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getString(i);
            } else {
                return orig.getString(i + 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
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
