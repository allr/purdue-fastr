package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

public class LogicalImpl extends NonScalarArrayImpl implements RLogical {

    final int[] content;


    public int[] getContent() {
        return content;
    }

    public LogicalImpl(int size) {
        content = new int[size];
    }

    public LogicalImpl(int[] values, int[] dimensions, Names names, Attributes attributes, boolean doCopy) {
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

    public LogicalImpl(int[] values, int[] dimensions) {
        this(values, dimensions, null, null, true);
    }

    public LogicalImpl(int[] values) {
        this(values, null, null, null, true);
    }

    public LogicalImpl(RLogical l, boolean valuesOnly) {
        content = new int[l.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = l.getLogical(i);
        }
        if (!valuesOnly) {
            dimensions = l.dimensions();
            names = l.names();
            attributes = l.attributes();
        }
    }

    public LogicalImpl(RLogical l, int[] dimensions, Names names, Attributes attributes) {
        content = new int[l.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = l.getLogical(i);
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
    public int getLogical(int i) {
        return content[i];
    }

    @Override
    public RAny boxedGet(int i) {
        return RLogicalFactory.getScalar(getLogical(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return content[i] == RLogical.NA;
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
    public RRaw asRaw() {
        return TracingView.ViewTrace.trace(new RLogical.RRawView(this));
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RLogical.RLogicalUtils.logicalToRaw(this, warn);
    }

    @Override
    public RLogical asLogical() {
        return this;
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return this;
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getFor(content, dimensions(), names());
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return asInt();
    }

    @Override
    public RDouble asDouble() {
        return TracingView.ViewTrace.trace(new RLogical.RDoubleView(this));
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return asDouble();
    }

    @Override
    public RComplex asComplex() {
        return TracingView.ViewTrace.trace(new RLogical.RComplexView(this));
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return TracingView.ViewTrace.trace(new RLogical.RStringView(this));
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return asString();
    }

    @Override
    public LogicalImpl materialize() {
        return this;
    }

    private static final String EMPTY_STRING = RLogical.TYPE_STRING + "(0)";
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
            str.append(Convert.prettyNA(Convert.logical2string(content[0])));
            for (int i = 1; i < content.length; i++) {
                str.append(", ");
                str.append(Convert.prettyNA(Convert.logical2string(content[i])));
            }
        }
        str.append(attributesPretty());
        return str.toString();
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        return factory.fromLogical();
    }

    @Override
    public RArray subset(RInt index) {
        return RLogical.RLogicalFactory.subset(this, index);
    }

    @Override
    public String typeOf() {
        return RLogical.TYPE_STRING;
    }

    @Override
    public LogicalImpl doStrip() {
        return new LogicalImpl(content, null, null, null, false);
    }

    @Override
    public LogicalImpl doStripKeepNames() {
        return new LogicalImpl(content, null, names, null, false);
    }
}
