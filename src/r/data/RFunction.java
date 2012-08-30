package r.data;

import r.data.internal.*;
import r.nodes.truffle.*;

public interface RFunction extends RAny {
    RArgumentList args();
    RFunction parent();
    RNode body();
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
