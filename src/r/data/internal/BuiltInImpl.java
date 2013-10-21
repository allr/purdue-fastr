package r.data.internal;

import r.Convert.ConversionStatus;
import r.*;
import r.builtins.*;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;

// this is a holder of a builtin when passed around in variables, e.g. in a way closures are
// note we can't use a builtin object for this, because such object is dependent on the argument names and numbers (and possibly constant arguments)
public class BuiltInImpl implements RBuiltIn {

    final CallFactory callFactory;

    public BuiltInImpl(CallFactory callFactory) {
        this.callFactory = callFactory;
    }

    @Override public RSymbol name() {
        return callFactory.name();
    }

    @Override public String pretty() {
        return "<builtin " + name().pretty() + ">";
    }

    @Override public CallFactory callFactory() {
        return callFactory;
    }

    @Override public RAny stripAttributes() {
        return this;
    }

    @Override public String prettyMatrixElement() {
        return pretty();
    }

    @Override public RRaw asRaw() {
        throw Utils.nyi();
    }

    @Override public RLogical asLogical() {
        throw Utils.nyi();
    }

    @Override public RInt asInt() {
        throw Utils.nyi();
    }

    @Override public RDouble asDouble() {
        throw Utils.nyi();
    }

    @Override public RComplex asComplex() {
        throw Utils.nyi();
    }

    @Override public RString asString() {
        throw Utils.nyi();
    }

    @Override public RList asList() {
        throw Utils.nyi();
    }

    @Override public void ref() {}

    @Override public boolean isShared() {
        return false;
    }

    @Override public boolean isTemporary() {
        return false;
    }

    @Override public <T extends RNode> T callNodeFactory(OperationFactory<T> factory) {
        throw Utils.nyi();
    }

    @Override public RRaw asRaw(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RLogical asLogical(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RInt asInt(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RDouble asDouble(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RComplex asComplex(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public RString asString(ConversionStatus warn) {
        throw Utils.nyi();
    }

    @Override public String typeOf() {
        return RBuiltIn.TYPE_STRING;
    }

    @Override public Attributes attributes() {
        return null;
    }

    @Override public Attributes attributesRef() {
        return null;
    }

    @Override public RArray setAttributes(Attributes attributes) {
        throw Utils.nyi();
    }

    @Override public boolean dependsOn(RAny value) {
        return false;
    }

    @Override public String toString() {
        return "BuiltInImpl[" + callFactory + "]";
    }

    @Override
    public void visit_all(ValueVisitor v) {
    }

    @Override
    public void accept(ValueVisitor v) {
        v.visit(this);
    }
}
