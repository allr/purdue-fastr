package r.data.internal;

import r.*;
import r.Convert.NAIntroduced;
import r.data.*;

public class DoubleImpl extends NonScalarArrayImpl implements RDouble {

    final double[] content;

    public DoubleImpl(double[] values, int[] dimensions, boolean doCopy) {
        if (doCopy) {
            content = new double[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
    }
    public DoubleImpl(double[] values, int[] dimensions) {
        this(values, dimensions, true);
    }

    public DoubleImpl(double[] values) {
        this(values, null, true);
    }

    public DoubleImpl(int size) {
        content = new double[size];
    }

    public DoubleImpl(RDouble d) {
        content = new double[d.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = d.getDouble(i);
        }
        dimensions = d.dimensions();
    }

    @Override
    public DoubleImpl stripAttributes() {
        if (dimensions == null) {
            return this;
        }
        if (!isShared()) {
            dimensions = null;
            return this;
        }
        DoubleImpl v = new DoubleImpl(content, null, false); // note: re-uses current values
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
    public RAny boxedGet(int i) {
        return RDoubleFactory.getScalar(getDouble(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return RDouble.RDoubleUtils.isNAorNaN(content[i]);
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Double) val).doubleValue()); // FIXME better conversion
    }

    @Override
    public RDouble set(int i, double val) {
        content[i] = val;
        return this;
    }

    @Override
    public double getDouble(int i) {
        return content[i];
    }

    @Override
    public RInt asInt() {
        return new RDouble.RIntView(this);
    }

    @Override
    public RInt asInt(NAIntroduced naIntroduced) {
        return RDouble.RDoubleUtils.double2int(this, naIntroduced);
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    @Override
    public RDouble asDouble(NAIntroduced naIntroduced) {
        return this;
    }

    @Override
    public DoubleImpl materialize() {
        return this;
    }

    @Override
    public String pretty() {
        if (dimensions != null) {
            return matrixPretty();
        }
        if (content.length == 0) {
//            return RDouble.TYPE_STRING + "(0)";
            return "numeric(0)";  // FIXME: I think there is an inconsistency in GNU-R itself on this
        }
        String fst = Convert.pretty(Convert.double2string(content[0]));
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.pretty(Convert.double2string(content[i])));
        }
        return str.toString();
    }

    @Override
    public RLogical asLogical() {
        return new RDouble.RLogicalView(this);
    }

    @Override
    public RLogical asLogical(NAIntroduced naIntroduced) {
        return asLogical();
    }

    @Override
    public RString asString() {
        return new RDouble.RStringView(this);
    }

    @Override
    public RString asString(NAIntroduced naIntroduced) {
        return asString();
    }

    @Override
    public RArray subset(RInt index) {
        return RDouble.RDoubleFactory.subset(this, index);
    }

    @Override
    public String typeOf() {
        return RDouble.TYPE_STRING;
    }
}
