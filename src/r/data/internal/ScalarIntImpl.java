package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;


public final class ScalarIntImpl extends ArrayImpl implements RInt {

    int value;

    public ScalarIntImpl(int value) {
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
    public RInt setDimensions(int[] dimensions) {
        return RInt.RIntFactory.getFor(new int[] {value}, dimensions, null);
    }

    @Override
    public RInt setNames(Names names) {
        return RInt.RIntFactory.getFor(new int[] {value}, null, names);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return value == RInt.NA;
    }

    @Override
    public ScalarIntImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        return Convert.prettyNA(Convert.int2string(value));
    }

    @Override
    public RRaw asRaw() {
        return RRaw.RRawFactory.getScalar(Convert.int2raw(value));
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RRaw.RRawFactory.getScalar(Convert.int2raw(value, warn));
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.int2logical(value));
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return this;
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return this;
    }

    @Override
    public RDouble asDouble() {
        return RDouble.RDoubleFactory.getScalar(Convert.int2double(value));
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return asDouble();
    }

    @Override
    public RComplex asComplex() {
        return RComplex.RComplexFactory.getScalar(Convert.int2double(value), 0);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return RString.RStringFactory.getScalar(Convert.int2string(value));
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
    }

    @Override
    public int getInt(int i) {
        Utils.check(i == 0);
        return getInt();
    }

    public int getInt() {
        return value;
    }

    @Override
    public RInt set(int i, int val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RInt set(int val) {
        value = val;
        return this;
    }

    @Override
    public String typeOf() {
        return RInt.TYPE_STRING;
    }
}
