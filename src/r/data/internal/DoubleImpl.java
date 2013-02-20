package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;

public class DoubleImpl extends NonScalarArrayImpl implements RDouble {

    final double[] content;

    public double[] getContent() {
        return content;
    }

    public DoubleImpl(double[] values, int[] dimensions, Names names, Attributes attributes, boolean doCopy) {
        if (doCopy) {
            content = new double[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
        this.names = names;
        this.attributes = attributes;
    }

    public DoubleImpl(double[] values, int[] dimensions, Names names) {
        this(values, dimensions, names, null, true);
    }

    public DoubleImpl(double[] values) {
        this(values, null, null, null, true);
    }

    public DoubleImpl(int size) {
        content = new double[size];
    }

    public DoubleImpl(RDouble d, boolean valuesOnly) {
        content = new double[d.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = d.getDouble(i);
        }
        if (!valuesOnly) {
            dimensions = d.dimensions();
            names = d.names();
            attributes = d.attributes();
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
    public DoubleImpl materialize() {
        return this;
    }

    private static final String EMPTY_STRING = "numeric(0)"; // NOTE: this is not RDouble.TYPE_STRING (R is inconsistent on this)
    private static final String NAMED_EMPTY_STRING = "named " + EMPTY_STRING;

    @Override
    public String pretty() {
        if (dimensions != null) {
            return matrixPretty();
        }
        if (content.length == 0) {
            return (names() == null) ? EMPTY_STRING : NAMED_EMPTY_STRING;
        }
        if (names() != null) {
            return namedPretty();
        }
        String fst = Convert.prettyNA(Convert.double2string(content[0]));
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.prettyNA(Convert.double2string(content[i])));
        }
        return str.toString();
    }

    @Override
    public RRaw asRaw() {
        return new RDouble.RRawView(this);
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RDouble.RDoubleUtils.doubleToRaw(this, warn);
    }

    @Override
    public RLogical asLogical() {
        return new RDouble.RLogicalView(this);
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return new RDouble.RIntView(this);
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return RDouble.RDoubleUtils.double2int(this, warn);
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return this;
    }

    @Override
    public RComplex asComplex() {
        return new RDouble.RComplexView(this);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return asComplex();
    }

    @Override
    public RString asString() {
        return new RDouble.RStringView(this);
    }

    @Override
    public RString asString(ConversionStatus warn) {
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

    @Override
    public DoubleImpl doStrip() {
        return new DoubleImpl(content, null, null, null, false);
    }
}
