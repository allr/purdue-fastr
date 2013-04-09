package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;


public final class ScalarLogicalImpl extends ArrayImpl implements RLogical {

    int value;

    public ScalarLogicalImpl(int value) {
        this.value = value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Object get(int i) {
        Utils.check(i == 0);
        return get();
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
        return set(((Integer) val).intValue());
    }

    @Override
    public RLogical setDimensions(int[] dimensions) {
        return RLogical.RLogicalFactory.getFor(new int[] {value}, dimensions, null);
    }

    @Override
    public RLogical setNames(Names names) {
        return RLogical.RLogicalFactory.getFor(new int[] {value}, null, names);
    }

    @Override
    public RLogical setAttributes(Attributes attributes) {
        return RLogical.RLogicalFactory.getFor(new int[] {value}, null, null, attributes);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return value == RLogical.NA;
    }

    @Override
    public ScalarLogicalImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        return Convert.prettyNA(Convert.logical2string(value));
    }

    @Override
    public RRaw asRaw() {
        return RRaw.RRawFactory.getScalar(Convert.logical2raw(value));
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RRaw.RRawFactory.getScalar(Convert.logical2raw(value, warn));
    }

    @Override
    public RLogical asLogical() {
        return this;
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return this;
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getScalar(Convert.logical2int(value));
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return asInt();
    }

    @Override
    public RDouble asDouble() {
        return RDouble.RDoubleFactory.getScalar(Convert.logical2double(value));
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return asDouble();
    }

    @Override
    public RComplex asComplex() {
        return RComplex.RComplexFactory.getScalar(Convert.logical2double(value), 0);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return RString.RStringFactory.getScalar(Convert.logical2string(value));
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
    }

    @Override
    public int getLogical(int il) {
        Utils.check(il == 0);
        return getLogical();
    }

    public int getLogical() {
        return value;
    }

    @Override
    public RLogical set(int i, int val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RLogical set(int val) {
        value = val;
        return this;
    }

    @Override
    public RLogical subset(final RInt index) {
        final int size = index.size();
        if (size == 1) {
            int i = index.getInt(0);
            if (i > 1) {
                return RLogical.BOXED_NA;
            } else {
                return this;
            }
        }
        final int lvalue = value;
        return new View.RLogicalView() {

            @Override
            public int getLogical(int i) {
                int j = index.getInt(i);
                if (j > 1) {
                    return RLogical.NA;
                } else {
                    return lvalue;
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
        return RLogical.TYPE_STRING;
    }
}
