package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;


public abstract class ArrayImpl extends BaseObject implements RArray {
    @Override
    public RArray subset(RAny keys) {
        if (keys instanceof RInt) {
            return subset((RInt) keys);
        } else if (keys instanceof RDouble) {
            return subset(((RDouble) keys).asInt());
        } else if (keys instanceof RString) {
            return subset((RString) keys);
        } else {
            Utils.nyi();
        }

        return null;
    }

    @Override
    public RArray subset(RString names) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray materialize() {
        return this;
    }

    @Override
    public int[] dimensions() {
        return null;
    }

    @Override
    public RArray setDimensions(int[] dimensions) {
        Utils.nyi("not supported");
        return null;
    }

    @Override
    public Names names() {
        return null;
    }

    @Override
    public RArray setNames(Names names) {
        Utils.nyi("not supported");
        return null;
    }

    @Override
    public Attributes attributes() {
        return null;
    }

    @Override
    public RArray setAttributes(Attributes attributes) {
        Utils.nyi("not supported");
        return null;
    }

    @Override
    public int index(int i, int j) { // i-th row, j-th column indexed from 1
        int[] dims = dimensions();
        return i + (j - 1) * dims[1];
    }

    @Override
    public void ref() {
    }

    @Override
    public boolean isShared() {
        return true;
    }

    protected RArray doStrip() {
        Utils.nyi();
        return null;
    }

    @Override
    public RArray stripAttributes() {
        if (dimensions() == null && names() == null) {
            return this;
        }
        if (!isShared()) {
            setDimensions(null);
            setNames(null);
            return this;
        }
        return doStrip();
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
    public RList asList() {
        return new RArray.RListView(this);
    }

    protected String namedPretty() {
        RArray.Names aNames = names();
        Utils.check(aNames != null);
        String[] names = aNames.asStringArray();
        int size = size();
        StringBuilder headers = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (int i = 0; i < size; i++) {
            String header = Convert.prettyGTNALT(names[i]);
            String value = boxedGet(i).prettyMatrixElement();
            int hlen = header.length();
            int vlen = value.length();
            int len = Math.max(hlen, vlen);
            if (i > 0) {
                len++;
            }
            Utils.strAppend(headers, header, len);
            Utils.strAppend(values, value, len);
        }
        return headers.append("\n").append(values).toString();
    }

    public RAny boxedNamedGet(int i) { // FIXME: this includes unnecessary allocation (in boxedGet) later re-done in setNames
        RAny res = boxedGet(i);
        Names names = names();
        if (names == null || !(res instanceof RArray)) { // FIXME: isn't res always an instance of RArray ?
            return res;
        }
        RSymbol s = names.sequence()[i];
        return ((RArray) res).setNames(Names.create(new RSymbol[] {s}));
     }
}
