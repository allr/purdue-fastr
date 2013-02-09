package r.data;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;

import r.nodes.ASTNode;
import r.nodes.truffle.*;

public interface RFunction {
    RFunction enclosingFunction();

    RSymbol[] paramNames();
    RNode[] paramValues();
    RNode body();
    RClosure createClosure(MaterializedFrame frame);
    RSymbol[] localWriteSet();

    CallTarget callTarget();

    ASTNode getSource();

    int nlocals();
    int nparams();
    // FIXME: will also need methods to modify a function

    public static final class EnclosingSlot {

        public EnclosingSlot(RSymbol sym, int hops, FrameSlot slot) {
            symbol = sym;
            this.hops = hops;
            this.slot = slot;
        }

        public final RSymbol symbol;
        public final int hops;
        public final FrameSlot slot;
    }

    int positionInLocalWriteSet(RSymbol sym);
    int positionInLocalReadSet(RSymbol sym);
    EnclosingSlot getLocalReadSetEntry(RSymbol sym);
    FrameSlot localSlot(RSymbol sym);
    EnclosingSlot enclosingSlot(RSymbol sym);
    boolean isInWriteSet(RSymbol sym);
}
