package r.data.internal;

import java.util.*;

import r.*;
import r.data.*;
import r.nodes.truffle.*;

// FIXME: "read set" and "write set" may not be the same names ; it is more "cached parent slots" and "slots"
public class FunctionImpl extends BaseObject implements RFunction {
    Function fnode;
    final RFunction parent;
    final RSymbol[] writeSet;
    final int writeSetBloom;
    final ReadSetEntry[] readSet;
    final int readSetBloom;

    private static final boolean DEBUG_CALLS = false;

        // note: the locallyWritten and locallyRead hash sets are modified input parameters
    public static RFunction create(RSymbol[] argNames, Function fnode, RFunction parent, HashSet<RSymbol> locallyWritten, HashSet<RSymbol> locallyRead) {

        // note: we cannot read fnode.argNames() here as it is not yet initialized
        HashSet<RSymbol> rs = locallyRead;
        HashSet<RSymbol> ws = locallyWritten;

        // build write set
        for (RSymbol s : argNames) {
            ws.remove(s); // arguments have to be first in the list and in fixed order
            rs.remove(s); // arguments do not read-set entries because they are always defined
        }
        RSymbol[] writeSet = new RSymbol[ws.size() + argNames.length];
        int i = 0;
        for (; i < argNames.length; i++) {
            writeSet[i] = argNames[i];
        }
        for (RSymbol s : ws) {
            writeSet[i++] = s;
        }

        // build read set
        ReadSetEntry[] readSet;
        if (parent == null || rs.isEmpty()) {
            readSet = new ReadSetEntry[0]; // FIXME: could save allocation and use null
        } else {
            ArrayList<ReadSetEntry> rsl = new ArrayList<>();
            for (RSymbol s : rs) {
                RFunction p = parent;
                int hops = 1;
                while (p != null) {
                    int pos = p.positionInWriteSet(s);
                    if (pos >= 0) {
                        rsl.add(new ReadSetEntry(s, hops, pos)); // FIXME: why not remember the RFunction reference instead of hops?
                        break;
                    }
                    p = p.parent();
                    hops++;
                }
            }
            readSet = rsl.toArray(new ReadSetEntry[0]); // FIXME: rewrite this to get rid of allocation/copying
        }

        // calculate bloom hashes
        int writeSetBloom = 0;
        int readSetBloom = 0;
        for (RSymbol sym : writeSet) {
            writeSetBloom |= sym.hash();
        }
        for (ReadSetEntry rse : readSet) {
            readSetBloom |= rse.symbol.hash();
        }

        return new FunctionImpl(fnode, parent, writeSet, writeSetBloom, readSet, readSetBloom);
    }

    FunctionImpl(Function fnode, RFunction parent, RSymbol[] writeSet, int writeSetBloom, ReadSetEntry[] readSet, int readSetBloom) {
        this.fnode = fnode;
        this.parent = parent;
        this.writeSet = writeSet;
        this.readSet = readSet;
        this.readSetBloom = readSetBloom;
        this.writeSetBloom = writeSetBloom;

        if (DEBUG_CALLS) {
            Utils.debug("creating function with");
            Utils.debug("write set (" + writeSet.length + ") is: " + printWriteSet(writeSet));
            Utils.debug("read set (" + readSet.length + ") is: " + printReadSet(readSet));
        }
    }

    private static String printWriteSet(RSymbol[] writeSet) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        int i = 0;
        for (RSymbol s : writeSet) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(s.pretty());
            str.append(":");
            str.append(i);
            i++;
        }
        return str.toString();
    }

    private static String printReadSet(ReadSetEntry[] readSet) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for (ReadSetEntry e : readSet) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(e.symbol.pretty());
            str.append(":");
            str.append("(");
            str.append(e.frameHops);
            str.append(",");
            str.append(e.framePos);
            str.append(")");
        }
        return str.toString();
    }

    public int nlocals() {
        return writeSet.length;
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

    // a version without allocation
    public int positionInReadSet(RSymbol sym) {
        if (isIn(sym.hash(), readSetBloom)) {
            ReadSetEntry[] rs = readSet;
            int len = rs.length;
            for (int i = 0; i < len; i++) {
                if (rs[i].symbol == sym) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ReadSetEntry getReadSetEntry(RSymbol sym) {
        int i = positionInReadSet(sym);
        return (i == -1) ? null : readSet[i];
    }

    public static boolean isIn(int id, int bloomfilter) { // TODO maybe move to Utils ?
        return (id & bloomfilter) == id;
    }

    @Override
    public RSymbol[] argNames() {
        return fnode.argNames();
    }

    @Override
    public RNode[] argExprs() {
        return fnode.argExprs();
    }

    @Override
    public RNode body() {
        return fnode.body();
    }

    @Override
    public Function node() {
        return fnode;
    }

    public void setFunctionNode(Function fnode) {
        this.fnode = fnode;
    }

    @Override
    public RFunction parent() {
        return parent;
    }
}
