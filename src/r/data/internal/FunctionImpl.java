package r.data.internal;

import java.util.*;

import r.data.*;
import r.nodes.truffle.*;

public class FunctionImpl extends BaseObject implements RFunction {
    final RArgumentList args;
    Function code; // AST Function node that holds the function body

    final RFunction parent;
    final RSymbol[] writeSet;
    final int writeSetBloom;
    final ReadSetEntry[] readSet;
    final int readSetBloom;

        // note: the locallyWritten and locallyRead hash sets are modified input parameters
    public static RFunction create(RArgumentList args, Function code, RFunction parent, HashSet<RSymbol> locallyWritten, HashSet<RSymbol> locallyRead) {
        HashSet<RSymbol> rs = locallyRead;
        HashSet<RSymbol> ws = locallyWritten;

        for (RSymbol s : ws) {
            rs.remove(s);
        }
        for (int i = 0; i < args.length(); i++) {
            RSymbol s = args.name(i);
            ws.add(s);
            rs.remove(s);
        }
        RSymbol[] writeSet = ws.toArray(new RSymbol[0]);

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

        return new FunctionImpl(args, code, parent, writeSet, writeSetBloom, readSet, readSetBloom);
    }

    FunctionImpl(RArgumentList args, Function code, RFunction parent, RSymbol[] writeSet, int writeSetBloom, ReadSetEntry[] readSet, int readSetBloom) {
        this.args = args;
        this.code = code;
        this.parent = parent;
        this.writeSet = writeSet;
        this.readSet = readSet;
        this.readSetBloom = readSetBloom;
        this.writeSetBloom = writeSetBloom;
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
        return (i==-1) ? null : readSet[i];
    }

    public static boolean isIn(int id, int bloomfilter) { // TODO maybe move to Utils ?
        return (id & bloomfilter) == id;
    }

    @Override
    public String pretty() {
        // FIXME: real R remembers the expression string for this
        StringBuilder str = new StringBuilder();
        str.append("function (");
        for (int i = 0; i < args.length(); i++) {
            if (i >= 1) {
                str.append(",");
            }
            str.append(args.name(i).pretty());
            RNode exp = args.expression(i);
            if (exp != null) {
                str.append("=");
                str.append(args.expression(i).getAST().toString());
            }
        }
        str.append(") {");
        str.append(code.body().getAST().toString());
        str.append("}");
        return str.toString();
    }

    @Override
    public RLogical asLogical() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RInt asInt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RArgumentList args() {
        return args;
    }

    @Override
    public RNode body() {
        return code.body();
    }

    public void setCode(Function code) {
        this.code = code;
    }

    @Override
    public RFunction parent() {
        return parent;
    }
}
