package r.data.internal;

import r.*;
import r.data.*;


public class ScalarLogicalImpl extends ArrayImpl implements RLogical {

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
        return Convert.logical2string(value);
    }

    @Override
    public RLogical asLogical() {
        return this;
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getScalar(Convert.logical2int(value));
    }

    @Override
    public RDouble asDouble() {
        return RDouble.RDoubleFactory.getScalar(Convert.logical2double(value));
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
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
    public boolean isShared() {
        return false;
    }
}
