package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;

public class ScalarComplexImpl extends ArrayImpl implements RComplex {
    double real;
    double imag;

    @Override
    public double[] getContent() {
        return new double[] {real, imag};
    }

    public ScalarComplexImpl(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Object get(int i) {
        assert Utils.check(i == 0);
        return  get();
    }

    public Object get() {
        return new RComplex.Complex(real, imag);
    }

    @Override
    public RAny boxedGet(int i) {
        assert Utils.check(i == 0);
        return boxedGet();
    }

    public RAny boxedGet() {
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        assert Utils.check(i == 0);
        return set(val);
    }

    public RArray set(Object val) {
        Complex c = (Complex) val;
        return set(c.realValue(), c.imagValue());
    }

    @Override
    public RComplex setDimensions(int[] dimensions) {
        return RComplex.RComplexFactory.getFor(new double[] {real, imag}, dimensions, null);
    }

    @Override
    public RComplex setNames(Names names) {
        return RComplex.RComplexFactory.getFor(new double[] {real, imag}, null, names);
    }

    @Override
    public RComplex setAttributes(Attributes attributes) {
        return RComplex.RComplexFactory.getFor(new double[] {real, imag}, null, null, attributes);
    }


    @Override
    public boolean isNAorNaN(int i) {
        assert Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return RComplex.RComplexUtils.eitherIsNAorNaN(real, imag);
    }

    public boolean isNA() {
        return RComplex.RComplexUtils.eitherIsNA(real, imag);
    }

    @Override
    public String pretty() {
        return Convert.prettyNA(Convert.complex2string(real, imag));
    }

    @Override
    public RRaw asRaw() {
        return asRaw(null);
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RRaw.RRawFactory.getScalar(Convert.complex2raw(real, imag, warn));
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.complex2logical(real, imag));
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return asInt(null);
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return RInt.RIntFactory.getScalar(Convert.complex2int(real, imag, warn));
    }

    @Override
    public RDouble asDouble() {
        return asDouble(null);
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return RDouble.RDoubleFactory.getScalar(Convert.complex2double(real, imag, warn));
    }

    @Override
    public RComplex asComplex() {
        return this;
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return this;
    }

    @Override
    public RString asString() {
        return RString.RStringFactory.getScalar(Convert.complex2string(real, imag));
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
    }

    @Override
    public ScalarComplexImpl materialize() {
        return this;
    }

    @Override
    public RComplex set(int i, double real, double imag) {
        assert Utils.check(i == 0);
        return set(real, imag);
    }

    public RComplex set(double real, double imag) {
        this.real = real;
        this.imag = imag;
        return this;
    }

    @Override
    public double getReal(int i) {
        assert Utils.check(i == 0);
        return getReal();
    }

    public double getReal() {
        return real;
    }

    @Override
    public double getImag(int i) {
        assert Utils.check(i == 0);
        return getImag();
    }

    public double getImag() {
        return imag;
    }

    @Override
    public RComplex subset(final RInt index) {
        final int size = index.size();
        if (size == 1) {
            int i = index.getInt(0);
            if (i > 1) {
                return RComplex.BOXED_NA;
            } else {
                return this;
            }
        }
        final double rvalue = real;
        final double ivalue = imag;
        return TracingView.ViewTrace.trace(new View.RComplexView() {

            @Override
            public double getReal(int i) {
                int j = index.getInt(i);
                if (j > 1) {
                    return RDouble.NA;
                } else {
                    return rvalue;
                }
            }

            @Override
            public double getImag(int i) {
                int j = index.getInt(i);
                if (j > 1) {
                    return RDouble.NA;
                } else {
                    return ivalue;
                }
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean isSharedReal() {
                return index.isShared();
            }

            @Override
            public void ref() {
                index.ref();
            }

            @Override
            public boolean dependsOn(RAny v) {
                return index.dependsOn(v);
            }

        });
    }

    @Override
    public String typeOf() {
        return RComplex.TYPE_STRING;
    }
}
