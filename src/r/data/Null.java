package r.data;

import r.*;


public final class Null implements RAny, RAttributes {
    private static Null instance = new Null();

    private Null() { }

    @Override
    public RAttributes getAttributes() {
        return this;
    }

    @Override
    public String pretty() {
       return "NULL";
    }

    @Override
    public Object get(int i) {
        return this;
    }

    @Override
    public RArray subset(RAny keys) {
        return this;
    }

    @Override
    public RArray subset(RInt index) {
        return this;
    }

    @Override
    public RArray subset(RString names) {
        return this;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray materialize() {
        return this;
    }

    public static Null getNull() {
        return instance;
    }
}
