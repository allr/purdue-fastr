package r.data;

public class RFrameDescriptor {

    final RSymbol[] writeSet;
    final ReadSetEntry[] readSet;

    RFrameDescriptor(RSymbol[] ws, ReadSetEntry[] rs) {
        writeSet = ws;
        readSet = rs;
    }

    // TODO signature and other stuff

    static class ReadSetEntry {

        ReadSetEntry(RSymbol sym, int hops, int pos) {
            symbol = sym;
            frameHops = hops;
            framePos = pos;
        }

        public final RSymbol symbol;
        public final int frameHops;
        public final int framePos;
    }

    public int positionInWriteSet(RSymbol sym) {
        RSymbol[] ws = writeSet;
        int len = ws.length;
        for (int i = 0; i < len; i++) {
            if (ws[i] == sym) {
                return i;
            }
        }
        return -1;
    }

    public ReadSetEntry getReadSetEntry(RSymbol sym) {
        ReadSetEntry[] rs = readSet;
        int len = rs.length;
        for (int i = 0; i < len; i++) {
            if (rs[i].symbol == sym) {
                return rs[i];
            }
        }
        return null;
    }
}
