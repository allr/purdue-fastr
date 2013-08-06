package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public class IntImpl extends NonScalarArrayImpl implements RInt {

    final int[] content;

    @Override
    public int[] getContent() {
        return content;
    }

    public IntImpl(int[] values, int[] dimensions, Names names, Attributes attributes, boolean doCopy) {
        if (doCopy) {
            content = new int[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
        this.names = names;
        this.attributes = attributes;
    }

    public IntImpl(int[] values, int[] dimensions, Names names) {
        this(values, dimensions, names, null, true);
    }

    public IntImpl(int[] values) {
        this(values, null, null, null, true);
    }

    public IntImpl(int size) {
        content = new int[size];
    }

    public IntImpl(RInt v, boolean valuesOnly) {
        content = new int[v.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = v.getInt(i);
        }
        if (!valuesOnly) {
            dimensions = v.dimensions();
            names = v.names();
            attributes = v.attributes();
        }
    }

    public IntImpl(RInt v, int[] dimensions, Names names, Attributes attributes) {
        content = new int[v.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = v.getInt(i);
        }
        this.dimensions = dimensions;
        this.names = names;
        this.attributes = attributes;
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
    public IntImpl materialize() {
        return this;
    }

    private static final String EMPTY_STRING = RInt.TYPE_STRING + "(0)";
    private static final String NAMED_EMPTY_STRING = "named " + EMPTY_STRING;

    @Override
    public String pretty() {
        StringBuilder str = new StringBuilder();
        if (dimensions != null) {
            str.append(arrayPretty());
        } else if (content.length == 0) {
            str.append((names() == null) ? EMPTY_STRING : NAMED_EMPTY_STRING);
        } else if (names() != null) {
            str.append(namedPretty());
        } else {
            str.append(Convert.prettyNA(Convert.int2string(content[0])));
            for (int i = 1; i < content.length; i++) {
                str.append(", ");
                str.append(Convert.prettyNA(Convert.int2string(content[i])));
            }
        }
        str.append(attributesPretty());
        return str.toString();
    }

    @Override
    public RRaw asRaw() {
        return new RInt.RRawView(this);
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RInt.RIntUtils.intToRaw(this, warn);
    }

    @Override
    public RLogical asLogical() {
        return new RInt.RLogicalView(this);
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return this;
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return this;
    }

    @Override
    public RDouble asDouble() {
        return new RInt.RDoubleView(this);
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return asDouble();
    }

    @Override
    public RComplex asComplex() {
        return new RInt.RComplexView(this);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return new RInt.RStringView(this);
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
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
            assert Utils.check(i < size, "bounds check");
            assert Utils.check(i >= 0, "bounds check");
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

        @Override
        public boolean isSharedReal() { // no state, so not shared
            return false;
        }

        @Override
        public boolean dependsOn(RAny value) {
            return false;
        }
    }

    @Override
    public String typeOf() {
        return RInt.TYPE_STRING;
    }

    @Override
    public IntImpl doStrip() {
        return new IntImpl(content, null, null, null, false);
    }

    @Override
    public IntImpl doStripKeepNames() {
        return new IntImpl(content, null, names, null, false);
    }
}
