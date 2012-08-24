package r.data;

public final class RFrameDescriptor {

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final ReadSetEntry[] readSet;
    final int readSetBloom;
    // TODO signature and other stuff

    private RFrameDescriptor(RSymbol[] ws, int wsBloom, ReadSetEntry[] rs, int rsBloom) {
        writeSet = ws;
        writeSetBloom = wsBloom;
        readSet = rs;
        readSetBloom = rsBloom;
    }

    public static RFrameDescriptor createFrameDescriptor(RSymbol[] ws, ReadSetEntry[] rs) {
        int wsBloom = 0;
        int rsBloom = 0;

        for (RSymbol sym : ws) {
            wsBloom |= sym.hash();
        }
        for (ReadSetEntry rse : rs) {
            rsBloom |= rse.symbol.hash();
        }

        return new RFrameDescriptor(ws, wsBloom, rs, rsBloom);
    }


    public static final class ReadSetEntry {

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
        if (isIn(sym.hash(), writeSetBloom)) {
            RSymbol[] ws = writeSet;
            int len = ws.length;
            for (int i = 0; i < len; i++) {
                if (ws[i] == sym) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ReadSetEntry getReadSetEntry(RSymbol sym) {
        if (isIn(sym.hash(), readSetBloom)) {
            ReadSetEntry[] rs = readSet;
            int len = rs.length;
            for (int i = 0; i < len; i++) {
                if (rs[i].symbol == sym) {
                    return rs[i];
                }
            }
        }
        return null;
    }

    static boolean isIn(int id, int bloomfilter) { // TODO maybe move to Utils ?
        return (id & bloomfilter) == id;
    }
}
