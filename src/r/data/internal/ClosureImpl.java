package r.data.internal;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.Convert.*;
import r.data.*;
import r.data.RAny.*;

public class ClosureImpl extends BaseObject implements RClosure {

    final MaterializedFrame environment;
    final RFunction function;

    public ClosureImpl(RFunction function, MaterializedFrame environment) {
        this.function = function;
        this.environment = environment;
    }

    @Override
    public String pretty() {
        Utils.check(function != null);
        StringBuilder str = new StringBuilder();
        str.append(function.getSource().toString());
        if (environment != null) {
            str.append(" <ENVIRONMENT " + environment + ">");
        }
        return str.toString();
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
    public MaterializedFrame enclosingFrame() {
        return environment;
    }

    @Override
    public RFunction function() {
        return function;
    }

    @Override
    public boolean isShared() { // FIXME: will have to update this when we support modification of closures
        return false;
    }

    @Override
    public void ref() { // FIXME: will have to update this when we support modification of closures
    }

    @Override
    public ClosureImpl stripAttributes() {
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
        return RClosure.TYPE_STRING;
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

    @Override
    public boolean dependsOn(RAny value) {
        return false;
    }
}
