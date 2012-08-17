package r.data.internal;

import r.*;
import r.data.*;

public class LogicalImpl extends ArrayImpl implements RLogical {

    int[] content;

    public LogicalImpl(int size) {
        content = new int[size];
    }

    public LogicalImpl(int[] values) {
        content = new int[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
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
    public int getLogical(int i) {
        return content[i];
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Integer) val).intValue()); // FIXME better conversion
    }

    @Override
    public RLogical set(int i, int val) {
        content[i] = val;
        return this;
    }

    @Override
    public RLogical asLogical() {
        return this;
    }

    @Override
    public RInt asInt() {
        return new IntView();
    }

    public String pretty() {
        if (content.length == 0) {
            return RLogical.TYPE_STRING + "(0)";
        }
        String fst = Convert.logical2string(content[0]);
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.logical2string(content[i]));
        }
        return str.toString();
    }

    class IntView implements RInt {

        @Override
        public Object get(int i) {
            return LogicalImpl.this.get(i);
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
            // TODO Auto-generated method stub
            return null;
        }

        public int size() {
            return LogicalImpl.this.size();
        }

        @Override
        public RInt asInt() {
            return this;
        }

        @Override
        public RArray materialize() {
            Utils.nyi();
            return null;
        }

        @Override
        public RAttributes getAttributes() {
            return LogicalImpl.this.getAttributes();
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
            return LogicalImpl.this;
        }

        @Override
        public RArray set(int i, int val) {
            return null;
        }
    }
}
