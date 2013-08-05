package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;
import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.data.RFunction.*;
import r.nodes.*;

public abstract class SuperWriteVariable extends BaseR {

    final RSymbol symbol;
    @Child RNode expr;

    private SuperWriteVariable(ASTNode ast, RSymbol symbol, RNode expr) {
        super(ast);
        this.symbol = symbol;
        this.expr = adoptChild(expr);
    }

    public static SuperWriteVariable getUninitialized(ASTNode orig, RSymbol sym, RNode rhs) {
        return new SuperWriteVariable(orig, sym, rhs) {

            private Object replaceAndExecute(RNode node, String reason, Frame frame) {
                replace(node, reason);
                return node.execute(frame);
            }

            @Override
            public final Object execute(Frame frame) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    Frame enclosingFrame = (frame != null) ? RFrameHeader.enclosingFrame(frame) : null;

                    if (enclosingFrame == null) {
                        return replaceAndExecute(WriteVariable.getWriteTopLevel(ast, symbol, expr), "install WriteTopLevel from SuperWriteVariable", frame);
                    }

                    FrameSlot slot = RFrameHeader.findVariable(enclosingFrame, symbol);
                    if (slot != null) {
                        return replaceAndExecute(getWriteViaWriteSet(ast, symbol, expr, slot), "install WriteViaWriteSet from SuperWriteVariable", frame);
                    }

                    EnclosingSlot eslot = RFrameHeader.findEnclosingVariable(enclosingFrame, symbol);
                    if (eslot == null) {
                        return replaceAndExecute(getWriteToTopLevel(ast, symbol, expr), "install WriteToTopLevel from SuperWriteVariable", frame);
                    } else {
                        return replaceAndExecute(getWriteViaEnclosingSlot(ast, symbol, expr, eslot.hops, eslot.slot), "install WriteViaReadSet from SuperWriteVariable", frame);
                    }
                }
            }
        };
    }

    public static SuperWriteVariable getWriteViaWriteSet(ASTNode ast, RSymbol symbol, RNode expr, final FrameSlot slot) {
        return new SuperWriteVariable(ast, symbol, expr) {
            @Override
            public Object execute(Frame frame) {
                RAny value = (RAny) expr.execute(frame);
                Frame enclosing = RFrameHeader.enclosingFrame(frame);
                boolean done = RFrameHeader.superWriteViaWriteSet(enclosing, slot, symbol, value);
                assert Utils.check(done);
                return value;
            }
        };
    }

    public static SuperWriteVariable getWriteViaEnclosingSlot(ASTNode ast, RSymbol symbol, RNode expr, final int hops, final FrameSlot slot) {
        return new SuperWriteVariable(ast, symbol, expr) {
            @Override
            public final Object execute(Frame frame) {
                RAny value = (RAny) expr.execute(frame);
                Frame enclosing = RFrameHeader.enclosingFrame(frame);
                boolean done = RFrameHeader.superWriteViaEnclosingSlotAndTopLevel(enclosing, hops, slot, symbol, value);
                assert Utils.check(done);
                return value;
            }
        };
    }

    public static SuperWriteVariable getWriteToTopLevel(ASTNode ast, RSymbol symbol, RNode expr) {
        return new SuperWriteVariable(ast, symbol, expr) {

            // NOTE: we could do more here, and original the plan was to do so

            // we could remember the last frame and version, and update the version whenever we make sure that no variable has been
            // inserted in that frame -- however, I can't see how that could be faster in the common case (an extra branch on the fast path),
            // and I am not sure we care about the slow path

            // NOTE: we would have to remember the frame, as there can be more than one frame active with the node, and some may have an
            // inserted symbol while another may not

            // WARNING: changing the behavior of version will also impact optimizations in function call (calling a builtin)

            // (same as ReadVariable)

            @Override
            public final Object execute(Frame frame) {
                RAny value = (RAny) expr.execute(frame);

                if (symbol.getVersion() != 0) {
                    Frame enclosingFrame = RFrameHeader.enclosingFrame(frame);
                    if (RFrameHeader.superWriteToExtensionEntry(enclosingFrame, symbol, value)) {
                        return value;
                    }
                }

                RFrameHeader.superWriteToTopLevel(symbol, value);
                return value;
            }
        };
    }
}
