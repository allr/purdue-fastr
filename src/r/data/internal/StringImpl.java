package r.data.internal;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class StringImpl extends NonScalarArrayImpl implements RString {

    final String[] content;

    public String[] getContent() {
        return content;
    }

    public StringImpl(String[] values, int[] dimensions, Names names, Attributes attributes, boolean doCopy) {
        if (doCopy) {
            content = new String[values.length];
            System.arraycopy(values, 0, content, 0, values.length);
        } else {
            content = values;
        }
        this.dimensions = dimensions;
        this.names = names;
        this.attributes = attributes;
    }

    public StringImpl(String[] values, int[] dimensions) {
        this(values, dimensions, null, null, true);
    }

    public StringImpl(String[] values) {
        this(values, null, null, null, true);
    }

    public StringImpl(int size) {
        content = new String[size];
    }

    public StringImpl(RString v, boolean valuesOnly) {
        content = new String[v.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = v.getString(i);
        }
        if (!valuesOnly) {
            dimensions = v.dimensions();
            names = v.names();
            attributes = v.attributes();
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
    public String getString(int i) {
        return content[i];
    }

    @Override
    public RAny boxedGet(int i) {
        return RString.RStringFactory.getScalar(getString(i));
    }

    @Override
    public boolean isNAorNaN(int i) {
        return content[i] == RString.NA;
    }

    @Override
    public StringImpl set(int i, String val) {
        content[i] = val;
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        content[i] = (String) val;
        return this;
    }

    @Override
    public StringImpl materialize() {
        return this;
    }

    private static final String EMPTY_STRING = RString.TYPE_STRING + "(0)";
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
        StringBuilder str = new StringBuilder();
        if (content[0] != RString.NA) {
            str.append("\"");
            str.append(content[0]); // FIXME: quote
            str.append("\"");
        } else {
            str.append("NA");
        }
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            if (content[i] != RString.NA) {
                str.append("\"");
                str.append(content[i]); // FIXME: quote
                str.append("\"");
            } else {
                str.append("NA");
            }
        }
        return str.toString();
    }

    @Override
    public RRaw asRaw() {
        Utils.check(false, "unreachable");
        return null;
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        return RString.RStringUtils.stringToRaw(this, warn);
    }

    @Override
    public RLogical asLogical() {
        return asLogical(null);
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        return RString.RStringUtils.stringToLogical(this, warn);
    }

    @Override
    public RInt asInt() {
        return asInt(null);
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        return RString.RStringUtils.stringToInt(this, warn);
    }


    @Override
    public RDouble asDouble() {
        return asDouble(null);
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        return RString.RStringUtils.stringToDouble(this, warn);
    }

    @Override
    public RComplex asComplex() {
        return asComplex(null);
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        return RString.RStringUtils.stringToComplex(this, warn);
    }

    @Override
    public RString asString() {
        return this;
    }

    @Override
    public RString asString(ConversionStatus warn) {
        return this;
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi();  // FIXME: is callNodeFactory still used?
        return null;
    }

    @Override
    public RArray subset(RInt index) {
        return RString.RStringFactory.subset(this, index);
    }

    @Override
    public String typeOf() {
        return RString.TYPE_STRING;
    }

    @Override
    public StringImpl doStrip() {
        return new StringImpl(content, null, null, null, false);
    }
}
