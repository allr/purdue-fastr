package r.data;

import r.*;
import r.Convert.*;
import r.data.RComplex.*;
import r.data.internal.*;

public interface RString extends RArray {

    String TYPE_STRING = "character";

    StringImpl EMPTY = (StringImpl) RArrayUtils.markShared(RStringFactory.getUninitializedArray(0));
    String NA = null;
    ScalarStringImpl BOXED_NA = (ScalarStringImpl) RArrayUtils.markShared(RStringFactory.getScalar(NA));
    ScalarStringImpl BOXED_DOT = (ScalarStringImpl) RArrayUtils.markShared(RStringFactory.getScalar("."));
    StringImpl EMPTY_NAMED_NA = (StringImpl) RArrayUtils.markShared(RStringFactory.getFor(new String[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    StringImpl NA_NAMED_NA = (StringImpl) RArrayUtils.markShared(RStringFactory.getFor(new String[] {NA}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    String getString(int i);
    RString set(int i, String val);
    RString materialize();

    public class RStringUtils {

        public static RComplex stringToComplex(RString value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            double[] content = new double[2 * size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                Complex c = Convert.string2complex(str, warn);
                content[2 * i] = c.realValue();
                content[2 * i + 1] = c.imagValue();
            }
            return RComplex.RComplexFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RDouble stringToDouble(RString value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            double[] content = new double[size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                content[i] = Convert.string2double(str, warn);
            }
            return RDouble.RDoubleFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RInt stringToInt(RString value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                content[i] = Convert.string2int(str, warn);
            }
            return RInt.RIntFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RLogical stringToLogical(RString value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                content[i] = Convert.string2logical(str, warn);
            }
            return RLogical.RLogicalFactory.getFor(content, value.dimensions(), null);
        }
        public static RRaw stringToRaw(RString value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                String str = value.getString(i);
                content[i] = Convert.string2raw(str, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions(), value.names());
        }
    }

    public class RStringFactory {
        public static ScalarStringImpl getScalar(String value) {
            return new ScalarStringImpl(value);
        }
        public static RString getScalar(String value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarStringImpl(value);
            } else {
                return getFor(new String[] {value}, dimensions, null);
            }
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
        public static RString getUninitializedNonScalarArray(int size) {
            return new StringImpl(size);
        }
        public static RString getUninitializedArray(int size, int[] dimensions, Names names, Attributes attributes) {
            if (size == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarStringImpl(NA);
            }
            return new StringImpl(new String[size], dimensions, names, attributes, false);
        }
        public static RString getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RString getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            String[] content = new String[size];
            for (int i = 0; i < size; i++) {
                content[i] = NA;
            }
            return new StringImpl(content, dimensions, null, null, false);
        }
        public static StringImpl getMatrixFor(String[] values, int m, int n) {
            return new StringImpl(values, new int[] {m, n}, null, null, false);
        }
        public static RString copy(RString s) {
            if (s.size() == 1 && s.dimensions() == null && s.names() == null && s.attributes() == null) {
                return new ScalarStringImpl(s.getString(0));
            }
            return new StringImpl(s, false);
        }
        public static RString strip(RString v) {
            if (v.size() == 1) {
                return new ScalarStringImpl(v.getString(0));
            }
            return new StringImpl(v, true);
        }
        public static RString getFor(String[] values) { // re-uses values!
            return getFor(values, null, null);

        }
        public static RString getFor(String[] values, int[] dimensions, Names names) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null) {
                return new ScalarStringImpl(values[0]);
            }
            return new StringImpl(values, dimensions, names, null, false);
        }
        public static RString getFor(String[] values, int[] dimensions, Names names, Attributes attributes) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarStringImpl(values[0]);
            }
            return new StringImpl(values, dimensions, names, attributes, false);
        }
        public static RString getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static RString getNA(boolean named) {
            return named ? NA_NAMED_NA : BOXED_NA;
        }
        public static RString exclude(int excludeIndex, RString orig) {
            Names names = orig.names();
            if (names == null) {
                return new RStringExclusion(excludeIndex, orig);
            }
            int size = orig.size();
            int nsize = size - 1;
            String[] content = new String[nsize];
            for (int i = 0; i < excludeIndex; i++) {
                content[i] = orig.getString(i);
            }
            for (int i = excludeIndex; i < nsize; i++) {
                content[i] = orig.getString(i + 1);
            }
            return RStringFactory.getFor(content, null, names.exclude(excludeIndex));
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
            assert Utils.check(i < size, "bounds check");
            assert Utils.check(i >= 0, "bounds check");

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

        @Override
        public boolean dependsOn(RAny value) {
            return orig.dependsOn(value);
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

        @Override
        public boolean dependsOn(RAny v) {
            return value.dependsOn(v) || index.dependsOn(v);
        }
    }
}
