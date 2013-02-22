package r.data.internal;

import r.*;
import r.Convert.*;
import r.builtins.*;
import r.data.*;
import r.data.RAny.Attributes;
import r.nodes.*;
import r.nodes.truffle.*;

// this is a holder of a builtin when passed around in variables, e.g. in a way closures are
// note we can't use a builtin object for this, because such object is dependent on the argument names and numbers (and possibly constant arguments)
public class BuiltInImpl implements RBuiltIn {

    final CallFactory callFactory;

    public BuiltInImpl(CallFactory callFactory) {
        this.callFactory = callFactory;
    }

    @Override
    public RSymbol name() {
        return callFactory.name();
    }

    @Override
    public String pretty() {
        return "<builtin " + name().pretty() + ">";
    }

    @Override
    public CallFactory callFactory() {
        return callFactory;
    }

    @Override
    public RAny stripAttributes() {
        return this;
    }

    @Override
    public String prettyMatrixElement() {
        return pretty();
    }

    @Override
    public RRaw asRaw() {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical() {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble() {
        Utils.nyi();
        return null;
    }

    @Override
    public RComplex asComplex() {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override
    public RList asList() {
        Utils.nyi();
        return null;
    }

    @Override
    public void ref() {
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        Utils.nyi();
        return null;
    }

    @Override
    public RRaw asRaw(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RLogical asLogical(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RComplex asComplex(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString(ConversionStatus warn) {
        Utils.nyi();
        return null;
    }

    @Override
    public String typeOf() {
        return RBuiltIn.TYPE_STRING;
    }

    @Override
    public Attributes attributes() {
        return null;
    }

    @Override
    public Attributes attributesRef() {
        return null;
    }

    @Override
    public RArray setAttributes(Attributes attributes) {
        Utils.nyi();
        return null;
    }
}
