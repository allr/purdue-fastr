package r.data.internal;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert.ConversionStatus;
import r.data.*;


public class EnvironmentImpl extends BaseObject implements REnvironment {

    final Frame frame;

    public EnvironmentImpl(Frame frame) {
        this.frame = frame;
    }

    @Override
    public Frame frame() {
        return frame;
    }

    @Override
    public String pretty() {
        if (this == REnvironment.EMPTY) {
            return "<environment: R_EmptyEnv>";
        }
        if (this == REnvironment.GLOBAL) {
            return "<environment: R_GlobalEnv>";
        }
        Utils.check(frame != null);
        return "<environment: " + frame + "(" + this + ")>";
    }

    @Override
    public String typeOf() {
        return REnvironment.TYPE_STRING;
    }

    @Override
    public RAny stripAttributes() {
        Utils.nyi();
        return null;
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
    public void ref() {
    }

    @Override
    public boolean isShared() {
        return false; // never copy
    }

    @Override
    public void assign(RSymbol name, RAny value, boolean inherits) {
        Utils.check(frame != null);
        // TODO: add hashing
        if (!inherits) {
            RFrame.localWrite(frame, name, value);
            return;
        } else {
            RFrame.reflectiveInheritsWrite(frame, name, value);
        }
    }

    @Override
    public RAny get(RSymbol name, boolean inherits) {
        Utils.check(frame != null);
        if (!inherits) {
            return RFrame.localRead(frame, name);
        } else {
            return RFrame.read(frame, name);
        }
    }

    @Override
    public boolean exists(RSymbol name, boolean inherits) {
        Utils.check(frame != null);
        if (!inherits) {
            return RFrame.localExists(frame, name);
        } else {
            return RFrame.exists(frame, name);
        }
    }

    @Override
    public RSymbol[] ls() {
        Utils.check(frame != null);
        return RFrame.listSymbols(frame);
    }

}
