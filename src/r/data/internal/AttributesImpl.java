package r.data.internal;

import java.util.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;


public class AttributesImpl extends BaseObject implements RAttributes {
    public final String[] specialAttributes = new String[]{"name", "class"};

    RAny[] specialSlots = new RAny[specialAttributes.length];
    Map<RSymbol, RAny> content;

    public RArray subset(RString keys) {
        Utils.nyi();
        return null;
    }

    public RArray materialize() {
        // TODO maybe it's time to create a LIST
        return this;
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
    public String pretty() {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }
}
