package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.Function;
import r.nodes.truffle.*;
import r.runtime.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.nodes.*;

import r.builtins.Return.ReturnException;

// FIXME: with the new Truffle API, some of our older structures are no longer needed (e.g. read set, write set), could remove them
// FIXME: in theory, a read set could be larger, simply a union of all write sets (slots) of enclosing functions
// FIXME: "read set" and "write set" may not be the same names ; it is more "cached parent slots" and "slots"

public class FunctionImpl extends BaseR implements RFunction {

    final RFunction enclosingFunction;
    final Function source;

    final RSymbol[] paramNames;
    @Children final RNode[] paramValues;
    final RNode body;
    final int dotsIndex;

    final FrameDescriptor frameDescriptor;

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final EnclosingSlot[] readSet;
    final int readSetBloom;

    private static final boolean DEBUG_CALLS = false;

    public FunctionImpl(Function source, RSymbol[] paramNames, RNode[] paramValues, RNode body, RFunction enclosingFunction, RSymbol[] writeSet, EnclosingSlot[] readSet) {
        super(source);
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
        frameDescriptor = new FrameDescriptor(writeSet);

        int tmpDotsIndex = -1;
        for (int i = 0; i < nparams; i++) {
            if (paramNames[i] == RSymbol.THREE_DOTS_SYMBOL) {
                tmpDotsIndex = i;
                break;
            }
        }
        dotsIndex = tmpDotsIndex;
    }

    @Override
    public Object call(Frame frame) {

        // TODO: as we no longer have truffle, get rid of this
        // do only a single pass through the parameters to fill in (see placeArgs)
        for (int i = 0; i < paramValues.length; i++) {
            Object value = frame.get(i);
            if (value == null) {
                RNode n = paramValues[i];
                if (n != null) {
                    if (FunctionCall.PROMISES) {
                        frame.set(i, RPromise.createDefault(n, frame));
                    } else {
                        RAny rvalue = (RAny) n.execute(frame);

                        if (rvalue != null) {
                            frame.set(i, rvalue);
                            rvalue.ref();
                        }
                        // NOTE: value can be null when a parameter is missing
                        // NOTE: if such a parameter is not used by the function, R is happy
                    }
                } else {
                    if (FunctionCall.PROMISES) {
                        frame.set(i, RPromise.createMissing(paramNames[i], frame));
                    }
                }
            }
        }

        Object res;
        try {
            res = body.execute(frame);
        } catch (ReturnException re) {
            res = frame.returnValue();
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

    @Override
    public int nlocals() {
        return writeSet.length;
    }

    @Override
    public int nparams() {
        return paramNames.length;
    }

    @Override
    public int dotsIndex() {
        return dotsIndex;
    }

    @Override
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
    @Override
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

    @Override
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
        return frameDescriptor.findFrameSlot(symbol);
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

    @Override
    public FrameDescriptor frameDescriptor() {
        return frameDescriptor;
    }

    @Override public Object execute(Frame frame) {
        assert Utils.check(false, "unreachable");
        return null;
    }

    public Frame createFrame(Frame enclosingFrame) {
        switch(nlocals()) {
            case 1:
                return new SmallFrame.SmallFrame1Slot(this, enclosingFrame, frameDescriptor());
            case 2:
                return new SmallFrame.SmallFrame2Slots(this, enclosingFrame, frameDescriptor());
            case 3:
                return new SmallFrame.SmallFrame3Slots(this, enclosingFrame, frameDescriptor());
            case 4:
                return new SmallFrame.SmallFrame4Slots(this, enclosingFrame, frameDescriptor());
            default:
                return new GenericFrame(this, enclosingFrame, frameDescriptor());
        }
    }
}
