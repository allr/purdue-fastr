package r.nodes.truffle;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.data.RFunction.EnclosingSlot;
import r.errors.*;
import r.nodes.*;

// FIXME: the frame slot lookup can be done statically, like in ArithmeticUpdateVariable
// TODO: needs to be updated with eval in mind (e.g. correct handling of top-level vs. empty environment)
public abstract class ReadVariable extends BaseR {

    final RSymbol symbol;

    private static final boolean DEBUG_R = false;

    public ReadVariable(ASTNode orig, RSymbol sym) {
        super(orig);
        symbol = sym;
    }

    // FIXME: merge this with REnvironment.GLOBAL
    public static RAny readNonVariable(ASTNode ast, RSymbol symbol) {
        // builtins
        RBuiltIn builtIn = Primitives.getBuiltIn(symbol, null);
        if (builtIn != null) {
            return builtIn;
        } else {
            throw RError.getUnknownVariable(ast, symbol);
        }
    }

    public static ReadVariable getUninitialized(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    ReadVariable node;
                    FrameSlot slot;
                    EnclosingSlot rse;
                    String reason;

                    // FIXME: revisit this with eval and language objects in mind
                    if (frame == null) {
                        node = getReadOnlyFromTopLevel(getAST(), symbol);
                        reason = "installReadOnlyFromTopLevelNode";
                    } else if ((slot = RFrameHeader.findVariable(frame, symbol)) != null) {
                        node = getReadLocal(getAST(), symbol, slot);
                        reason = "installReadLocalNode";
                    } else if ((rse = RFrameHeader.readSetEntry(frame, symbol)) == null) {
                            // note: this can happen even without reflective variable access, when reading a top-level variable from a top-level function
                        node = getReadTopLevel(getAST(), symbol);
                        reason = "installReadTopLevel";
                    } else {
                        node = getReadEnclosing(getAST(), symbol, rse.hops, rse.slot);
                        reason = "installReadEnclosingNode";
                    }
                    replace(node, reason);
                    if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" uninitialized rewritten: "+reason); }
                    return node.execute(frame);
                }
            }
        };
    }

    /** GRAAL changed read variable for a really fast path for existing local variables. Anonymous inner class replaced
     * too.
     */
    public static ReadVariable getReadLocal(ASTNode orig, RSymbol sym, final FrameSlot slot) {
        return new ReadLocalVariable(orig, sym, slot);
        /*return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = RFrameHeader.readViaWriteSet(frame, slot, symbol);
                if (val == null) {
                    return readNonVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" local-ws, returns "+val+" ("+val.pretty()+") from slot "+slot); }
                return val;
            }
        }; */
    }

    static class ReadLocalVariable extends ReadVariable {

        protected final FrameSlot _slot;

        public ReadLocalVariable(ASTNode ast, RSymbol sym, FrameSlot slot) {
            super(ast, sym);
            _slot = slot;
        }

        public ReadLocalVariable(ReadLocalVariable from) {
            super(from.ast, from.symbol);
            _slot = from._slot;
        }

        @Override
        public RAny execute(Frame frame) {
            RAny result = RFrameHeader.readViaWriteSetFastPath(frame, _slot);
            CompilerDirectives.transferToInterpreter();
            if (result != null)
                replace(new ReadExistingLocalVariable(this));
            else
                result = replace(new ReadGenericLocalVariable(this)).execute(frame);
            return result;
        }
    }

    static class ReadExistingLocalVariable extends ReadLocalVariable {

        public ReadExistingLocalVariable(ReadLocalVariable from) {
            super(from);
        }

        @Override
        public RAny execute(Frame frame) {
            RAny result = RFrameHeader.readViaWriteSetFastPath(frame, _slot);
            if (result != null)
                return result;
            CompilerDirectives.transferToInterpreter();
            return replace(new ReadGenericLocalVariable(this)).execute(frame);
        }
    }

    static class ReadGenericLocalVariable extends ReadLocalVariable {

        public ReadGenericLocalVariable(ReadLocalVariable from) {
            super(from);
        }


        @Override
        public final RAny execute(Frame frame) {
            RAny val = RFrameHeader.readViaWriteSet(frame, _slot, symbol);
            if (val == null) {
                return readNonVariable(ast, symbol);
            }
            if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" local-ws, returns "+val+" ("+val.pretty()+") from slot "+_slot); }
            return val;
        }

    }



    public static ReadVariable getReadEnclosing(ASTNode orig, RSymbol sym, final int hops, final FrameSlot slot) {
        // FIXME: could we get better performance through updating hops, position ?
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                RAny val = RFrameHeader.readViaReadSet(frame, hops, slot, symbol);
                if (val == null) {
                    return readNonVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" read-set, returns "+val+" ("+val.pretty()+") from slot "+slot+" hops "+hops); }
                return val;
            }
        };
    }

    public static ReadVariable getReadTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            int version;

            @Override
            public final Object execute(Frame frame) {
                RAny val;

                // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                // (same as SuperWriteVariable)

                if (version != symbol.getVersion()) {
                    val = RFrameHeader.readFromExtensionEntry(frame, symbol);
                    if (val == null) {
                        version = symbol.getVersion();
                        // oldFrame = frame;
                        val = symbol.getValue();
                    }
                } else {
                    val = symbol.getValue();
                }
                if (val == null) {
                    return readNonVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" top-level, returns "+val+" ("+val.pretty()+")" ); }
                return val;
            }
        };
    }

    public static ReadVariable getReadOnlyFromTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                assert Utils.check(frame == null);
                RAny val = symbol.getValue();
                if (val == null) {  // TODO: another node
                    return readNonVariable(ast, symbol);
                }
                return val;
            }
        };
    }
}
