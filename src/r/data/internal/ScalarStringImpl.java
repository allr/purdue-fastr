package r.data.internal;

import r.*;
import r.data.*;


public class ScalarStringImpl extends ArrayImpl implements RString {

    String value;

    public ScalarStringImpl(String value) {
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
        return set((String) val);
    }

    @Override
    public RString setDimensions(int[] dimensions) {
        return RString.RStringFactory.getFor(new String[] {value}, dimensions);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return value == RString.NA;
    }

    @Override
    public ScalarStringImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        if (value != RString.NA) {
            return "\"" + value + "\"";
        } else {
            return "NA";
        }
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.string2logical(value));
    }

    @Override
    public RInt asInt() {
        return  RInt.RIntFactory.getScalar(Convert.string2int(value));
    }

    @Override
    public RDouble asDouble() {
        return RDouble.RDoubleFactory.getScalar(Convert.string2double(value));
    }

    @Override
    public RString asString() {
        return this;
    }

    @Override
    public String getString(int i) {
        Utils.check(i == 0);
        return getString();
    }

    public String getString() {
        return value;
    }

    @Override
    public RString set(int i, String val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RString set(String val) {
        value = val;
        return this;
    }
}
