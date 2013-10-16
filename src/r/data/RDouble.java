package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.data.internal.*;

public interface RDouble extends RNumber {

    String TYPE_STRING = "double";
    long NA_LONGBITS = 0x7ff00000000007a2L; // R's NA is a special instance of IEEE's NaN
    int NA_LOWBITS = (int) NA_LONGBITS;
    double NA = Double.longBitsToDouble(NA_LONGBITS);
    double NaN = Double.NaN;
    double EPSILON = Math.pow(2.0, -52.0);
    double NEG_INF = Double.NEGATIVE_INFINITY;
    double POS_INF = Double.POSITIVE_INFINITY;

    DoubleImpl EMPTY = (DoubleImpl) RArrayUtils.markShared(RDoubleFactory.getUninitializedArray(0));
    ScalarDoubleImpl BOXED_ZERO = (ScalarDoubleImpl) RArrayUtils.markShared(RDoubleFactory.getScalar(0));
    ScalarDoubleImpl BOXED_NA = (ScalarDoubleImpl) RArrayUtils.markShared(RDoubleFactory.getScalar(NA));
    ScalarDoubleImpl BOXED_NEG_INF = (ScalarDoubleImpl) RArrayUtils.markShared(RDoubleFactory.getScalar(Double.NEGATIVE_INFINITY));
    ScalarDoubleImpl BOXED_POS_INF = (ScalarDoubleImpl) RArrayUtils.markShared(RDoubleFactory.getScalar(Double.POSITIVE_INFINITY));

