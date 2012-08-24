package r.data;

public final class RFrameDescriptor {

    final RSymbol[] writeSet;
    final int writeSetBlossom;
    final ReadSetEntry[] readSet;
    final int readSetBlossom;
    // TODO signature and other stuff

    private RFrameDescriptor(RSymbol[] ws, int wsBlossom, ReadSetEntry[] rs, int rsBlossom) {
        writeSet = ws;
        writeSetBlossom = wsBlossom;
        readSet = rs;
        readSetBlossom = rsBlossom;
    }

    public static RFrameDescriptor createFrameDescriptor(RSymbol[] ws, ReadSetEntry[] rs) {
        int wsBlossom = 0;
        int rsBlossom = 0;

        for (RSymbol sym : ws) {
            wsBlossom |= sym.id();
        }
        for (ReadSetEntry rse : rs) {
            rsBlossom |= rse.symbol.id();
        }

        return new RFrameDescriptor(ws, wsBlossom, rs, rsBlossom);
    }


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
        if (isIn(sym.id(), writeSetBlossom)) {
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
        if (isIn(sym.id(), readSetBlossom)) {
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

    static boolean isIn(int id, int blossom) { // TODO maybe move to Utils ?
        return (id & blossom) == id;
    }
}
