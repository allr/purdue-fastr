package r.data;

import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.FunctionCall;
import r.runtime.*;

public final class RPromise {
    private final RNode expression; // must be a root node
    private final Frame frame; // FIXME: can we merge the frame and value fields?
    private RAny value;
    private int bits;

    private static final int FORCE_DIRTY_MASK = 1 << 0;
    private static final int MISSING_DIRTY_MASK = 1 << 1;
    private static final int MISSING_MASK = 1 << 2 | 1 << 3;
    private static final int MISSING_SHIFT = 2;

    private static final int MISSING_BITS_DEFAULT = 2 << MISSING_SHIFT;
    private static final int MISSING_BITS_MISSING = 1 << MISSING_SHIFT; // NOTE: we use different encoding of missing states from GNU-R

    private RPromise(RNode expression, Frame frame, int bits) {
        this.expression = expression; // root node
        this.frame = frame;
        this.bits = bits;
    }

    public static RPromise createNormal(RNode expression, Frame frame) {
        return new RPromise(expression, frame, 0);
    }

    public static RPromise createDefault(RNode expression, Frame frame) {
        return new RPromise(expression, frame, MISSING_BITS_DEFAULT);
    }

    public static RPromise createMissing(final RSymbol argName, Frame frame) {

        final ASTNode errorAST = frame == null ? null : frame.function().getSource();
        // FIXME: could cache these nodes per function/argument (though missing is probably rare)
        RNode errorExpression = new BaseR(new r.nodes.ast.Constant(RSymbol.EMPTY_SYMBOL)) {
            // FIXME: don't need bits, could detect missing arg using instanceof

            @Override
            public Object execute(Frame dummy) {
                throw RError.getArgumentMissing(errorAST, argName.pretty());
            }

        };

        return new RPromise(errorExpression, frame, MISSING_BITS_MISSING);
    }

    public Object forceOrGet() {
        if (value == null) {
            try {
                if (!markForceDirty()) {
                    throw RError.getPromiseCycle(expression.getAST()); // TODO: use the correct AST - probably the current context
                }
                value = (RAny) expression.execute(frame);
                if (AbstractCall.MATERIALIZE_FUNCTION_ARGUMENTS && value instanceof View) {
                    value = ((View) value).materialize();
                }
            } finally {
                markForceClean();
            }
            value.ref();
        }
        return value;
    }

    public static Object force(Object o) {
        if (FunctionCall.PROMISES && o instanceof RPromise) {
            return ((RPromise) o).forceOrGet();
        } else {
            return o;
        }
    }

    public boolean markForceDirty() {
        boolean old = (bits & FORCE_DIRTY_MASK) != 0;
        bits |= FORCE_DIRTY_MASK;
        return !old;
    }

    public void markForceClean() {
        bits &= ~FORCE_DIRTY_MASK;
    }

    public boolean markMissingDirty() {
        boolean old = (bits & MISSING_DIRTY_MASK) != 0;
        bits |= MISSING_DIRTY_MASK;
        return !old;
    }

    public void markMissingClean() {
        bits &= ~MISSING_DIRTY_MASK;
    }

    public boolean isDefault() {
        return (bits & MISSING_MASK) == MISSING_BITS_DEFAULT;
    }

    public boolean isMissing() {
        return (bits & MISSING_MASK) == MISSING_BITS_MISSING;
    }

    public RNode expression() {
        return expression;
    }

    public Frame frame() {
        return frame;
    }
}
