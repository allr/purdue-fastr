package r.nodes.truffle;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.ScalarDoubleImpl;
import r.nodes.*;

import com.oracle.truffle.api.frame.*;

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
                        FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
                        if (slot != null) {
                            node = getWriteLocal(getAST(), symbol, slot, expr);
                            reason = "installWriteLocalNode";
                        } else {
                            // this is only reachable with dynamic invocation (e.g. through eval)
                            node = getWriteExtension(getAST(), symbol, expr);
                            reason = "installWriteExtensionNode";
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

    // TRUFFLE : Writing scalar double variables to double typed frame slots is now supported by this code for local
    //           variables. This requires multiple classes for writing local variables, which all follow after the
    //           method.
    public static WriteVariable getWriteLocal(ASTNode orig, RSymbol sym, final FrameSlot slot, RNode rhs) {
        return new WriteVariableLocal(orig, sym, rhs, slot);
    }

    public static class WriteVariableLocal extends WriteVariable {

        final FrameSlot slot;

        private WriteVariableLocal(ASTNode orig, RSymbol symbol, RNode expr, FrameSlot slot) {
            super(orig, symbol, expr);
            this.slot = slot;
        }

        public WriteVariableLocal(WriteVariableLocal other) {
            super(other.ast, other.symbol, other.expr);
            this.slot = other.slot;
        }

        @Override
        public Object execute(Frame frame) {
            Object value = expr.execute(frame);
            if (value instanceof Double) {
                CompilerDirectives.transferToInterpreter();
                return replace(new WriteVariableLocalDouble(this)).execute(frame, value);
            } else if (value instanceof ScalarDoubleImpl) {
                CompilerDirectives.transferToInterpreter();
                return replace(new WriteVariableLocalScalarDouble(this)).execute(frame, value);
            } else {
                CompilerDirectives.transferToInterpreter();
                return replace(new WriteVariableLocalObject(this)).execute(frame, Utils.cast(value, RAny.class));
            }
        }
    }

    public static class WriteVariableLocalObject extends WriteVariableLocal {

        public WriteVariableLocalObject(WriteVariableLocal other) {
            super(other);
            slot.setKind(FrameSlotKind.Object);
        }

        @Override
        public Object execute(Frame frame) {
            RAny value = Utils.cast(expr.execute(frame));
            return execute(frame, value);
        }

        public Object execute(Frame frame, RAny value) {
            RFrameHeader.writeAtCondRef(frame, slot, value);
            if (DEBUG_W) {
                Utils.debug("write - " + symbol.pretty() + " local-ws, wrote " + value + " (" + value.pretty() + ") to slot " + slot);
            }
            return value;
        }
    }

    public static class WriteVariableLocalDouble extends WriteVariableLocal {

        public WriteVariableLocalDouble(WriteVariableLocal other) {
            super(other);
            slot.setKind(FrameSlotKind.Double);
        }

        @Override
        public Object execute(Frame frame) {
            Object value = expr.execute(frame);
            return execute(frame, value);
        }

        public Object execute(Frame frame, Object value) {
            if (value instanceof Double) {
                RFrameHeader.writeDouble(frame, slot, (Double) value);
                return value;
            } else {
                CompilerDirectives.transferToInterpreter();
                return replace(new WriteVariableLocalObject(this)).execute(frame, Utils.cast(value, RAny.class));
            }
        }
    }
    public static class WriteVariableLocalScalarDouble extends WriteVariableLocal {

        public WriteVariableLocalScalarDouble(WriteVariableLocal other) {
            super(other);
            slot.setKind(FrameSlotKind.Double);
        }

        @Override
        public Object execute(Frame frame) {
            Object value = expr.execute(frame);
            return execute(frame, value);
        }

        public Object execute(Frame frame, Object value) {
            if (value instanceof ScalarDoubleImpl) {
                RFrameHeader.writeDouble(frame, slot, ((ScalarDoubleImpl)value).getDouble());
                return value;
            } else {
                CompilerDirectives.transferToInterpreter();
                return replace(new WriteVariableLocalObject(this)).execute(frame, Utils.cast(value, RAny.class));
            }
        }
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
