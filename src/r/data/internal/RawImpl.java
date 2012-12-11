package r.data.internal;

import r.*;
import r.Convert.*;
import r.data.*;


public class RawImpl extends NonScalarArrayImpl implements RRaw {

    final byte[] content;

    public RawImpl(int size) {
        content = new byte[size];
    }

    public RawImpl(byte[] values, int[] dimensions, boolean doCopy) {
        if (doCopy) {
            content = new byte[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
    }

    public RawImpl(byte[] values, int[] dimensions) {
        this(values, dimensions, true);
    }

    public RawImpl(byte[] values) {
        this(values, null, true);
    }

    public RawImpl(RRaw r) {
        content = new byte[r.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = r.getRaw(i);
        }
        dimensions = r.dimensions();
    }

    @Override
    public RawImpl stripAttributes() {
        if (dimensions == null) {
            return this;
        }
        if (!isShared()) {
            dimensions = null;
            return this;
        }
        RawImpl v = new RawImpl(content, null, false); // note: re-uses current values
        v.refcount = refcount; // mark the new vector shared
        return v;
    }

    @Override
    public int size() {
        return content.length;
    }

    @Override
    public Object get(int i) {
        return content[i];
    }

    @Override
    public byte getRaw(int i) {
        return content[i];
    }

    @Override
    public RAny boxedGet(int i) {
        return RRawFactory.getScalar(getRaw(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return false;
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Byte) val).byteValue());
    }

    @Override
    public RRaw set(int i, byte val) {
        content[i] = val;
        return this;
    }

    @Override
    public RRaw asRaw() {
        return this;
    }

    @Override
    public RRaw asRaw(NAIntroduced naIntroduced, OutOfRange outOfRange) {
        return this;
    }

    @Override
    public RLogical asLogical() {
        return new RRaw.RLogicalView(this);
    }

    @Override
    public RLogical asLogical(NAIntroduced naIntroduced) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return new RRaw.RIntView(this);
    }

    @Override
    public RInt asInt(NAIntroduced naIntroduced) {
        return asInt();
    }

    @Override
    public RDouble asDouble() {
        return new RRaw.RDoubleView(this);
    }

    @Override
    public RDouble asDouble(NAIntroduced naIntroduced) {
        return asDouble();
    }

    @Override
    public RString asString() {
        return new RRaw.RStringView(this);
    }

    @Override
    public RString asString(NAIntroduced naIntroduced) {
        return asString();
    }

    @Override
    public RawImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        if (dimensions != null) {
            return matrixPretty();
        }
        if (content.length == 0) {
            return RRaw.TYPE_STRING + "(0)";
        }
        String fst = Convert.pretty(Convert.raw2string(content[0]));
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.pretty(Convert.raw2string(content[i])));
        }
        return str.toString();
    }

    @Override
    public String typeOf() {
        return RRaw.TYPE_STRING;
    }

    @Override
    public RArray subset(RInt index) {
        return RRaw.RRawFactory.subset(this, index);
    }

}
