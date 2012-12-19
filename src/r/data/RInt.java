package r.data;

import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.data.internal.*;

// FIXME: add conversion to scalar representation to copies (also other types that have scalar representations)

public interface RInt extends RNumber {
    int NA = Integer.MIN_VALUE;
    String TYPE_STRING = "integer";

    ScalarIntImpl BOXED_NA = RIntFactory.getScalar(NA);
    ScalarIntImpl BOXED_ZERO = RIntFactory.getScalar(0);
    ScalarIntImpl BOXED_ONE = RIntFactory.getScalar(1);

    IntImpl EMPTY = (IntImpl) RIntFactory.getUninitializedArray(0);

    int getInt(int i);
    RInt set(int i, int val);
    RInt materialize();

    public class RIntUtils {
        public static RRaw intToRaw(RInt value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                int ival = value.getInt(i);
                content[i] = Convert.int2raw(ival, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions());
        }
    }
    public class RIntFactory {
        public static ScalarIntImpl getScalar(int value) {
            return new ScalarIntImpl(value);
        }
        public static RInt getScalar(int value, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarIntImpl(value);
            } else {
                return getFor(new int[] {value}, dimensions);
            }
        }
        public static RInt getArray(int... values) {
            if (values.length == 1) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values);
        }
        public static RInt getArray(int[] values, int[] dimensions) {
            if (dimensions == null && values.length == 1) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values, dimensions);
        }
        public static RInt getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarIntImpl(0);
            }
            return new IntImpl(size);
        }
        public static RInt getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarIntImpl(0);
            }
            return new IntImpl(new int[size], dimensions, false);
        }
        public static RInt getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RInt getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            int[] content = new int[size];
            Arrays.fill(content, NA);
            return new IntImpl(content, dimensions, false);
        }
        public static IntImpl getMatrixFor(int[] values, int m, int n) {
            return new IntImpl(values, new int[] {m, n}, false);
        }
        public static RInt copy(RInt i) {
            if (i.size() == 1 && i.dimensions() == null) {
                return new ScalarIntImpl(i.getInt(0));
            }
            return new IntImpl(i, false);
        }
        public static RInt copyValuesOnly(RInt i) {
            if (i.size() == 1) {
                return new ScalarIntImpl(i.getInt(0));
            }
            return new IntImpl(i, true);
        }
        public static RInt getFor(int[] values) { // re-uses values!
            return getFor(values, null);

        }
        public static RInt getFor(int[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 1 && dimensions == null) {
                return new ScalarIntImpl(values[0]);
            }
            return new IntImpl(values, dimensions, false);
        }
        public static RInt forSequence(int from, int to, int step) {
            return new IntImpl.RIntSequence(from, to, step);
        }
        public static RInt exclude(int excludeIndex, RInt orig) {
            return new RIntExclusion(excludeIndex, orig);
        }
        public static RInt subset(RInt value, RInt index) {
            return new RIntSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringView implements RString {

        final RInt orig;
        public RStringView(RInt orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public String getString(int i) {
            int v = orig.getInt(i);
            return Convert.int2string(v);
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
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RComplexView extends View.RComplexView implements RComplex {

        final RInt orig;
        public RComplexView(RInt orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public double getReal(int i) {
            int v = orig.getInt(i);
            return Convert.int2double(v);
        }

        @Override
        public double getImag(int i) {
            return 0;
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
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RDoubleView extends View.RDoubleView implements RDouble {

        final RInt orig;
        public RDoubleView(RInt orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public double getDouble(int i) {
            int v = orig.getInt(i);
            return Convert.int2double(v);
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
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RLogicalView extends View.RLogicalView implements RLogical {

        final RInt orig;
        public RLogicalView(RInt orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public int getLogical(int i) {
            int v = orig.getInt(i);
            return Convert.int2logical(v);
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
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RRawView extends View.RRawView implements RRaw { // FIXME: remove this? it breaks warnings

        final RInt orig;
        public RRawView(RInt orig) {
            this.orig = orig;
        }

        @Override
        public int size() {
            return orig.size();
        }

        @Override
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig;
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public byte getRaw(int i) {
            int v = orig.getInt(i);
            return Convert.int2raw(v);
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
        public int[] dimensions() {
            return orig.dimensions();
        }
    }

    public static class RIntExclusion extends View.RIntView implements RInt {

        final RInt orig;
        final int excludeIndex;
        final int size;

        public RIntExclusion(int excludeIndex, RInt orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getInt(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getInt(i);
            } else {
                return orig.getInt(i + 1);
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
    public static class RIntSubset extends View.RIntView implements RInt {

        final RInt value;
        final int vsize;
        final RInt index;
        final int isize;

        public RIntSubset(RInt value, RInt index) {
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
        public int getInt(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RInt.NA;
            } else {
                return value.getInt(j - 1);
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
