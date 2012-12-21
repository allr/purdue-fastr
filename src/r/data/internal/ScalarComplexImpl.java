package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;

public class ScalarComplexImpl extends ArrayImpl implements RComplex {
    double real;
    double imag;

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
        Utils.check(i == 0);
        return  get();
    }

    public Object get() {
        return new RComplex.Complex(real, imag);
    }

    @Override
    public RAny boxedGet(int i) {
        Utils.check(i == 0);
        return boxedGet();
    }

    public RAny boxedGet() {
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RArray set(Object val) {
        Complex c = (Complex) val;
        return set(c.realValue(), c.imagValue());
    }

    @Override
    public RComplex setDimensions(int[] dimensions) {
        return RComplex.RComplexFactory.getFor(new double[] {real, imag}, dimensions);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
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
        Utils.check(i == 0);
        return set(real, imag);
    }

    public RComplex set(double real, double imag) {
        this.real = real;
        this.imag = imag;
        return this;
    }

    @Override
    public double getReal(int i) {
        Utils.check(i == 0);
        return getReal();
    }

    public double getReal() {
        return real;
    }

    @Override
    public double getImag(int i) {
        Utils.check(i == 0);
        return getImag();
    }

    public double getImag() {
        return imag;
    }

    @Override
    public String typeOf() {
        return RComplex.TYPE_STRING;
    }
}