    DoubleImpl EMPTY_NAMED_NA = (DoubleImpl) RArrayUtils.markShared(RDoubleFactory.getFor(new double[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    DoubleImpl NA_NAMED_NA = (DoubleImpl) RArrayUtils.markShared(RDoubleFactory.getFor(new double[] {NA}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    RDouble set(int i, double val);
    double getDouble(int i);
    RDouble materialize();
    double[] getContent();

    double sum(boolean narm);

    public class RDoubleUtils {
        public static final boolean ARITH_NA_CHECKS = false;
        // should have explicit checks with floating point arithmetics to avoid NAs turning into NaNs?
        // NOTE: GNU-R does not have these checks


        public static boolean isNA(double d) {
            return ((int) Double.doubleToRawLongBits(d)) == NA_LOWBITS;
        }
        public static boolean arithIsNA(double d) {
            return ARITH_NA_CHECKS ? isNA(d) : false;
        }
        public static boolean fitsRInt(double d) {
            return d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE;
        }
        public static boolean isNAorNaN(double d) {
            return Double.isNaN(d);
        }
        public static boolean isFinite(double d) {
            return !isNAorNaN(d) && !Double.isInfinite(d);
        }
        public static RInt double2int(RDouble value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                double d = value.getDouble(i);
                content[i] = Convert.double2int(d, warn);
            }
            return RInt.RIntFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RRaw doubleToRaw(RDouble value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                double dval = value.getDouble(i);
                content[i] = Convert.double2raw(dval, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions(), value.names());
        }
        public static double[] copyAsDoubleArray(RDouble d) {
            int size = d.size();
            if (size == 1) {
                return new double[] {d.getDouble(0)};
            } else {
                double[] res = new double[size];

                if (d instanceof DoubleImpl) {
                    System.arraycopy(((DoubleImpl) d).getContent(), 0, res, 0, size);
                } else {
                    for (int i = 0; i < size; i++) {
                        res[i] = d.getDouble(i);
                    }
                }
                return res;
            }
        }
        public static boolean hasNAorNaN(RDouble d) {
            int size = d.size();
            for (int i = 0; i < size; i++) {
                if (isNAorNaN(d.getDouble(i))) {
                    return true;
                }
            }
            return false;
        }
        public static RDouble convertNAandNaNtoZero(RDouble d) {
            if (d instanceof ScalarDoubleImpl) {
                ScalarDoubleImpl sd = (ScalarDoubleImpl) d;
                if (sd.isNAorNaN()) {
                    return BOXED_ZERO;
                } else {
                    return sd;
                }
            } else {
                RDouble res = d.materialize();
                double[] content = res.getContent();
                for (int i = 0; i < content.length; i++) {
                    if (isNAorNaN(content[i])) {
                        content[i] = 0;
                    }
                }
                return res;
            }
        }
        public static double sum(RDouble v, boolean narm) {
            int size = v.size();
            double res = 0;
            for (int i = 0; i < size; i++) {
                double d = v.getDouble(i);
                if (narm) {
                    if (RDouble.RDoubleUtils.isNAorNaN(d)) {
                        continue;
                    }
                }
                res += d;
            }
            return res;
        }
    }
    public class RDoubleFactory {
        public static ScalarDoubleImpl getScalar(double value) {
            return new ScalarDoubleImpl(value);
        }
        public static RDouble getScalar(double value, int[] dimensions, Names names, Attributes attributes) {
            if (dimensions == null && names == null && attributes == null) {
                return new ScalarDoubleImpl(value);
            } else {
                return getFor(new double[] {value}, dimensions, names, attributes);
            }
        }
        public static RDouble getArray(double... values) {
            if (values.length == 1) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values);
        }
        public static RDouble getArray(double[] values, int[] dimensions) {
            if (dimensions == null && values.length == 1) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values, dimensions, null);
        }
        public static RDouble getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarDoubleImpl(0);
            }
            return new DoubleImpl(size);
        }
        public static RDouble getUninitializedNonScalarArray(int size) {
            return new DoubleImpl(size);
        }
        public static RDouble getUninitializedArray(int size, int[] dimensions, Names names, Attributes attributes) {
            if (size == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarDoubleImpl(0);
            }
            return new DoubleImpl(new double[size], dimensions, names, attributes, false);
        }
        public static RDouble getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RDouble getNAArray(int size, int[] dimensions) {
            return getNAArray(size, dimensions, null, null);
        }
        public static RDouble getNAArray(int size, int[] dimensions, Names names, Attributes attributes) {
            if (size == 1 && dimensions == null && names == null && attributes == null) {
                return BOXED_NA;
            }
            double[] content = new double[size];
            Arrays.fill(content, NA);
            return new DoubleImpl(content, dimensions, names, attributes, false);
        }
        public static DoubleImpl getMatrixFor(double[] values, int m, int n) {
            return new DoubleImpl(values, new int[] {m, n}, null, null, false);
        }
        public static RDouble copy(RDouble d) {
            if (d.size() == 1 && d.dimensions() == null && d.names() == null && d.attributes() == null) {
                return new ScalarDoubleImpl(d.getDouble(0));
            }
            return new DoubleImpl(d, false);
        }
        public static RDouble strip(RDouble v) {
            if (v.size() == 1) {
                return new ScalarDoubleImpl(v.getDouble(0));
            }
            return new DoubleImpl(v, true);
        }
        public static RDouble stripKeepNames(RDouble v) {
            Names names = v.names();
            if (v.size() == 1 && names == null) {
                return new ScalarDoubleImpl(v.getDouble(0));
            }
            return new DoubleImpl(v, null, names, null);
        }
        public static RDouble getFor(double[] values) { // re-uses values!
            return getFor(values, null, null);
        }
        public static RDouble getFor(double[] values, int[] dimensions, Names names) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values, dimensions, names, null, false);
        }
        public static RDouble getFor(double[] values, int[] dimensions, Names names, Attributes attributes) {  // re-uses values!
            if (values.length == 1 && dimensions == null && names == null && attributes == null) {
                return new ScalarDoubleImpl(values[0]);
            }
            return new DoubleImpl(values, dimensions, names, attributes, false);
        }
        public static RDouble getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static RDouble getNA(boolean named) {
            return named ? NA_NAMED_NA : BOXED_NA;
        }
        public static RDouble exclude(int excludeIndex, RDouble orig) {
            Names names = orig.names();
            if (names == null) {
                return TracingView.ViewTrace.trace(new RDoubleExclusion(excludeIndex, orig));
            }
            int size = orig.size();
            int nsize = size - 1;
            double[] content = new double[nsize];
            for (int i = 0; i < excludeIndex; i++) {
                content[i] = orig.getDouble(i);
            }
            for (int i = excludeIndex; i < nsize; i++) {
                content[i] = orig.getDouble(i + 1);
            }
            return RDoubleFactory.getFor(content, null, names.exclude(excludeIndex));
        }
        public static RDouble subset(RDouble value, RInt index) {
            return TracingView.ViewTrace.trace(new RDoubleSubset(value, index));
        }
    }

    public static class RStringView extends View.RStringProxy<RDouble> implements RString {

        public RStringView(RDouble orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble() {
            return orig;
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
        public RComplex asComplex(ConversionStatus warn) {
            return orig.asComplex();
        }

        @Override
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
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
        public String getString(int i) {
            return Convert.double2string(orig.getDouble(i));
        }
    }

    public static class RComplexView extends View.RComplexProxy<RDouble> implements RComplex {

        public RComplexView(RDouble orig) {
            super(orig);
        }

        @Override
        public RDouble asDouble() {
            return orig;
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
        public RDouble asDouble(ConversionStatus warn) {
            return orig;
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
        public double getReal(int i) {
            double d = orig.getDouble(i);
            if (RDoubleUtils.isNAorNaN(d)) {
                return RDouble.NA;
            } else {
                return d;
            }
        }

        @Override
        public double getImag(int i) {
            double d = orig.getDouble(i);
            if (RDoubleUtils.isNAorNaN(d)) {
                return RDouble.NA;
            } else {
                return 0;
            }
        }
    }

    public static class RIntView extends View.RIntProxy<RDouble> implements RInt {

        public RIntView(RDouble orig) {
            super(orig);
        }

        @Override
        public int getInt(int i) {
            return Convert.double2int(orig.getDouble(i));
        }
    }

    public static class RLogicalView extends View.RLogicalProxy<RDouble> implements RLogical {

        public RLogicalView(RDouble orig) {
            super(orig);
        }

        @Override
        public int getLogical(int i) {
            return Convert.double2logical(orig.getDouble(i));
        }

    }

    public static class RRawView extends View.RRawProxy<RDouble> implements RRaw { // FIXME: remove this? it breaks warnings

        public RRawView(RDouble orig) {
            super(orig);
        }

        @Override
        public byte getRaw(int i) {
            return Convert.double2raw(orig.getDouble(i));
        }
    }

    public static class RDoubleExclusion extends View.RDoubleView implements RDouble {

        final RDouble orig;
        final int excludeIndex;
        final int size;

        public RDoubleExclusion(int excludeIndex, RDouble orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double getDouble(int i) {
            assert Utils.check(i < size, "bounds check");
            assert Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getDouble(i);
            } else {
                return orig.getDouble(i + 1);
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
    public static class RDoubleSubset extends View.RDoubleView implements RDouble {

        final RDouble value;
        final int vsize;
        final RInt index;
        final int isize;

        public RDoubleSubset(RDouble value, RInt index) {
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
        public double getDouble(int i) {
            int j = index.getInt(i);
            assert Utils.check(j > 0);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getDouble(j - 1);
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
