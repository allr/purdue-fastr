package r.data.internal;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert.ConversionStatus;
import r.builtins.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;


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
    public String pretty() { // FIXME: clean this up when subclasses are implemented
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
    public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
        if (!inherits) {
            RFrame.localWrite(frame, name, value);
            return;
        } else {
            RFrame.reflectiveInheritsWrite(frame, name, value);
        }
    }

    @Override
    public RAny get(RSymbol name, boolean inherits) {
        if (!inherits) {
            return RFrame.localRead(frame, name);
        } else {
            return RFrame.read(frame, name);
        }
    }

    @Override
    public boolean exists(RSymbol name, boolean inherits) {
        if (!inherits) {
            return RFrame.localExists(frame, name);
        } else {
            return RFrame.exists(frame, name);
        }
    }

    @Override
    public RCallable match(RSymbol name) {
        return RFrame.match(frame, name);
    }

    @Override
    public RSymbol[] ls() {
        return RFrame.listSymbols(frame);
    }

    public static RAny readFromTopLevel(RSymbol sym) {
        return sym.getValue();
    }

    // a custom environment has no function (no read set or write set)
    // it always has an extension
    public static class Custom extends EnvironmentImpl implements REnvironment {

        public Custom(Frame frame) {
            super(frame);
        }

        public static Custom create(Frame parentFrame, REnvironment rootEnvironment, boolean hash, int hashSize) {
            Frame newFrame = new Frame(RFrame.RESERVED_SLOTS, parentFrame);
            newFrame.setObject(RFrame.FUNCTION_SLOT, REnvironment.DUMMY_FUNCTION);
            if (hash) {
                RFrame.installHashedExtension(newFrame, hashSize);
            } else {
                RFrame.installExtension(newFrame);
            }
            RFrame.setRootEnvironment(newFrame, rootEnvironment);
            return new Custom(newFrame);
        }

        @Override
        public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
            if (!inherits) {
                RFrame.customLocalWrite(frame, name, value);
                return;
            } else {
                RFrame.customReflectiveInheritsWrite(frame, name, value);
            }
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            if (!inherits) {
                return RFrame.customLocalRead(frame, name);
            } else {
                return RFrame.customRead(frame, name);
            }
        }

        @Override
        public boolean exists(RSymbol name, boolean inherits) {
            if (!inherits) {
                return RFrame.customLocalExists(frame, name);
            } else {
                return RFrame.customExists(frame, name);
            }
        }

        @Override
        public RCallable match(RSymbol name) {
            Utils.nyi("generic match");
            return null;
        }
    }

    public static class Global extends EnvironmentImpl implements REnvironment {

        public Global() {
            super(null);
        }

        @Override
        public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
            RFrame.writeToTopLevelCondRef(name, value);
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            if (!inherits) {
                return readFromTopLevel(name);
            } else {
                RAny res = readFromTopLevel(name);
                if (res != null) {
                    return res;
                }
                // builtins
                return Primitives.getBuiltIn(name, null);
            }
        }

        @Override
        public boolean exists(RSymbol name, boolean inherits) {
            if (!inherits) {
                return readFromTopLevel(name) != null;
            } else {
                RAny res = readFromTopLevel(name);
                if (res != null) {
                    return true;
                }
                return Primitives.hasCallFactory(name, null);
            }
        }

        @Override
        public RCallable match(RSymbol name) {
            RAny res = readFromTopLevel(name);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
            // builtins
            return Primitives.getBuiltIn(name, null);
        }

        @Override
        public RSymbol[] ls() {
            return RSymbol.listSymbols();
        }

        @Override
        public String pretty() {
            return "<environment: R_GlobalEnv>";
        }
    }

    public static class Empty extends EnvironmentImpl implements REnvironment {

        public Empty() {
            super(null);
        }

        @Override
        public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
            throw RError.getAssignEmpty(ast);
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            return null;
        }

        @Override
        public boolean exists(RSymbol name, boolean inherits) {
            return false;
        }

        @Override
        public RCallable match(RSymbol name) {
            return null;
        }

        @Override
        public RSymbol[] ls() {
            return RSymbol.EMPTY_SYMBOL_ARRAY;
        }

        @Override
        public String pretty() {
            return "<environment: R_EmptyEnv>";
        }
    }
}
