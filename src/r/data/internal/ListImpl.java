package r.data.internal;

import r.*;
import r.data.*;

public class ListImpl extends NonScalarArrayImpl implements RList {

    final RAny[] content;

    public ListImpl(RAny[] values, int[] dimensions, boolean doCopy) {
        if (doCopy) {
            content = new RAny[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
    }

    public ListImpl(RAny[] values) {
        this(values, null, true);
    }

    public ListImpl(RAny[] values, int[] dimensions) {
        this(values, dimensions, true);
    }

    public ListImpl(int size) {
        content = new RAny[size];
    }

    public ListImpl(RList v) { // deep-copy
                               // FIXME: why deep? do all callers need it deep?
        content = new RAny[v.size()];
        for (int i = 0; i < content.length; i++) {
            RAny e = v.getRAny(i);
            content[i] = Utils.copy(e);
        }
        dimensions = v.dimensions();
    }

    @Override
    public ListImpl stripAttributes() {
        if (dimensions == null) {
            return this;
        }
        if (!isShared()) {
            dimensions = null;
            return this;
        }
        ListImpl v = new ListImpl(content, null, false); // note: re-uses current values
        v.refcount = refcount; // mark the new integer shared
        return v;
    }

    @Override
    public ListImpl asList() {
        return this;
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
    public Object getRef(int i) {
        RAny v = content[i];
        v.ref();
        return v;
    }

    @Override
    public RAny boxedGet(int i) {
        return RListFactory.getScalar(getRAny(i));
    }

    @Override
    public RAny getRAny(int i) {
        return content[i];
    }

    @Override
    public RAny getRAnyRef(int i) {
        RAny v = content[i];
        v.ref();
        return v;
    }

    @Override
    public boolean isNAorNaN(int i) {
        RAny v = content[i];
        if (v instanceof RArray) {
            RArray a = (RArray) v;
            if (a.size() == 1) {
                return a.isNAorNaN(0);
            }
        }
        return false;
    }

    @Override
    public RArray set(int i, Object val) {
        content[i] = (RAny) val;
        return this;
    }

    @Override
    public RArray set(int i, RAny val) {
        content[i] = val;
        return this;
    }

    private static final StringBuilder emptyString = new StringBuilder();

    @Override
    public String pretty() {
        return pretty(emptyString);
    }

    @Override
    public String prettyMatrixElement() { // only called on scalar (boxed) lists
        Utils.check(content.length == 1);
        RAny v = content[0];
        if (!(v instanceof RArray)) {
            Utils.nyi("unsupported type");
        }
        RArray a = (RArray) v;
        int asize = a.size();

        if (asize == 1) {
            if (a instanceof RList) {
                return "List,1";
            }
            return a.prettyMatrixElement();
        }

        String base;
        if (a instanceof RDouble) {
            base = "Numeric"; // FIXME: are these names more general in R?
        } else if (a instanceof RInt) {
            base = "Integer";
        } else if (a instanceof RLogical) {
            base = "Logical";
        } else if (a instanceof RString) {
            base = "Character";
        } else {
            Utils.nyi("unsupported type");
            base = null;
        }
        return base + "," + Integer.toString(asize);
    }

    public String pretty(StringBuilder indexPrefix) {
        if (dimensions != null) {
            return matrixPretty();
        }
        if (content.length == 0) {
            return RList.TYPE_STRING + "()";
        } else {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                if (i >= 1) {
                    str.append("\n\n");
                }
                StringBuilder nprefix = new StringBuilder();
                nprefix.append(indexPrefix);
                nprefix.append("[[");
                nprefix.append(i + 1);
                nprefix.append("]]");
                str.append(nprefix);
                str.append("\n");

                RAny v = content[i];
                if (v instanceof ListImpl) {
                    str.append(((ListImpl) v).pretty(nprefix));
                } else {
                    str.append(v.pretty());
                }
            }
            return str.toString();
        }
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
    public RArray subset(RInt index) {
        return RList.RListFactory.subset(this, index);
    }
}
