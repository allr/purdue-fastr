package r.data;

import r.*;
import r.Convert.*;
import r.data.internal.*;

// NOTE: Java has signed bytes, but R has unsigned
public interface RRaw extends RArray {

    String TYPE_STRING = "raw";
    byte ZERO = 0;

    RawImpl EMPTY = RRawFactory.getUninitializedArray(0);

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
        public static RawImpl getUninitializedArray(int size, int[] dimensions) {
            return new RawImpl(new byte[size], dimensions, false);
        }
        public static RawImpl getMatrixFor(byte[] values, int m, int n) {
            return new RawImpl(values, new int[] {m, n}, false);
        }
        public static RawImpl copy(RRaw v) {
            return new RawImpl(v);
        }
        public static RawImpl getFor(byte[] values) {  // re-uses values!
            return getFor(values, null);
        }
        public static RawImpl getFor(byte[] values, int[] dimensions) {  // re-uses values!
            return new RawImpl(values, dimensions, false);
        }
        public static RRaw subset(RRaw value, RInt index) {
            return new RRawSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringView implements RString {

        final RRaw orig;
        public RStringView(RRaw v) {
            this.orig = v;
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
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public String getString(int i) {
            byte v = orig.getRaw(i);
            return Convert.raw2string(v);
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

        final RRaw orig;
        public RComplexView(RRaw v) {
            this.orig = v;
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
        public RString asString(ConversionStatus warn) {
            return orig.asString();
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

        final RRaw orig;
        public RDoubleView(RRaw v) {
            this.orig = v;
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
        public RString asString(ConversionStatus warn) {
            return orig.asString();
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

        @Override
        public double getDouble(int i) {
            byte v = orig.getRaw(i);
            return Convert.raw2double(v);
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

    public static class RIntView extends View.RIntView implements RInt {

        final RRaw orig;
        public RIntView(RRaw v) {
            this.orig = v;
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
        public RLogical asLogical() {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw() {
            return orig;
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
        public RLogical asLogical(ConversionStatus warn) {
            return orig.asLogical();
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RAttributes getAttributes() {
            return orig.getAttributes();
        }

        @Override
        public int getInt(int i) {
            return Convert.raw2int(orig.getRaw(i));
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

        RRaw orig;

        public RLogicalView(RRaw v) {
            this.orig = v;
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
            return Convert.raw2logical(orig.getRaw(i));
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
}
