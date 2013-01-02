package r.data;

import r.*;
import r.Convert.*;
import r.data.internal.*;

// NOTE: Java has signed bytes, but R has unsigned
public interface RRaw extends RArray {

    String TYPE_STRING = "raw";
    byte ZERO = 0;

    RawImpl EMPTY = (RawImpl) RArrayUtils.markShared(RRawFactory.getUninitializedArray(0));
    RawImpl BOXED_ZERO = (RawImpl) RArrayUtils.markShared(RRawFactory.getScalar(ZERO));

    RawImpl EMPTY_NAMED_NA = (RawImpl) RArrayUtils.markShared(RRawFactory.getFor(new byte[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    RawImpl ZERO_NAMED_NA = (RawImpl) RArrayUtils.markShared(RRawFactory.getFor(new byte[] {ZERO}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    byte getRaw(int il);
    RRaw set(int i, byte val);
    RRaw materialize();

    public class RRawFactory {
        public static RawImpl getScalar(byte value) {
            return getFor(new byte[] {value});
        }
        public static RawImpl getArray(byte... values) {
            return new RawImpl(values);
        }
        public static RawImpl getArray(byte[] values, int[] dimensions) {
            return new RawImpl(values, dimensions);
        }
        public static RawImpl getUninitializedArray(int size) {
            return new RawImpl(size);
        }
        public static RawImpl getUninitializedNonScalarArray(int size) {
            return new RawImpl(size);
        }
        public static RawImpl getUninitializedArray(int size, int[] dimensions) {
            return new RawImpl(new byte[size], dimensions, null, false);
        }
        public static RawImpl getZeroArray(int size) {
            return getUninitializedArray(size);
        }
        public static RawImpl getMatrixFor(byte[] values, int m, int n) {
            return new RawImpl(values, new int[] {m, n}, null, false);
        }
        public static RawImpl copy(RRaw v) {
            return new RawImpl(v, false);
        }
        public static RawImpl strip(RRaw v) {
            return new RawImpl(v, true);
        }
        public static RawImpl getFor(byte[] values) {  // re-uses values!
            return getFor(values, null, null);
        }
        public static RawImpl getFor(byte[] values, int[] dimensions, Names names) {  // re-uses values!
            return new RawImpl(values, dimensions, names, false);
        }
        public static RRaw getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static RRaw getZero(boolean named) {
            return named ? ZERO_NAMED_NA : BOXED_ZERO;
        }
        public static RRaw exclude(int excludeIndex, RRaw orig) {
            Names names = orig.names();
            if (names == null) {
                return new RRawExclusion(excludeIndex, orig);
            }
            int size = orig.size();
            int nsize = size - 1;
            byte[] content = new byte[nsize];
            for (int i = 0; i < excludeIndex; i++) {
                content[i] = orig.getRaw(i);
            }
            for (int i = excludeIndex; i < nsize; i++) {
                content[i] = orig.getRaw(i + 1);
            }
            return RRawFactory.getFor(content, null, names.exclude(excludeIndex));
        }
        public static RRaw subset(RRaw value, RInt index) {
            return new RRawSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringProxy<RRaw> implements RString {

        public RStringView(RRaw orig) {
            super(orig);
        }

        @Override
        public RRaw asRaw() {
            return orig;
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig;
        }

        @Override
        public String getString(int i) {
            byte v = orig.getRaw(i);
            return Convert.raw2string(v);
        }
    }

    public static class RComplexView extends View.RComplexProxy<RRaw> implements RComplex {

        public RComplexView(RRaw orig) {
            super(orig);
        }

        @Override
        public RDouble asDouble() {
            return orig.asDouble();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig;
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig;
        }

        @Override
        public double getReal(int i) {
            byte v = orig.getRaw(i);
            return Convert.raw2double(v);
        }

        @Override
        public double getImag(int i) {
            return 0;
        }
    }

    public static class RDoubleView extends View.RDoubleProxy<RRaw> implements RDouble {

        public RDoubleView(RRaw orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RInt asInt() {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig;
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt();
        }

        @Override
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig;
        }

        public double getDouble(int i) {
            byte v = orig.getRaw(i);
            return Convert.raw2double(v);
        }
    }

    public static class RIntView extends View.RIntProxy<RRaw> implements RInt {

        public RIntView(RRaw orig) {
            super(orig);
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
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig;
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
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig;
        }

        @Override
        public int getInt(int i) {
            return Convert.raw2int(orig.getRaw(i));
        }
    }

    public static class RLogicalView extends View.RLogicalProxy<RRaw> implements RLogical {

        public RLogicalView(RRaw orig) {
            super(orig);
        }

        @Override
        public int getLogical(int i) {
            return Convert.raw2logical(orig.getRaw(i));
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> 0's are returned in that case
    public static class RRawSubset extends View.RRawView implements RRaw {

        final RRaw value;
        final int vsize;
        final RInt index;
        final int isize;

        public RRawSubset(RRaw value, RInt index) {
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
        public byte getRaw(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RRaw.ZERO;
            } else {
                return value.getRaw(j - 1);
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

    public static class RRawExclusion extends View.RRawView implements RRaw {

        final RRaw orig;
        final int excludeIndex;
        final int size;

        public RRawExclusion(int excludeIndex, RRaw orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public byte getRaw(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getRaw(i);
            } else {
                return orig.getRaw(i + 1);
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
}
