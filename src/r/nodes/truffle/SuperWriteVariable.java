package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.RFunction.*;
import r.nodes.*;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;


public abstract class SuperWriteVariable extends BaseR {

    final RSymbol symbol;
    @Stable RNode expr;

    private SuperWriteVariable(ASTNode ast, RSymbol symbol, RNode expr) {
        super(ast);
        this.symbol = symbol;
        this.expr = updateParent(expr);
    }

    public static SuperWriteVariable getUninitialized(ASTNode orig, RSymbol sym, RNode rhs) {
        return new SuperWriteVariable(orig, sym, rhs) {

            private Object replaceAndExecute(RNode node, String reason, RContext context, Frame frame) {
                replace(node, reason);
                return node.execute(context, frame);
            }

            @Override
            public final Object execute(RContext context, Frame frame) {
                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    Frame parentFrame = frame != null ? RFrame.getParent(frame) : null;

                    if (parentFrame == null) {
                        return replaceAndExecute(WriteVariable.getWriteTopLevel(ast, symbol, expr), "install WriteTopLevel from SuperWriteVariable", context, frame);
                    }

                    int pos = RFrame.getPositionInWS(parentFrame, symbol);
                    if (pos >= 0) {
                        return replaceAndExecute(getWriteViaWriteSet(ast, symbol, expr, pos), "install WriteViaWriteSet from SuperWriteVariable", context, frame);
                    }

                    ReadSetEntry rse = RFrame.getRSEntry(frame, symbol);
                    if (rse == null) {
                        return replaceAndExecute(getWriteToTopLevel(ast, symbol, expr), "install WriteToTopLevel from SuperWriteVariable", context, frame);
                    } else {
                        return replaceAndExecute(getWriteViaReadSet(ast, symbol, expr, rse.frameHops, rse.framePos), "install WriteViaReadSet from SuperWriteVariable", context, frame);
                    }
                }
            }
        };
    }

    public static SuperWriteVariable getWriteViaWriteSet(ASTNode ast, RSymbol symbol, RNode expr, final int pos) {
        return new SuperWriteVariable(ast, symbol, expr) {
            @Override
            public Object execute(RContext context, Frame frame) {
                RAny value = (RAny) expr.execute(context, frame);
                Frame parentFrame = RFrame.getParent(frame);
                RFrame.superWriteViaWriteSet(parentFrame, pos, symbol, value);
                return value;
            }
        };
    }

    public static SuperWriteVariable getWriteViaReadSet(ASTNode ast, RSymbol symbol, RNode expr, final int hops, final int position) {
        return new SuperWriteVariable(ast, symbol, expr) {
            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny value = (RAny) expr.execute(context, frame);
                Frame parentFrame = RFrame.getParent(frame);
                RFrame.superWriteViaReadSet(parentFrame, hops, position, symbol, value);
                return value;
            }
        };
    }

    public static SuperWriteVariable getWriteToTopLevel(ASTNode ast, RSymbol symbol, RNode expr) {
        return new SuperWriteVariable(ast, symbol, expr) {

            int version;

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny value = (RAny) expr.execute(context, frame);
                Frame parentFrame = RFrame.getParent(frame);

                // TODO check if 'version' is enough, I think the good test has to be:
                // if (frame != oldFrame || version != symbol.getVersion()) {
                // (same as ReadVariable)

                if (version != symbol.getVersion()) {
                    if (!RFrame.superWriteToExtension(parentFrame, symbol, value, null)) {
                        version = symbol.getVersion();
                        // oldFrame = frame;
                    }
                }
                RFrame.superWriteToTopLevel(symbol, value);
                return value;
            }
        };
    }
}
