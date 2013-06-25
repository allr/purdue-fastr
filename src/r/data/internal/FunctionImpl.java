package r.data.internal;

import r.*;
import r.data.*;
import r.nodes.Function;
import r.nodes.truffle.*;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;
import r.builtins.Return.ReturnException;

// FIXME: with the new Truffle API, some of our older structures are no longer needed (e.g. read set, write set), could remove them
// FIXME: in theory, a read set could be larger, simply a union of all write sets (slots) of enclosing functions
// FIXME: "read set" and "write set" may not be the same names ; it is more "cached parent slots" and "slots"

public class FunctionImpl extends RootNode implements RFunction {


    private static class ArgWriter extends RNode {
        protected final RSymbol name;
        protected final FrameSlot slot;
        @Child protected final RNode defaultValue;

        public ArgWriter(RSymbol name, FrameSlot slot, RNode defaultValue) {
            this.name = name;
            this.slot = slot;
            this.defaultValue = defaultValue;
        }

        protected ArgWriter(ArgWriter other) {
            this.name = other.name;
            this.slot = other.slot;
            this.defaultValue = adoptChild(other.defaultValue);
        }

        @Override
        public Object execute(Frame frame) {
            assert false;
            return null;
        }

        public void execute(Frame frame, Object value) {
            if (value != null) {
                if ((value instanceof Double || value instanceof ScalarDoubleImpl)) {
                    // TODO this does not work yet because of the promises, I must revisit it later
                    CompilerDirectives.transferToInterpreter();
                    replace(new DoubleArgWriter(this)).execute(frame, value);
                } else {
                    CompilerDirectives.transferToInterpreter();
                    replace(new SpecifiedArgWriter(this)).execute(frame,value);
                }
            } else {
                CompilerDirectives.transferToInterpreter();
                replace(new GenericArgWriter(this)).execute(frame,value);
            }
        }

        private static class SpecifiedArgWriter extends ArgWriter {

            protected SpecifiedArgWriter(ArgWriter other) {
                super(other);
            }

            @Override public void execute(Frame frame, Object value) {
                if (value == null) {
                    CompilerDirectives.transferToInterpreter();
                    replace(new GenericArgWriter(this)).execute(frame, value);
                } else {
                    Utils.frameSetObject(frame, slot, value);
                }
            }
        }

        // TODO DoubleArgWriter does not work yet, because of promises
        // TODO this must also be more specialized.
        private static class DoubleArgWriter extends ArgWriter {

            protected DoubleArgWriter(ArgWriter other) {
                super(other);
                slot.setKind(FrameSlotKind.Double);
            }

            @Override
            public void execute(Frame frame, Object value) {
                if (value == null)
                    if (defaultValue != null)
                        value = defaultValue.execute(frame);
                double val;
                if (value instanceof Double) {
                    val = (Double) value;
                } else if (value instanceof ScalarDoubleImpl) {
                    val = ((ScalarDoubleImpl) value).getDouble();
                } else {
                    CompilerDirectives.transferToInterpreter();
                    slot.setKind(FrameSlotKind.Object);
                    replace(new ArgWriter(this)).execute(frame, value);
                    return;
                }
                Utils.frameSetDouble(frame, slot, val);
            }
        }

        private static class GenericArgWriter extends ArgWriter {

            protected GenericArgWriter(ArgWriter other) {
                super(other);
            }

            @Override
            public void execute(Frame frame, Object value) {
                if (value != null) {
                    Utils.frameSetObject(frame, slot, value);
                    // note: ref done by caller
                } else {
                    if (defaultValue != null) {
                        if (FunctionCall.PROMISES) {
                            Utils.frameSetObject(frame, slot, RPromise.createDefault(defaultValue, frame));
                        } else {
                            RAny rvalue = (RAny) defaultValue.execute(frame);
                            if (rvalue != null) {
                                Utils.frameSetObject(frame, slot, rvalue);
                                rvalue.ref();
                            }
                            // NOTE: value can be null when a parameter is missing
                            // NOTE: if such a parameter is not used by the function, R is happy
                        }
                    } else {
                        if (FunctionCall.PROMISES) {
                            Utils.frameSetObject(frame, slot, RPromise.createMissing(name, frame));
                        }
                    }
                }
            }
        }
    }


    final RFunction enclosingFunction;
    final Function source;

    final RSymbol[] paramNames;
    @Child final RNode body;
    final int dotsIndex;

    @Children final ArgWriter[] argWriters;

    final FrameDescriptor frameDescriptor;
    final CallTarget callTarget;

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final EnclosingSlot[] readSet;
    final int readSetBloom;

    private static final boolean DEBUG_CALLS = false;

    public FunctionImpl(Function source, RSymbol[] paramNames, RNode[] paramValues, RNode body, RFunction enclosingFunction, RSymbol[] writeSet, EnclosingSlot[] readSet) {
        this.source = source;
        // TRUFFLE : kept for the paramNames() method of Function
        this.paramNames = paramNames;
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

        // TRUFFLE : each argument how has its own node that does the copying to the frame
        int nparams = paramNames.length;
        frameDescriptor = new FrameDescriptor();
        if (nparams != 0) {
            argWriters = new ArgWriter[nparams];
            for (int i = 0; i < nparams; ++i)
                argWriters[i] = adoptChild(new ArgWriter(paramNames[i], frameDescriptor.addFrameSlot(writeSet[i]), paramValues[i]));
        } else {
            argWriters = null;
        }
        for (int i = nparams; i < writeSet.length; i++) {
            frameDescriptor.addFrameSlot(writeSet[i]);
        }

        callTarget = Truffle.getRuntime().createCallTarget(this, frameDescriptor);

        int tmpDotsIndex = -1;
        for (int i = 0; i < nparams; i++) {
            if (paramNames[i] == RSymbol.THREE_DOTS_SYMBOL) {
                tmpDotsIndex = i;
                break;
            }
        }
        dotsIndex = tmpDotsIndex;
    }

    @Override public Object execute(VirtualFrame frame) {
        // TRUFFLE : each argument how has its own node that does the copying to the frame
        RFrameHeader h = RFrameHeader.header(frame);
        if (argWriters != null) {
            Object[] args = h.arguments();
            for (int i = 0; i < argWriters.length; ++i)
                argWriters[i].execute(frame, args[i]);
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

    @Override
    public int nlocals() {
        return writeSet.length;
    }

    @Override
    public int nparams() {
        return argWriters == null ? 0 : argWriters.length;
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

    @Override public RClosure createClosure(MaterializedFrame enclosingEnvironment) {
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

    @Override public FrameSlot localSlot(RSymbol symbol) {
        return frameDescriptor.findFrameSlot(symbol);
    }

    @Override public EnclosingSlot enclosingSlot(RSymbol symbol) {
        int hops = 0;
        for (RFunction func = enclosingFunction; func != null; func = func.enclosingFunction()) {
            hops++;
            FrameSlot slot = func.localSlot(symbol);
            if (slot != null) { return new EnclosingSlot(symbol, hops, slot); }
        }
        return null;
    }
}
