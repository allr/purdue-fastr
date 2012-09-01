package r.data;

import r.nodes.ASTNode;
import r.nodes.truffle.*;

public interface RFunction {
    RFunction enclosing();

    RSymbol[] argNames();
    RNode[] argExprs();
    RNode body();
    RClosure createClosure(RFrame frame);

    ASTNode getSource();

    int nlocals();
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

    int positionInWriteSet(RSymbol sym);
    int positionInReadSet(RSymbol sym);
    ReadSetEntry getReadSetEntry(RSymbol sym);
}
