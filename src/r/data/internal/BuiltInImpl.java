package r.data.internal;

import r.*;
import r.Convert.*;
import r.builtins.*;
import r.data.*;
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
    public RAttributes getAttributes() {
        return null;
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
    public RLogical asLogical() {
        return null;
    }

    @Override
    public RInt asInt() {
        return null;
    }

    @Override
    public RDouble asDouble() {
        return null;
    }

    @Override
    public RString asString() {
        Utils.nyi();
        return null;
    }

    @Override
    public RList asList() {
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
    public RLogical asLogical(NAIntroduced naIntroduced) {
        Utils.nyi();
        return null;
    }

    @Override
    public RInt asInt(NAIntroduced naIntroduced) {
        Utils.nyi();
        return null;
    }

    @Override
    public RDouble asDouble(NAIntroduced naIntroduced) {
        Utils.nyi();
        return null;
    }

    @Override
    public RString asString(NAIntroduced naIntroduced) {
        Utils.nyi();
        return null;
    }

    @Override
    public String typeOf() {
        return RBuiltIn.TYPE_STRING;
    }

}
