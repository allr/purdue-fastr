package r.nodes.truffle;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.nodes.*;

// FIXME: we could get some performance by specializing on whether an update (writing the same value) is likely ; this is so when the assignment is used
// in update operations (vector update, replacement functions) ; we could use unconditional ref in other cases
public abstract class WriteVariable extends BaseR {

    // TODO: All BaseRNode are useless EXCEPT for the uninitialized version (since Truffle keeps track of the original)
    final RSymbol symbol;
    @Child RNode expr;

    private static final boolean DEBUG_W = false;

    private WriteVariable(ASTNode orig, RSymbol symbol, RNode expr) {
        super(orig);
        setExpr(expr);
        this.symbol = symbol;
    }

    @Override public void replace0(RNode o, RNode n) {
        if (expr == o) expr = n;
    }

    public void setExpr(RNode expr) {
        this.expr = adoptChild(expr);
    }

    public RNode getExpr() {
        return expr;
    }

    public static WriteVariable getUninitialized(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override public final Object execute(Frame frame) {

                try {
                    throw new UnexpectedResultException(null);
                } catch (UnexpectedResultException e) {
                    WriteVariable node;
                    String reason;

                    if (frame == null) {
                        node = getWriteTopLevel(getAST(), symbol, expr);
                        reason = "installWriteTopLevelNode";
                    } else {
                        int slot = RFrameHeader.findVariable(frame, symbol);
                        if (slot != -1) {
                            node = getWriteLocal(getAST(), symbol, slot, expr);
                            reason = "installWriteLocalNode";
                        } else {
                            // this is only with reflective access
                            // TODO: remove this? the reflective access should never go through WriteVariable
                            node = getWriteExtension(getAST(), symbol, expr);
                            reason = "installWriteExtensionNode (!!! only reflective access !!!)";
                            Utils.check(false, "unreachable as long as not doing any reflective writes");
                        }
                    }
                    if (DEBUG_W) {
                        Utils.debug("write - " + symbol.pretty() + " uninitialized rewritten: " + reason);
                    }
                    return replace(node, reason).execute(frame);
                }
            }
        };
    }

    public static WriteVariable getWriteLocal(ASTNode orig, RSymbol sym, final int slot, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override public final Object execute(Frame frame) {
                RAny val = Utils.cast(expr.execute(frame));
                RFrameHeader.writeAtCondRef(frame, slot, val);
                if (DEBUG_W) {
                    Utils.debug("write - " + symbol.pretty() + " local-ws, wrote " + val + " (" + val.pretty() + ") to slot " + slot);
                }
                return val;
            }
        };
    }

    public static WriteVariable getWriteTopLevel(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override public final Object execute(Frame frame) {
                RAny val = Utils.cast(expr.execute(frame));
                RFrameHeader.writeToTopLevelCondRef(symbol, val);
                if (DEBUG_W) {
                    Utils.debug("write - " + symbol.pretty() + " toplevel, wrote " + val + " (" + val.pretty() + ")");
                }
                return val;
            }
        };
    }

    public static WriteVariable getWriteExtension(ASTNode orig, RSymbol sym, RNode rhs) {
        return new WriteVariable(orig, sym, rhs) {

            @Override public final Object execute(Frame frame) {
                RAny val = Utils.cast(expr.execute(frame));
                RFrameHeader.writeToExtension(frame, symbol, val);
                return val;
            }
        };
    }
}
