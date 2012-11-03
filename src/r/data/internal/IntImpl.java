package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class IntImpl extends ArrayImpl implements RInt {

    int[] content;

    public IntImpl(int[] values, boolean doCopy) {
        if (doCopy) {
            content = new int[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
    }

    public IntImpl(int[] values) {
        this(values, true);
    }

    public IntImpl(int size) {
        content = new int[size];
    }

    public IntImpl(RInt v) {
        content = new int[v.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = v.getInt(i);
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
    public int getInt(int i) {
        return content[i];
    }

    @Override
    public RAny boxedGet(int i) {
        return RIntFactory.getScalar(getInt(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return content[i] == RInt.NA;
    }

    @Override
    public RArray set(int i, Object val) {
        return set(i, ((Integer) val).intValue()); // FIXME better conversion
    }

    @Override
    public RInt set(int i, int val) {
        content[i] = val;
        return this;
    }

    @Override
    public RInt asInt() {
        return this;
    }

    @Override
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
        return new RInt.RLogicalView(this);
    }

    @Override
    public RDouble asDouble() {
        return new RInt.RDoubleView(this);
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromInt();
    }

    @Override
    public RArray subset(RInt index) {
        return RInt.RIntFactory.subset(this, index);
    }

    public static class RIntSequence extends View.RIntView implements RInt {
        // note: the sequence can go from large values to smaller values
        final int from;
        final int to;
        final int step;

        final int size;

        public RIntSequence(int from, int to, int step) {
            this.from = from;
            this.to = to;
            this.step = step;

            int absstep = (step > 0) ? step : -step;
            if (from <= to) {
                size = (to - from + 1) / absstep;
            } else {
                size = (from - to + 1) / absstep;
            }
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public int getInt(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");
            return from + i * step;
        }

        public boolean isPositive() {
            return from > 0 && to > 0;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public int step() {
            return step;
        }

        public int min() {
            return (from < to) ? from : to;
        }

        public int max() {
            return (to > from) ? to : from;
        }
    }
}
