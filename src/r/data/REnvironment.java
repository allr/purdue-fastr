package r.data;

import r.Truffle.*;

import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

public interface REnvironment extends RAny {
    String TYPE_STRING = "environment";

    REnvironment EMPTY = new EnvironmentImpl.Empty();
    REnvironment GLOBAL = new EnvironmentImpl.Global();

    DummyFunction DUMMY_FUNCTION = new DummyFunction(); // a placeholder for no local variables, for Frames that do not belong to a real function

    Frame frame();

    void assign(RSymbol name, RAny value, boolean inherits, ASTNode ast);

    RAny get(RSymbol name, boolean inherits);

    boolean exists(RSymbol name, boolean inherits);

    RCallable match(RSymbol name);

    RSymbol[] ls();

    public static class DummyFunction implements RFunction {

        @Override public int positionInLocalWriteSet(RSymbol sym) {
            return -1;
        }

        @Override public int positionInLocalReadSet(RSymbol sym) {
            return -1;
        }

        @Override public EnclosingSlot getLocalReadSetEntry(RSymbol sym) {
            return null;
        }

        @Override public boolean isInWriteSet(RSymbol sym) {
            return false;
        }

        @Override public RFunction enclosingFunction() {
            return null;
        }

        @Override public RSymbol[] paramNames() {
            return null;
        }

        @Override public RNode[] paramValues() {
            return null;
        }

        @Override public RNode body() {
            return null;
        }

        @Override public RClosure createClosure(Frame frame) {
            return null;
        }

        @Override public ASTNode getSource() {
            return null;
        }

        @Override public int nlocals() {
            return 0;
        }

        @Override public int nparams() {
            return 0;
        }

        private static RSymbol[] emptySet = new RSymbol[0];

        @Override public RSymbol[] localWriteSet() {
            return emptySet;
        }

        @Override public int localSlot(RSymbol sym) {
            return -1;
        }

        @Override public CallTarget callTarget() {
            return null;
        }

        @Override public EnclosingSlot enclosingSlot(RSymbol sym) {
            return null;
        }
    }

}
