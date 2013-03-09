package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

/** The base class for builtin functions. */
public abstract class BuiltIn extends AbstractCall {

    public BuiltIn(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
        super(orig, argNames, argExprs);
    }

    /** Builtin functions with no arguments. */
    abstract static class BuiltIn0 extends BuiltIn {

        public BuiltIn0(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
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
    abstract static class BuiltIn1 extends BuiltIn {

        public BuiltIn1(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
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
    abstract static class BuiltIn2 extends BuiltIn {

        public BuiltIn2(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
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

    /** Report a missing argument. */
    public static void missingArg(ASTNode ast, String paramName) {
        throw RError.getGenericError(ast, String.format(RError.ARGUMENT_MISSING, paramName));
    }

    /** Check that the argument provided has the right name else throw an error. */
    public static void ensureArgName(ASTNode ast, String expectedName, RSymbol actualName) {
        if (actualName == null) { return; }
        RSymbol expected = RSymbol.getSymbol(expectedName);
        if (actualName != expected) { throw RError.getGenericError(ast, String.format(RError.ARGUMENT_NOT_MATCH, actualName.pretty(), expectedName)); }
    }

    /** Return a constant or the Java null. */
    public static RAny getConstantValue(RNode node) {
        return node.getAST() instanceof r.nodes.Constant ? (RAny) node.execute(null) : null;
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
        if (value != null) { return false; }
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
            args[i] = (RAny) argExprs[i].execute(frame);
        }
        return args;
    }
}
