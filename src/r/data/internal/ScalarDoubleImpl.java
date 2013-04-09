package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;


public final class ScalarDoubleImpl extends ArrayImpl implements RDouble {

    double value;

    @Override
    public double[] getContent() {
        return new double[] {value};
    }

    public ScalarDoubleImpl(double value) {
        this.value = value;
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
        return value;
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
        return set(((Double) val).doubleValue());
    }

    @Override
    public RDouble setDimensions(int[] dimensions) {
        return RDouble.RDoubleFactory.getFor(new double[] {value}, dimensions, null);
    }

    @Override
    public RDouble setNames(Names names) {
        return RDouble.RDoubleFactory.getFor(new double[] {value}, null, names);
    }

    @Override
    public RDouble setAttributes(Attributes attributes) {
        return RDouble.RDoubleFactory.getFor(new double[] {value}, null, null, attributes);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return RDouble.RDoubleUtils.isNAorNaN(value);
    }

    public boolean isNA() {
        return RDouble.RDoubleUtils.isNA(value);
    }

    @Override
    public String pretty() {
        return Convert.prettyNA(Convert.double2string(value));
    }

    @Override
    public RRaw asRaw() {
        return RRaw.RRawFactory.getScalar(Convert.double2raw(value));
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RRaw.RRawFactory.getScalar(Convert.double2raw(value, warn));
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.double2logical(value));
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getScalar(Convert.double2int(value));
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return RInt.RIntFactory.getScalar(Convert.double2int(value, warn));
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return this;
    }

    @Override
    public RComplex asComplex() {
        return RComplex.RComplexFactory.getScalar(value, 0);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return RString.RStringFactory.getScalar(Convert.double2string(value));
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
    }

    @Override
    public ScalarDoubleImpl materialize() {
        return this;
    }

    @Override
    public RDouble set(int i, double val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RDouble set(double val) {
        value = val;
        return this;
    }

    @Override
    public double getDouble(int i) {
        Utils.check(i == 0);
        return getDouble();
    }

    public double getDouble() {
        return value;
    }

    @Override
    public RDouble subset(final RInt index) {
        final int size = index.size();
        if (size == 1) {
            int i = index.getInt(0);
            if (i > 1) {
                return RDouble.BOXED_NA;
            } else {
                return this;
            }
        }
        final double dvalue = value;
        return new View.RDoubleView() {

            @Override
            public double getDouble(int i) {
                int j = index.getInt(i);
                if (j > 1) {
                    return RDouble.NA;
                } else {
                    return dvalue;
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

        };
    }

    @Override
    public String typeOf() {
        return RDouble.TYPE_STRING;
    }
}
