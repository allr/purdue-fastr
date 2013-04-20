package r.data.internal;

import r.Truffle.CallTarget;
import r.Truffle.Children;
import r.Truffle.Frame;
import r.*;
import r.builtins.Return.ReturnException;
import r.data.*;
import r.nodes.Function;
import r.nodes.truffle.*;

public class FunctionImpl extends RNode implements RFunction {

    final RFunction enclosingFunction;
    final Function source;

    final RSymbol[] paramNames;
    @Children final RNode[] paramValues;
    final RNode body;

    final RSymbol[] frameDescriptor;
    final CallTarget callTarget;

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final EnclosingSlot[] readSet;
    final int readSetBloom;

    private static final boolean DEBUG_CALLS = false;

    @Override public void replace0(RNode o, RNode n) {
        replace(paramValues, o, n);
    }

    public FunctionImpl(Function source, RSymbol[] paramNames, RNode[] paramValues, RNode body, RFunction enclosingFunction, RSymbol[] writeSet, EnclosingSlot[] readSet) {
        this.source = source;
        this.paramNames = paramNames;
        this.paramValues = paramValues;
        this.body = body;
        this.enclosingFunction = enclosingFunction;
        this.writeSet = writeSet;
        this.readSet = readSet;

        // calculate bloom masks
        int wsBloom = 0;
        int rsBloom = 0;
        for (RSymbol sym : writeSet) {
            wsBloom |= sym.hash();
        }
        for (EnclosingSlot rse : readSet) {
            rsBloom |= rse.symbol.hash();
        }
        this.readSetBloom = rsBloom;
        this.writeSetBloom = wsBloom;

        if (DEBUG_CALLS) {
            Utils.debug("creating function with");
            Utils.debug("  write set [" + writeSet.length + "]: " + printWriteSet(writeSet));
            Utils.debug("  read set  [" + readSet.length + "]: " + printReadSet(readSet));
        }

        // FIXME: this could be turned into nodes and node rewriting, each argument copied by a special node (the Truffle way to do it)
        int nparams = paramNames.length;
        frameDescriptor = writeSet;
        //new FrameDescriptor();
        /*
         * for (int i = 0; i < nparams; i++) { paramSlots[i] = frameDescriptor.addFrameSlot(writeSet[i]); } for (int i =
         * nparams; i < writeSet.length; i++) { frameDescriptor.addFrameSlot(writeSet[i]); }
         */
        callTarget = new CallTarget(this, frameDescriptor);
    }

    @Override public Object execute(Frame frame) {
        RFrameHeader h = RFrameHeader.header(frame);
        Object[] args = h.arguments();
        for (int i = 0; i < paramNames.length; i++) {
            RAny value = (RAny) args[i]; // FIXME: use RAny array instead?
            if (value != null) {
                frame.setObject(i, value);
                value.ref();
            } else {
                RNode n = paramValues[i];
                if (n != null) {
                    value = (RAny) n.execute(frame); // TODO: get rid of the context
                    if (value != null) {
                        frame.setObject(i, value);
                        value.ref();
                    }
                    // NOTE: value can be null when a parameter is missing
                    // NOTE: if such a parameter is not used by the function, R is happy
                }
            }
        }

        Object res;
        try {
            res = body.execute(frame);
        } catch (ReturnException re) {
            res = h.returnValue();
        }
        return res;
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

    private static String printReadSet(EnclosingSlot[] readSet) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for (EnclosingSlot e : readSet) {
            if (first) {
                first = false;
            } else {
                str.append(", ");
            }
            str.append(e.symbol.pretty());
            str.append(":");
            str.append("(");
            str.append(e.hops);
            str.append(",");
            str.append(e.slot);
            str.append(")");
        }
        return str.toString();
    }

    public int nlocals() {
        return writeSet.length;
    }

    public int nparams() {
        return paramNames.length;
    }

    public int positionInLocalWriteSet(RSymbol sym) {
        if (isIn(sym.hash(), writeSetBloom)) {
            RSymbol[] ws = writeSet;
            int len = ws.length;
            for (int i = 0; i < len; i++) {
                if (ws[i] == sym) { return i; }
            }
        }
        return -1;
    }

    // a version without allocation
    public int positionInLocalReadSet(RSymbol sym) {
        if (isIn(sym.hash(), readSetBloom)) {
            EnclosingSlot[] rs = readSet;
            int len = rs.length;
            for (int i = 0; i < len; i++) {
                if (rs[i].symbol == sym) { return i; }
            }
        }
        return -1;
    }

    public EnclosingSlot getLocalReadSetEntry(RSymbol sym) {
        int i = positionInLocalReadSet(sym);
        return (i == -1) ? null : readSet[i];
    }

    public static boolean isIn(int id, int bloomfilter) { // TODO maybe move to Utils ?
        return (id & bloomfilter) == id;
    }

    @Override public RSymbol[] paramNames() {
        return paramNames;
    }

    @Override public RNode[] paramValues() {
        return paramValues;
    }

    @Override public RNode body() {
        return body;
    }

    @Override public RFunction enclosingFunction() {
        return enclosingFunction;
    }

    @Override public Function getSource() {
        return source;
    }

    @Override public CallTarget callTarget() {
        return callTarget;
    }

    @Override public RClosure createClosure(Frame enclosingEnvironment) {
        return new ClosureImpl(this, enclosingEnvironment);
    }

    @Override public boolean isInWriteSet(RSymbol sym) {
        if (positionInLocalWriteSet(sym) != -1) { return true; }
        if (enclosingFunction == null) { return false; }
        return enclosingFunction.isInWriteSet(sym);
    }

    @Override public RSymbol[] localWriteSet() {
        return writeSet;
    }

    @Override public int localSlot(RSymbol symbol) {
        for (int i = 0; i < frameDescriptor.length; i++)
            if (symbol == frameDescriptor[i]) return i;
        return -1;
        //    return frameDescriptor.findFrameSlot(symbol);
    }

    @Override public EnclosingSlot enclosingSlot(RSymbol symbol) {
        int hops = 0;
        for (RFunction func = enclosingFunction; func != null; func = func.enclosingFunction()) {
            hops++;
            int slot = func.localSlot(symbol);
            if (slot != -1) { return new EnclosingSlot(symbol, hops, slot); }
        }
        return null;
    }
}
