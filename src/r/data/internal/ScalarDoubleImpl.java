package r.data.internal;

import r.*;
import r.data.*;


public final class ScalarDoubleImpl extends ArrayImpl implements RDouble {

    double value;

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
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return RDouble.RDoubleUtils.isNAorNaN(value);
    }

    @Override
    public String pretty() {
        return Convert.double2string(value);
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.double2logical(value));
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getScalar(Convert.double2int(value));
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
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
}
