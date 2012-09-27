package r.data;

import com.oracle.truffle.runtime.Frame;
import r.nodes.ASTNode;
import r.nodes.truffle.*;

public interface RFunction {
    RFunction enclosing();

    RSymbol[] paramNames();
    RNode[] paramValues();
    RNode body();
    RClosure createClosure(Frame frame);

    ASTNode getSource();

    int nlocals();
    int nparams();
    // FIXME: will also need methods to modify a function

    public static final class ReadSetEntry {

        public ReadSetEntry(RSymbol sym, int hops, int pos) {
            symbol = sym;
            frameHops = hops;
            framePos = pos;
        }

        public final RSymbol symbol;
        public final int frameHops;
        public final int framePos;
    }

    int positionInLocalWriteSet(RSymbol sym);
    int positionInLocalReadSet(RSymbol sym);
    ReadSetEntry getLocalReadSetEntry(RSymbol sym);
    boolean isInWriteSet(RSymbol sym);
}
