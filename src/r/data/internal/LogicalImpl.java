package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class LogicalImpl extends ArrayImpl implements RLogical {

    int[] content;

    public LogicalImpl(int size) {
        content = new int[size];
    }

    public LogicalImpl(int[] values) {
        content = new int[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    public LogicalImpl(RLogical l) {
        content = new int[l.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = l.getLogical(i);
        }
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
        return RInt.RIntFactory.getForArray(content);
    }

    @Override
    public RDouble asDouble() {
        return new RLogical.RDoubleView(this);
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

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromLogical();
    }
}
