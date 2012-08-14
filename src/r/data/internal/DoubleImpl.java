package r.data.internal;

import r.*;
import r.data.*;

public class DoubleImpl extends ArrayImpl implements RDouble {
    double[] content;

    public DoubleImpl(double[] values) {
        content = new double[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    public Object get(int i) {
        return content[i - 1];
    }

    public double getDouble(int i) {
        return content[i - 1];
    }

    @Override
    public RInt asInt() {
        return new IntView();
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

    class IntView implements RInt {
        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public Object get(int i) {
            return ((Double) DoubleImpl.this.get(i)).doubleValue();
        }

        @Override
        public RArray subset(RAny keys) {
            return materialize();
        }

        @Override
        public RArray subset(RInt index) {
            return materialize();
        }

        @Override
        public RArray subset(RString names) {
            return materialize();
        }

        @Override
        public RInt materialize() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAttributes getAttributes() {
            return DoubleImpl.this.getAttributes();
        }

        @Override
        public String pretty() {
            return materialize().pretty(); // FIXME what a stupid impl ...
        }

    }
}
