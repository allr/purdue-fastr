package r.nodes.truffle;

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

    public final RSymbol symbol;

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

    public static RNode getUninitialized(ASTNode orig, RSymbol sym) {

        int ddIndex = sym.dotDotValue();
        if (ddIndex != -1) {
            return new ReadDotDotVariable(orig, ddIndex - 1);
        }

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

                    if (frame == null) {
                        node = getReadOnlyFromTopLevel(getAST(), symbol);
                        reason = "installReadOnlyFromTopLevelNode";
                    } else if ((slot = RFrameHeader.findVariable(frame, symbol)) != null) {
                        node = getSimpleReadLocal(getAST(), symbol, slot);
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

    public static class SimpleLocal extends ReadVariable {

        public final FrameSlot slot;

        public SimpleLocal(ASTNode orig, RSymbol sym, FrameSlot slot) {
            super(orig, sym);
            this.slot = slot;
        }

        @Override
        public final Object execute(Frame frame) {
            try {
                Object value = RFrameHeader.getObjectForcingPromises(frame, slot);
                if (value == null) {
                    throw new UnexpectedResultException(null);
                }
                return value;
            } catch (UnexpectedResultException e) {
                return replace(getReadLocal(ast, symbol, slot)).execute(frame);
            }
        }
    }

    private static ReadVariable getSimpleReadLocal(ASTNode orig, RSymbol sym, final FrameSlot slot) {
        return new SimpleLocal(orig, sym, slot);
    }

    /** Fast and simple read from inlined frame. This class is very specialized and it assumes that the variable is
     * always written before being read (e.g. when arguments are stored). If this condition is not met, an NPE will
     * likely occur.
     */
    public static class InlinedLocal extends ReadVariable {

        final RAny[] locals;
        final int index;

        public InlinedLocal(ASTNode orig, RSymbol symbol, RAny[] locals, int index) {
            super(orig, symbol);
            this.locals = locals;
            this.index = index;
        }

        @Override
        public Object execute(Frame frame) {
            return locals[index];
        }
    }

    private static ReadVariable getReadLocal(ASTNode orig, RSymbol sym, final FrameSlot slot) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                Object val = RFrameHeader.readViaWriteSet(frame, slot, symbol);
                if (val == null) {
                    return readNonVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" local-ws, returns "+val+" ("+((RAny)val).pretty()+") from slot "+slot); }
                return val;
            }
        };
    }

    private static ReadVariable getReadEnclosing(ASTNode orig, RSymbol sym, final int hops, final FrameSlot slot) {
        // FIXME: could we get better performance through updating hops, position ?
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                Object val = RFrameHeader.readViaReadSet(frame, hops, slot, symbol);
                if (val == null) {
                    return readNonVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" read-set, returns "+val+" ("+((RAny)val).pretty()+") from slot "+slot+" hops "+hops); }
                return val;
            }
        };
    }

    private static ReadVariable getReadTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            int version;

            @Override
            public final Object execute(Frame frame) {
                Object val;

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
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" top-level, returns "+val+" ("+((RAny) val).pretty()+")" ); }
                return val;
            }
        };
    }

    private static ReadVariable getReadOnlyFromTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                assert Utils.check(frame == null);
                Object val = symbol.getValue();
                if (val == null) {  // TODO: another node
                    return readNonVariable(ast, symbol);
                }
                return val;
            }
        };
    }

    private static class ReadDotDotVariable extends BaseR {
        final int index; // index in ..., 0-based
        @Child RNode readDots;

        public ReadDotDotVariable(ASTNode ast, int index) {
            super(ast);
            assert Utils.check(index >= 0);

            this.index = index;
            readDots = adoptChild(ReadVariable.getUninitialized(ast, RSymbol.THREE_DOTS_SYMBOL));
        }

        @Override
        public Object execute(Frame frame) {
            RDots dotsValue = (RDots) readDots.execute(frame);
            Object[] values = dotsValue.values();
            int len = values.length;

            if (index < len) {
                return RPromise.force(values[index]);
            }
            throw RError.getDotsBounds(ast, index + 1);
        }
    }
}
