package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.*;


// FIXME: we could get some performance by specializing on whether an update (writing the same value) is likely ; this is so when the assignment is used
// in update operations (vector update, replacement functions) ; we could use unconditional ref in other cases
public abstract class WriteVariable extends BaseR {

    // TODO: All BaseRNode are useless EXCEPT for the uninitialized version (since Truffle keeps track of the original)
    final RSymbol symbol;
    @Stable RNode expr;

    private static final boolean DEBUG_W = false;

    private WriteVariable(ASTNode orig, RSymbol symbol, RNode expr) {
        super(orig);
        setExpr(expr);
        this.symbol = symbol;
    }

    public void setExpr(RNode expr) {
        this.expr = updateParent(expr);
    }

    public RNode getExpr() {
        return expr;
    }

    public static WriteVariable getUninitialized(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override
            public final Object execute(RContext context, Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    WriteVariable node;
                    String reason;

                    if (frame == null) {
                        node = getWriteTopLevel(getAST(), symbol, expr);
                        reason = "installWriteTopLevelNode";
                    } else {
                        int pos = RFrame.getPositionInWS(frame, symbol);
                        if (pos >= 0) {
                            node = getWriteLocal(getAST(), symbol, pos, expr);
                            reason = "installWriteLocalNode";
                        } else {
                            // this is only with reflective access
                            // TODO: remove this? the reflective access should never go through WriteVariable
                            node = getWriteExtension(getAST(), symbol, expr);
                            reason = "installWriteExtensionNode (!!! only reflective access !!!)";
                            Utils.check(false, "unreachable as long as not doing any reflective writes");
                        }
                    }
                    if (DEBUG_W) { Utils.debug("write - "+symbol.pretty()+" uninitialized rewritten: "+reason); }
                    return replace(node, reason).execute(context, frame);
                }
            }
        };
    }

    public static WriteVariable getWriteLocal(ASTNode orig, RSymbol sym, final int pos, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Stable
            int position = pos;

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                RFrame.writeAtCondRef(frame, position, val);
                if (DEBUG_W) { Utils.debug("write - "+symbol.pretty()+" local-ws, wrote "+val+" ("+val.pretty()+") to position "+position); }
                return val;
            }
        };
    }

    public static WriteVariable getWriteTopLevel(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                RFrame.writeToTopLevelCondRef(symbol, val);
                if (DEBUG_W) { Utils.debug("write - "+symbol.pretty()+" toplevel, wrote "+val+" ("+val.pretty()+")"); }
                return val;
            }
        };
    }

    public static WriteVariable getWriteExtension(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override
            public final Object execute(RContext context, Frame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                RFrame.writeToExtension(frame, symbol, val);
                return val;
            }
        };
    }
}
