package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.data.internal.*;

public interface RComplex extends RArray {
    String TYPE_STRING = "complex";

    ComplexImpl EMPTY = (ComplexImpl) RComplexFactory.getUninitializedArray(0);
    ScalarComplexImpl BOXED_NA = RComplexFactory.getScalar(RDouble.NA, RDouble.NA);
    ScalarComplexImpl BOXED_ZERO = RComplexFactory.getScalar(0, 0);

    RComplex set(int i, double real, double imag);
    double getReal(int i);
    double getImag(int i);
    RComplex materialize();

    public final class Complex {
        public static final Complex NA = new Complex(RDouble.NA, RDouble.NA);

        private final double real;
        private final double imag;

        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }

        public double realValue() {
            return real;
        }

        public double imagValue() {
            return imag;
        }
    }

    public class RComplexUtils {
        public static boolean eitherIsNA(double real, double imag) {
            return RDouble.RDoubleUtils.isNA(real) || RDouble.RDoubleUtils.isNA(imag);
        }
        public static boolean eitherIsNAorNaN(double real, double imag) {
            return RDouble.RDoubleUtils.isNAorNaN(real) ||  RDouble.RDoubleUtils.isNAorNaN(imag);
        }
        public static RDouble complex2double(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            double[] content = new double[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2double(real, imag, warn);
            }
            return RDouble.RDoubleFactory.getFor(content, value.dimensions());
        }
        public static RInt complex2int(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2int(real, imag, warn);
            }
            return RInt.RIntFactory.getFor(content, value.dimensions());
        }
        public static RRaw complexToRaw(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2raw(real, imag, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions());
        }
    }

    public class RComplexFactory {
        public static ScalarComplexImpl getScalar(double real, double imag) {
            return new ScalarComplexImpl(real, imag);
        }
        public static ScalarComplexImpl getScalar(Complex value) {
            return new ScalarComplexImpl(value.realValue(), value.imagValue());
        }
        public static RComplex getScalar(double real, double imag, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarComplexImpl(real, imag);
            } else {
                return getFor(new double[] {real, imag}, dimensions);
            }
        }
        public static RComplex getArray(double... values) {
            if (values.length == 2) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values);
        }
        public static RComplex getArray(double[] values, int[] dimensions) {
            if (dimensions == null && values.length == 2) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values, dimensions);
        }
        public static RComplex getArray(double[] realValues, double[] imagValues, int[] dimensions) {
            if (dimensions == null && realValues.length == 1) {
                return new ScalarComplexImpl(realValues[0], imagValues[0]);
            }
            return new ComplexImpl(realValues, imagValues, dimensions);
        }
        public static RComplex getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarComplexImpl(0, 0);
            }
            return new ComplexImpl(size);
        }
        public static RComplex getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarComplexImpl(0, 0);
            }
            return new ComplexImpl(new double[2 * size], dimensions, false);
        }
        public static RComplex getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RComplex getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            double[] content = new double[2 * size];
            Arrays.fill(content, RDouble.NA);
            return new ComplexImpl(content, dimensions, false);
        }
        public static ComplexImpl getMatrixFor(double[] values, int m, int n) {
            return new ComplexImpl(values, new int[] {m, n}, false);
        }
        public static RComplex copy(RComplex c) {
            if (c.size() == 1 && c.dimensions() == null) {  // FIXME: rely instead on scalarization ?
                return new ScalarComplexImpl(c.getReal(0), c.getImag(0));
            }
            return new ComplexImpl(c);
        }
        public static RComplex getFor(double[] values) { // re-uses values!
            return getFor(values, null);
        }
        public static RComplex getFor(double[] values, int[] dimensions) {  // re-uses values!
            if (values.length == 2 && dimensions == null) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values, dimensions, false);
        }
        public static RComplex exclude(int excludeIndex, RComplex orig) {
            return new RComplexExclusion(excludeIndex, orig);
        }
        public static RComplex subset(RComplex value, RInt index) {
            return new RComplexSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringView implements RString {

        RComplex orig;

        public RStringView(RComplex orig) {
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
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RComplex asComplex() {
            return orig;
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
            return orig.asRaw();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble(warn);
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
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
        public String getString(int i) {
            return Convert.complex2string(orig.getReal(i), orig.getImag(i));
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

        RComplex orig;

        public RLogicalView(RComplex orig) {
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
        public RList asList() {
            return orig.asList();
        }

        @Override
        public RString asString() {
            return orig.asString();
        }

        @Override
        public RComplex asComplex() {
            return orig;
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
        public RRaw asRaw() {
            return orig.asRaw();
        }

        @Override
        public RString asString(ConversionStatus warn) {
            return orig.asString();
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig;
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig.asDouble(warn);
        }

        @Override
        public RInt asInt(ConversionStatus warn) {
            return orig.asInt(warn);
        }

        @Override
        public RRaw asRaw(ConversionStatus warn) {
            return orig.asRaw(warn);
        }

        @Override
        public int getLogical(int i) {
            return Convert.complex2logical(orig.getReal(i), orig.getImag(i));
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
    public static class RComplexExclusion extends View.RComplexView implements RComplex {

        final RComplex orig;
        final int excludeIndex;
        final int size;

        public RComplexExclusion(int excludeIndex, RComplex orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double getReal(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getReal(i);
            } else {
                return orig.getReal(i + 1);
            }
        }

        @Override
        public double getImag(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getImag(i);
            } else {
                return orig.getImag(i + 1);
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
    public static class RComplexSubset extends View.RComplexView implements RComplex {

        final RComplex value;
        final int vsize;
        final RInt index;
        final int isize;

        public RComplexSubset(RComplex value, RInt index) {
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
        public double getReal(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getReal(j - 1);
            }
        }

        @Override
        public double getImag(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getImag(j - 1);
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
