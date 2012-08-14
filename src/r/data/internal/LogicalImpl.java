package r.data.internal;

import r.*;
import r.data.*;


public class LogicalImpl extends ArrayImpl implements RLogical {
    int[] content;

    public LogicalImpl(int[] values) {
        content = new int[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    public Object get(int i) {
        return content[i - 1];
    }

    public int getInt(int i) {
        return content[i - 1];
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
    }
}

