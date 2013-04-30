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

/** GRAAL Changed the way arguments are stored to the frame to a list of specific nodes for each argument. Those nodes
 * furthes speculate on the fact that the argument is scalar and therefore does not need to call the highly polymorphic
 * ref() method Graal is not capable of inlining.
 */
public class FunctionImpl extends RootNode implements RFunction {

    final RFunction enclosingFunction;
    final Function source;

    final RSymbol[] paramNames;
    //@Children final RNode[] paramValues;
    @Child final RNode body;

    final FrameDescriptor frameDescriptor;
    //final FrameSlot[] paramSlots;
    final CallTarget callTarget;

    final RSymbol[] writeSet;
    final int writeSetBloom;
    final EnclosingSlot[] readSet;
    final int readSetBloom;
    @Children final ParamWriter[] _paramWriters;

    private static final boolean DEBUG_CALLS = false;


    public String toString() {
        String result ="FunctionImpl";
        for (RSymbol r: paramNames)
            result += r.name();
        return result;
    }

    public FunctionImpl(Function source, RSymbol[] paramNames, RNode[] paramValues, RNode body, RFunction enclosingFunction, RSymbol[] writeSet, EnclosingSlot[] readSet) {
        this.source = source;
        this.paramNames = paramNames;
        //this.paramValues = paramValues;
        this._paramWriters = new ParamWriter[paramValues.length];
        this.body = adoptChild(body);
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
        //paramSlots = new FrameSlot[nparams];
        frameDescriptor = new FrameDescriptor();
        for (int i = 0; i < nparams; i++)
            _paramWriters[i] = adoptChild(new ParamWriter(i,paramValues[i], frameDescriptor.addFrameSlot(writeSet[i], FrameSlotKind.Object)));

        for (int i = nparams; i < writeSet.length; i++)
            frameDescriptor.addFrameSlot(writeSet[i], FrameSlotKind.Object);

        callTarget = Truffle.getRuntime().createCallTarget(this, frameDescriptor);
    }

    /** Writes given argument to the frameslot.
     *
     */
    static class ParamWriter extends RNode {

        final int _idx;
        @Child final RNode _value;
        final FrameSlot _slot;


        public ParamWriter(int idx, RNode value, FrameSlot slot) {
            _idx = idx;
            _value = adoptChild(value);
            _slot = slot;

        }

        @Override
        public Object execute(Frame frame) {
            RFrameHeader h = RFrameHeader.header(frame);
            Object[] args = h.arguments();
            RAny value = (RAny) args[_idx];
            if (value != null) {
                if (value instanceof RAny.NotRefCounted) {
                    CompilerDirectives.transferToInterpreter();
                    replace(new ScalarParamWriter(this)).execute(frame);
                    return null;
                }
            }
            CompilerDirectives.transferToInterpreter();
            replace(new GenericParamWriter(this)).execute(frame);
            return null;
        }

        protected void writeParamDefaultValue(Frame frame) {
            RNode n = _value;
            if (n != null) {
                RAny value = (RAny) n.execute(frame); // TODO: get rid of the context
                if (value != null) {
                    try {
                        frame.setObject(_slot, value);
                    } catch (FrameSlotTypeException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    value.ref();
                }
                // NOTE: value can be null when a parameter is missing
                // NOTE: if such a parameter is not used by the function, R is happy
            }
        }
    }

    /** Scalar param writer, that does not call the ref() method on itself. */
    static class ScalarParamWriter extends ParamWriter {

        public ScalarParamWriter(ParamWriter other) {
            super(other._idx, other._value, other._slot);
        }

        @Override
        public Object execute(Frame frame) {
            RFrameHeader h = RFrameHeader.header(frame);
            Object[] args = h.arguments();
            RAny value = (RAny) args[_idx];
            if (CompilerDirectives.injectBranchProbability(0.05, value == null)) {
                writeParamDefaultValue(frame);
            } else {
                if (value instanceof RAny.NotRefCounted) {
                    try {
                        frame.setObject(_slot, value);
                    } catch (FrameSlotTypeException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                } else {
                    CompilerDirectives.transferToInterpreter();
                    replace(new GenericParamWriter(this)).execute(frame);
                }
            }
            return null;
        }
    }

    static class GenericParamWriter extends ParamWriter {

        public GenericParamWriter(ParamWriter other) {
            super(other._idx, other._value, other._slot);
        }

        @Override
        public Object execute(Frame frame) {
            RFrameHeader h = RFrameHeader.header(frame);
            Object[] args = h.arguments();
            RAny value = (RAny) args[_idx];
            if (CompilerDirectives.injectBranchProbability(0.05, value == null)) {
                writeParamDefaultValue(frame);
            } else {
                try {
                    frame.setObject(_slot, value);
                } catch (FrameSlotTypeException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                value.ref();
            }
            return null;
        }
    }





    @ExplodeLoop
    @Override public Object execute(VirtualFrame frame) {
        for (ParamWriter pw : _paramWriters)
            pw.execute(frame);
        Object res;
        try {
            res = body.execute(frame);
        } catch (ReturnException re) {
            res = RFrameHeader.header(frame).returnValue();
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
