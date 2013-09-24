package r.data;

import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

public interface RFunction {
    RFunction enclosingFunction();

    RSymbol[] paramNames();
    RNode[] paramValues();
    RNode body();
    RClosure createClosure(Frame frame);
    Frame createFrame(Frame enclosingFrame);
    RSymbol[] localWriteSet();
    FrameDescriptor frameDescriptor();
    Object call(Frame frame);
    Object callNoDefaults(Frame frame);
    ASTNode getSource();

    int nlocals();
    int nparams();
    int dotsIndex();

    public static final class EnclosingSlot {

        public EnclosingSlot(RSymbol sym, int hops, int slot) {
            symbol = sym;
            this.hops = hops;
            this.slot = slot;
        }

        public final RSymbol symbol;
        public final int hops;
        public final int slot;
    }

    int positionInLocalWriteSet(RSymbol sym);
    int positionInLocalReadSet(RSymbol sym);
    EnclosingSlot getLocalReadSetEntry(RSymbol sym);
    int localSlot(RSymbol sym);
    EnclosingSlot enclosingSlot(RSymbol sym);
    boolean hasLocalOrEnclosingSlot(RSymbol sym);
    boolean hasLocalSlot(RSymbol sym);
}
