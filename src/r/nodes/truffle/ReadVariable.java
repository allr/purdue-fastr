package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.data.RFunction.EnclosingSlot;
import r.errors.*;
import r.nodes.*;
import r.runtime.*;

// FIXME: the frame slot lookup can be done statically, like in ArithmeticUpdateVariable
// TODO: needs to be updated with eval in mind (e.g. correct handling of top-level vs. empty environment)
public abstract class ReadVariable extends BaseR {

    final RSymbol symbol;

    private static final boolean DEBUG_R = false;

    public ReadVariable(ASTNode orig, RSymbol sym) {
        super(orig);
        symbol = sym;
    }

    // FIXME: merge this with REnvironment.GLOBAL, re-visit when adding support for search path
    public static RAny readNonVariablePerhapsBuiltin(ASTNode ast, RSymbol symbol) {
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
                    int slot;
                    EnclosingSlot rse;
                    String reason;

                    if (frame == null) {
                        node = getReadOnlyFromTopLevel(getAST(), symbol);
                        reason = "installReadOnlyFromTopLevelNode";
                    } else if ((slot = frame.findVariable(symbol)) != -1) {
                        node = getSimpleReadLocal(getAST(), symbol, slot);
                        reason = "installReadLocalNode";
                    } else if ((rse = frame.readSetEntry(symbol)) == null) {
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

    private static ReadVariable getSimpleReadLocal(ASTNode orig, RSymbol sym, final int slot) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                try {
                    Object value = frame.getObjectForcingPromises(slot);
                    if (value == null) {
                        throw new UnexpectedResultException(null);
                    }
                    return value;
                } catch (UnexpectedResultException e) {
                    return replace(getReadLocal(ast, symbol, slot)).execute(frame);
                }
            }
        };
    }

    private static ReadVariable getReadLocal(ASTNode orig, RSymbol sym, final int slot) {
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                Object val = frame.readViaWriteSet(slot, symbol);
                if (val == null) {
                    // NOTE: builtins handled in readViaWriteSet
                    throw RError.getUnknownVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" local-ws, returns "+val+" ("+((RAny)val).pretty()+") from slot "+slot); }
                return val;
            }
        };
    }

    private static ReadVariable getReadEnclosing(ASTNode orig, RSymbol sym, final int hops, final int slot) {
        // FIXME: could we get better performance through updating hops, position ?
        return new ReadVariable(orig, sym) {

            @Override
            public final Object execute(Frame frame) {
                Object val = frame.readViaReadSet(hops, slot, symbol);
                if (val == null) {
                    // NOTE: builtins handled in readViaReadSet
                    throw RError.getUnknownVariable(ast, symbol);
                }
                if (DEBUG_R) { Utils.debug("read - "+symbol.pretty()+" read-set, returns "+val+" ("+((RAny)val).pretty()+") from slot "+slot+" hops "+hops); }
                return val;
            }
        };
    }

    private static ReadVariable getReadTopLevel(ASTNode orig, RSymbol sym) {
        return new ReadVariable(orig, sym) {

            // NOTE: we could do more here, and original the plan was to do so

            // we could remember the last frame and version, and update the version whenever we make sure that no variable has been
            // inserted in that frame -- however, I can't see how that could be faster in the common case (an extra branch on the fast path),
            // and I am not sure we care about the slow path

            // NOTE: we would have to remember the frame, as there can be more than one frame active with the node, and some may have an
            // inserted symbol while another may not

            // WARNING: changing the behavior of version will also impact optimizations in function call (calling a builtin)

            // (same as SuperWriteVariable)

            @Override
            public final Object execute(Frame frame) {
                Object val;

                if (symbol.getVersion() != 0) {
                    val = frame.readFromExtensionEntry(symbol);
                    if (val == null) {
                        val = symbol.getValue();
                    }
                } else {
                    val = symbol.getValue();
                }
                if (val == null) {
                    return readNonVariablePerhapsBuiltin(ast, symbol);
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
                    return readNonVariablePerhapsBuiltin(ast, symbol);
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
                Object value = values[index];
                if (value != null) {
                    return RPromise.force(value);
                } else {
                    throw RError.getDotDotMissing(ast, index);
                }
            }
            throw RError.getDotsBounds(ast, index + 1);
        }
    }
}
