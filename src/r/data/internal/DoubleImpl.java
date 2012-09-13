package r.data.internal;

import r.*;
import r.data.*;

public class DoubleImpl extends ArrayImpl implements RDouble {

    double[] content;

    public DoubleImpl(double[] values) {
        content = new double[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    public DoubleImpl(int size) {
        content = new double[size];
    }

    public DoubleImpl(RDouble d) {
        content = new double[d.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = d.getDouble(i);
        }
    }

    @Override
    public int size() {
        return content.length;
    }

    public Object get(int i) {
        return content[i];
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Double) val).doubleValue()); // FIXME better conversion
    }

    @Override
    public RArray set(int i, double val) {
        content[i] = val;
        return this;
    }

    public double getDouble(int i) {
        return content[i];
    }

    @Override
    public RInt asInt() {
        return new IntView();
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    public String pretty() {
        if (content.length == 0) {
            return RDouble.TYPE_STRING + "(0)";
        }
        String fst = Convert.double2string(content[0]);
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.double2string(content[i]));
        }
        return str.toString();
    }

    class IntView extends View implements RInt {

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public Object get(int i) {
            return getInt(i);
        }

        @Override
        public RArray set(int i, int val) {
            return materialize().set(i, val);
        }

        public int size() {
            return DoubleImpl.this.size();
        }

        @Override
        public RInt materialize() {
            return RInt.RIntFactory.copy(this);
        }

        @Override
        public RAttributes getAttributes() {
            return DoubleImpl.this.getAttributes();
        }

        @Override
        public RLogical asLogical() {
            return DoubleImpl.this.asLogical();
        }

        @Override
        public RDouble asDouble() {
            return DoubleImpl.this;
        }

        @Override
        public int getInt(int i) {
            return Convert.double2int(DoubleImpl.this.getDouble(i));
        }
    }

    @Override
    public RLogical asLogical() {
        Utils.nyi();
        return null;
    }
}
