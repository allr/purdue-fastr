package r.builtins;

import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

import com.oracle.truffle.api.nodes.*;

/** The base class for builtin functions. */
public abstract class Builtin extends AbstractCall {

    public Builtin(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
        super(orig, argNames, argExprs);
    }

    /** Builtin functions with no arguments. */
    abstract static class Builtin0 extends Builtin {

        public Builtin0(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override public final Object execute(Frame frame) {
            return doBuiltIn(frame);
        }

        public abstract RAny doBuiltIn(Frame frame);

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame);
        }
    }

    /** Builtin functions of one argument. */
    abstract static class Builtin1 extends Builtin {

        public Builtin1(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override public final Object execute(Frame frame) {
            return doBuiltIn(frame, (RAny) argExprs[0].execute(frame));
        }

        public abstract RAny doBuiltIn(Frame frame, RAny arg);

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame, params[0]);
        }
    }

    /** Builtin functions of two arguments. */
    abstract static class Builtin2 extends Builtin {

        public Builtin2(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override public final Object execute(Frame frame) {
            return doBuiltIn(frame, (RAny) argExprs[0].execute(frame), (RAny) argExprs[1].execute(frame));
        }

        public abstract RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1);

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame, params[0], params[1]);
        }
    }

    /** Return a constant or the Java null. */
    public static RAny getConstantValue(RNode node) {
        return node.getAST() instanceof r.nodes.ast.Constant ? (RAny) node.execute(null) : null;
    }

    /** Is node a logical constant with the value cvalue? */
    public static boolean isLogicalConstant(RNode node, int cvalue) {
        RAny value = getConstantValue(node);
        if (value == null || !(value instanceof RLogical)) { return false; }
        RLogical lv = (RLogical) value;
        return lv.size() == 1 ? lv.getLogical(0) == cvalue : false;
    }

    /** Is node a numeric constant with the value cvalue? */
    public static boolean isNumericConstant(RNode node, double cvalue) {
        RAny value = getConstantValue(node);
        if (value == null) { return false; }
        boolean ok = value instanceof RDouble || value instanceof RInt || value instanceof RLogical;
        if (!ok) { return false; }
        RDouble dv = value.asDouble();
        return dv.size() == 1 ? dv.getDouble(0) == cvalue : false;
    }

    @Override public Object execute(Frame frame) {
        return doBuiltIn(frame, evalArgs(frame));
    }

    public abstract RAny doBuiltIn(Frame frame, RAny[] params);

    @ExplodeLoop private RAny[] evalArgs(Frame frame) {
        int len = argExprs.length;
        RAny[] args = new RAny[len];
        for (int i = 0; i < len; i++) {
            RNode expr = argExprs[i];
            if (expr != null) {
                args[i] = (RAny) expr.execute(frame);
            }
        }
        return args;
    }
}
