package r.data.internal;

import java.util.*;

import r.*;
import r.Convert.*;
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
    public RRaw asRaw() {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical() {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override
    public RComplex asComplex() {
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

    @Override
    public int[] dimensions() {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray setDimensions(int[] dimensions) {
        Utils.nyi();
        return null;
    }

    @Override
    public Names names() {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray setNames(Names names) {
        Utils.nyi();
        return null;
    }

    @Override
    public int index(int i, int j) {
        Utils.nyi();
        return 0;
    }

    @Override
    public void ref() {
    }

    @Override
    public boolean isShared() {
        return true;
    }

    @Override
    public RAttributes stripAttributes() {
        Utils.nyi();
        return null;
    }

    @Override
    public Object getRef(int i) {
        return get(i);
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public String typeOf() {
        Utils.nyi();
        return null;
    }
}
