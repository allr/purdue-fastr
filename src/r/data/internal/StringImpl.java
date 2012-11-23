package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class StringImpl extends NonScalarArrayImpl implements RString {

    final String[] content;

    public StringImpl(String[] values, int[] dimensions, boolean doCopy) {
        if (doCopy) {
            content = new String[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
    }

    public StringImpl(String[] values, int[] dimensions) {
        this(values, dimensions, true);
    }

    public StringImpl(String[] values) {
        this(values, null, true);
    }

    public StringImpl(int size) {
        content = new String[size];
    }

    public StringImpl(RString v, boolean valuesOnly) {
        content = new String[v.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = v.getString(i);
        }
        if (!valuesOnly) {
            dimensions = v.dimensions();
        }
    }

    @Override
    public StringImpl stripAttributes() {
        if (dimensions == null) {
            return this;
        }
        if (!isShared()) {
            dimensions = null;
            return this;
        }
        StringImpl v = new StringImpl(content, null, false); // note: re-uses current values
        v.refcount = refcount; // mark the new integer shared
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
    public String getString(int i) {
        return content[i];
    }

    @Override
    public RAny boxedGet(int i) {
        return RString.RStringFactory.getScalar(getString(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return content[i] == RString.NA;
    }

    @Override
    public StringImpl set(int i, String val) {
        content[i] = val;
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        content[i] = (String) val;
        return this;
    }

    @Override
    public RString asString() {
        return this;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public StringImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        if (dimensions != null) {
            return matrixPretty();
        }
        if (content.length == 0) {
            return RString.TYPE_STRING + "(0)";
        }
        StringBuilder str = new StringBuilder();
        str.append("\"");
        str.append(content[0]);
        str.append("\"");
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append("\"");
            str.append(content[i]);
            str.append("\"");
        }
        return str.toString();
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
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi();  // FIXME: is callNodeFactory still used?
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        return RString.RStringFactory.subset(this, index);
    }
}
