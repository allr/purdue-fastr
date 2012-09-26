package r.data.internal;

import com.oracle.truffle.runtime.Frame;
import r.*;
import r.data.*;
import r.nodes.Function;
import r.nodes.truffle.*;

// FIXME: "read set" and "write set" may not be the same names ; it is more "cached parent slots" and "slots"
public class FunctionImpl extends BaseObject implements RFunction {

    final RFunction enclosing;

    final Function source;

    final RSymbol[] paramNames;
    final RNode[] paramValues;
    final RNode body;

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final ReadSetEntry[] readSet;
    final int readSetBloom;

    private static final boolean DEBUG_CALLS = false;

    public FunctionImpl(Function source, RSymbol[] paramNames, RNode[] paramValues, RNode body, RFunction enclosing, RSymbol[] writeSet, ReadSetEntry[] readSet) {
        this.source = source;
        this.paramNames = paramNames;
        this.paramValues = paramValues;
        this.body = body;
        this.enclosing = enclosing;
        this.writeSet = writeSet;
        this.readSet = readSet;

        // calculate blossom masks
        int wsBlossom = 0;
        int rsBlossom = 0;
        for (RSymbol sym : writeSet) {
            wsBlossom |= sym.hash();
        }
        for (ReadSetEntry rse : readSet) {
            rsBlossom |= rse.symbol.hash();
        }
        this.readSetBloom = rsBlossom;
        this.writeSetBloom = wsBlossom;

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

    public int positionInLocalWriteSet(RSymbol sym) {
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
    public int positionInLocalReadSet(RSymbol sym) {
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

    public ReadSetEntry getLocalReadSetEntry(RSymbol sym) {
        int i = positionInLocalReadSet(sym);
        return (i == -1) ? null : readSet[i];
    }

    public static boolean isIn(int id, int bloomfilter) { // TODO maybe move to Utils ?
        return (id & bloomfilter) == id;
    }

    @Override
    public RSymbol[] paramNames() {
        return paramNames;
    }

    @Override
    public RNode[] paramValues() {
        return paramValues;
    }

    @Override
    public RNode body() {
        return body;
    }

    @Override
    public RFunction enclosing() {
        return enclosing;
    }

    @Override
    public Function getSource() {
        return source;
    }

    @Override
    public RClosure createClosure(Frame frame) {
        return new ClosureImpl(this, frame);
    }

    @Override
    public boolean isInWriteSet(RSymbol sym) {
        if (positionInLocalWriteSet(sym) != -1) {
            return true;
        }
        if (enclosing == null) {
            return false;
        }
        return enclosing.isInWriteSet(sym);
    }
}
