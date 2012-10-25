package r.data.internal;

import java.util.*;

import r.*;
import r.data.*;

public class AttributesImpl extends BaseObject implements RAttributes {

    public final String[] specialAttributes = new String[]{"name", "class"};

    RAny[] specialSlots = new RAny[specialAttributes.length];
    Map<RSymbol, RAny> content;

    @Override
    public int size() {
        int size = content.size();
        for (RAny obj : specialSlots) {
            if (obj != null) {
                    size++;
            }
        }
        return size;
    }

    public RArray subset(RString keys) {
        Utils.nyi();
        return null;
    }

    public RArray materialize() {
        // TODO maybe it's time to create a LIST
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray subset(RAny keys) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        Utils.nyi();
        return null;
    }

    @Override
    public Object get(int i) {
        if (i < specialAttributes.length) {
            return specialSlots[i];
        }
        Utils.nyi();
        return null;
    }

    @Override
    public RAny boxedGet(int i) {
        return (RAny) get(i);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.nyi();
        return false;
    }

    @Override
    public String pretty() {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical() {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override
    public RList asList() {
        Utils.nyi();
        return null;
    }
}
