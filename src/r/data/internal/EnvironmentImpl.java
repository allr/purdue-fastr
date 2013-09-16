package r.data.internal;

import java.util.*;

import r.*;
import r.Convert.ConversionStatus;
import r.builtins.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.runtime.*;


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
    public boolean isTemporary() {
        return true; // can modify
    }

    @Override
    public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
        if (!inherits) {
            frame.localWrite(name, value);
            return;
        } else {
            frame.reflectiveInheritsWrite(name, value);
        }
    }

    @Override
    public void delayedAssign(RSymbol name, RPromise value, ASTNode ast) {
        frame.localWriteNoRef(name, value);
    }

    @Override
    public RAny get(RSymbol name, boolean inherits) {
        if (!inherits) {
            return (RAny) frame.localRead(name);
        } else {
            return Utils.cast(frame.read(name));
        }
    }

    @Override
    public Object localGetNotForcing(RSymbol name) {
        return frame.localReadNotForcing(name);
    }

    @Override
    public boolean exists(RSymbol name, boolean inherits) {
        if (!inherits) {
            return frame.localExists(name);
        } else {
            return frame.exists(name);
        }
    }

    @Override
    public RCallable match(RSymbol name) {
        return Frame.match(frame, name);
    }

    public static RSymbol[] removeHidden(RSymbol[] symbols) { // FIXME: unnecessary copying
        ArrayList<RSymbol> nonHidden = new ArrayList<RSymbol>(symbols.length);

        for (RSymbol s : symbols) {
            if (!s.isHidden()) {
                nonHidden.add(s);
            }
        }
        return nonHidden.toArray(new RSymbol[nonHidden.size()]);
    }

    @Override
    public RSymbol[] ls(boolean includingHidden) { // FIXME: maybe could speed-up by propagating the filtering further
        RSymbol[] symbols =  frame.listSymbols();
        if (!includingHidden) {
            return removeHidden(symbols);
        } else {
            return symbols;
        }
    }

    public static Object readFromTopLevel(RSymbol symbol) {
        return symbol.getValue();
    }

    // a custom environment has no function (no read set or write set)
    // it always has an extension
    public static class Custom extends EnvironmentImpl implements REnvironment {

        public Custom(Frame frame) {
            super(frame);
            assert Utils.check(frame != null);
        }

        // NOTE: rootEnvironment only needs to be set when parentFrame is null
        // NOTE: rootEnvironment == null means the global environment
        public static Custom create(Frame parentFrame, REnvironment rootEnvironment, boolean hash, int hashSize) {

            NoSlotsFrame newFrame = new NoSlotsFrame(new DummyFunction(), parentFrame);
            if (hash) {
                newFrame.installHashedExtension(hashSize);
            } else {
                newFrame.installExtension();
            }
            newFrame.rootEnvironment(rootEnvironment);
            return new Custom(newFrame);
        }

        public static Frame createForList(Frame parentFrame, RList list) {

            NoSlotsFrame newFrame = new NoSlotsFrame(new DummyFunction(), parentFrame);
            int size = list.size();
            newFrame.installHashedExtension(size);
            RArray.Names names = list.names();
            if (names != null) {
                RSymbol[] symbols = names.sequence();
                for (int i = 0; i < size; i++) {
                    RSymbol s = symbols[i];
                    if (s != RSymbol.NA_SYMBOL && s != RSymbol.EMPTY_SYMBOL) {
                        newFrame.localWrite(s, list.getRAnyRef(i));
                    }
                }
            }

            return newFrame;
        }

        @Override
        public void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast) {
            if (!inherits) {
                frame.customLocalWrite(name, value);
                return;
            } else {
                frame.customReflectiveInheritsWrite(name, value);
            }
        }

        @Override
        public void delayedAssign(RSymbol name, RPromise value, ASTNode ast) {
            frame.customLocalWriteNoRef(name, value);
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            if (!inherits) {
                return (RAny) frame.customLocalRead(name);
            } else {
                return Utils.cast(frame.customRead(name));
            }
        }

        @Override
        public Object localGetNotForcing(RSymbol name) {
            return frame.customLocalReadNoForcing(name);
        }

        @Override
        public boolean exists(RSymbol name, boolean inherits) {
            if (!inherits) {
                return frame.customLocalExists(name);
            } else {
                return frame.customExists(name);
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
            Frame.writeToTopLevelCondRef(name, value);
        }

        @Override
        public void delayedAssign(RSymbol name, RPromise value, ASTNode ast) {
            Frame.writeToTopLevelNoRef(name, value);
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            if (!inherits) {
                return Utils.cast(readFromTopLevel(name));
            } else {
                RAny res = Utils.cast(readFromTopLevel(name));
                if (res != null) {
                    return res;
                }
                // builtins
                return Primitives.getBuiltIn(name, null);
            }
        }

        @Override
        public Object localGetNotForcing(RSymbol name) {
            return name.getValueNoForce();
        }

        @Override
        public boolean exists(RSymbol name, boolean inherits) {
            if (!inherits) {
                return readFromTopLevel(name) != null;
            } else {
                RAny res = Utils.cast(readFromTopLevel(name));
                if (res != null) {
                    return true;
                }
                return Primitives.hasCallFactory(name, null);
            }
        }

        @Override
        public RCallable match(RSymbol name) {
            Object res = readFromTopLevel(name);
            if (res != null && res instanceof RCallable) {
                return (RCallable) res;
            }
            // builtins
            return Primitives.getBuiltIn(name, null);
        }

        @Override
        public RSymbol[] ls(boolean includingHidden) {
            return RSymbol.listUsedSymbols(includingHidden);
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
        public void delayedAssign(RSymbol name, RPromise value, ASTNode ast) {
            throw RError.getAssignEmpty(ast);
        }

        @Override
        public RAny get(RSymbol name, boolean inherits) {
            return null;
        }

        @Override
        public Object localGetNotForcing(RSymbol name) {
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
        public RSymbol[] ls(boolean includingHidden) {
            return RSymbol.EMPTY_SYMBOL_ARRAY;
        }

        @Override
        public String pretty() {
            return "<environment: R_EmptyEnv>";
        }
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
