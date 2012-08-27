package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.*;

public abstract class WriteVariable extends BaseRNode {

    // TODO: All BaseRNode are useless EXCEPT for uninitialized version (since truffle keep track of the original)
    final RSymbol symbol;
    RNode expr;

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
            public Object execute(RContext context, RFrame frame) {
                WriteVariable node;
                String reason;

                if (frame == null) {
                    node = getWriteTopLevel(getAST(), symbol, expr);
                    reason = "installWriteTopLevelNode";
                } else {
                    int pos = frame.getPositionInWS(symbol);
                    if (pos >= 0) {
                        node = getWriteLocal(getAST(), symbol, pos, expr);
                        reason = "installWriteLocalNode";
                    } else {
                        node = getWriteExtension(getAST(), symbol, expr); // TODO this should be removed or at least asserted false !
                        reason = "installWriteExtensionNode (!!! REMOVE when write sets are implemented)";
                        Utils.check(false, "TODO: implement wset and remove this condition");
                    }
                }
                return replace(node, reason).execute(context, frame);
            }
        };
    }

    public static WriteVariable getWriteLocal(ASTNode orig, RSymbol sym, final int pos, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Stable
            int position = pos;

            @Override
            public Object execute(RContext context, RFrame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                frame.writeAt(position, val);
                return val;
            }
        };
    }

    public static WriteVariable getWriteTopLevel(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override
            public Object execute(RContext context, RFrame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                RFrame.writeInTopLevel(symbol, val);
                return val;
            }
        };
    }

    public static WriteVariable getWriteExtension(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override
            public Object execute(RContext context, RFrame frame) {
                RAny val = Utils.cast(expr.execute(context, frame));
                frame.writeInExtension(symbol, val);
                return val;
            }
        };
    }
}
