package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class IntImpl extends ArrayImpl implements RInt {

    int[] content;

    public IntImpl(int[] values) {
        content = new int[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    @Override
    public int size() {
        return content.length;
    }

    public Object get(int i) {
        return content[i];
    }

    public int getInt(int i) {
        return content[i];
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Integer) val).intValue()); // FIXME better conversion
    }

    @Override
    public RArray set(int i, int val) {
        content[i] = val;
        return this;
    }

    @Override
    public RInt asInt() {
        return this;
    }

    public String pretty() {
        if (content.length == 0) {
            return RInt.TYPE_STRING + "(0)";
        }
        String fst = Convert.int2string(content[0]);
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.int2string(content[i]));
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
        return new DoubleView();
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromInt();
    }

    class DoubleView implements RDouble {

        @Override
        public Object get(int i) {
            int v = IntImpl.this.getInt(i);
            return  v == RInt.NA ? RDouble.NA : v;
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
        public RArray subset(RString names) {
            Utils.nyi();
            return null;
        }

        public int size() {
            return IntImpl.this.size();
        }

        @Override
        public RInt asInt() {
            return IntImpl.this;
        }

        @Override
        public RDouble asDouble() {
            return this;
        }

        @Override
        public RArray materialize() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAttributes getAttributes() {
            return IntImpl.this.getAttributes();
        }

        @Override
        public String pretty() {
            return materialize().pretty();
        }

        @Override
        public RArray set(int i, Object val) {
            return null;
        }

        @Override
        public RLogical asLogical() {
            Utils.nyi();
            return null;
        }

        @Override
        public RArray set(int i, double val) {
            return null;
        }

        @Override
        public double getDouble(int i) {
            int v = IntImpl.this.getInt(i);
            return v == RInt.NA ? RDouble.NA : (double) v;
        }

        @Override
        public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
            Utils.nyi(); // Do we have to bind on the view node or on the implementation
            return null;
        }
    }
}
