package r.builtins;

import java.util.*;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

public abstract class BuiltIn extends AbstractCall {

    public BuiltIn(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
        super(orig, argNames, argExprs);
    }

    abstract static class BuiltIn0 extends BuiltIn {

        public BuiltIn0(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public final Object execute(Frame frame) {
            return doBuiltIn(frame);
        }

        public abstract RAny doBuiltIn(Frame frame);

        @Override
        public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame);
        }
    }

    abstract static class BuiltIn1 extends BuiltIn {

        public BuiltIn1(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public final Object execute(Frame frame) {
            return doBuiltIn(frame, (RAny) argExprs[0].execute(frame));
        }

        public abstract RAny doBuiltIn(Frame frame, RAny arg);

        @Override
        public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame, params[0]);
        }
    }

    abstract static class BuiltIn2 extends BuiltIn {

        public BuiltIn2(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public final Object execute(Frame frame) {
            return doBuiltIn(frame, (RAny) argExprs[0].execute(frame), (RAny) argExprs[1].execute(frame));
        }

        public abstract RAny doBuiltIn(Frame frame, RAny arg0, RAny arg1);

        @Override
        public final RAny doBuiltIn(Frame frame, RAny[] params) {
            return doBuiltIn(frame, params[0], params[1]);
        }
    }

    public static void missingArg(ASTNode ast, String paramName) {
        throw RError.getGenericError(ast, String.format(RError.ARGUMENT_MISSING, paramName));
    }

    public static void ensureArgName(ASTNode ast, String expectedName, RSymbol actualName) {
        if (actualName == null) {
            return;
        }
        RSymbol expected = RSymbol.getSymbol(expectedName);
        if (actualName != expected) {
            throw RError.getGenericError(ast, String.format(RError.ARGUMENT_NOT_MATCH, actualName.pretty(), expectedName));
        }
    }

    public static RAny getConstantValue(RNode node) {
        if (node.getAST() instanceof r.nodes.Constant) {
            return (RAny) node.execute(null);
        }
        return null;
    }

    public static boolean isLogicalConstant(RNode node, int cvalue) {
        RAny value = getConstantValue(node);
        if (value != null && value instanceof RLogical) {
            RLogical lv = (RLogical) value;
            if (lv.size() == 1) {
                return lv.getLogical(0) == cvalue;
            }
        }
        return false;
    }

    public static boolean isNumericConstant(RNode node, double cvalue) {
        RAny value = getConstantValue(node);
        if (value != null) {
            if (value instanceof RDouble || value instanceof RInt || value instanceof RLogical) {
                RDouble dv = value.asDouble();
                if (dv.size() == 1) {
                    return dv.getDouble(0) == cvalue;
                }
            }
        }
        return false;
    }

    abstract static class NamedArgsBuiltIn extends BuiltIn {
        final int[] argPositions; // maps arguments to positions in parameters array
        final int nParams; // number of _named_ parameters (not ... symbol)

        public NamedArgsBuiltIn(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, int nParams, AnalyzedArguments aargs) {
            super(orig, argNames, argExprs);
            this.argPositions = aargs.argPositions;
            this.nParams = nParams;
        }

        @ExplodeLoop
        private RAny[] evalArgs(Frame frame) {
            int len = argExprs.length;
            RAny[] params = new RAny[nParams];
            for (int i = 0; i < len; i++) {
                RAny val = (RAny) argExprs[i].execute(frame);
                int pos = argPositions[i];
                params[pos] = val;
            }
            return params;
        }

        public static class AnalyzedArguments {
            public boolean[] providedParams;
            public int[] argPositions;
            public int[] paramPositions;
            public ArrayList<Integer> unusedArgs;

            AnalyzedArguments(int nParams, int nArgs) {
                providedParams = new boolean[nParams]; // FIXME: could merge with paramPositions
                argPositions = new int[nArgs];
                paramPositions = new int[nParams];
            }
        }

        public static AnalyzedArguments analyzeArguments(RSymbol[] argNames, RNode[] argExprs, String[] paramNames) {
            RSymbol[] sparamNames = new RSymbol[paramNames.length];
            for (int i = 0; i < paramNames.length; i++) {
                sparamNames[i] = RSymbol.getSymbol(paramNames[i]);
            }
            return analyzeArguments(argNames, argExprs, sparamNames);
        }

        public static AnalyzedArguments analyzeArguments(RSymbol[] argNames, RNode[] argExprs, RSymbol[] paramNames) {

            // argument is the value passed by caller
            // parameter is the callee's placeholder for the value

            int nParams = paramNames.length;

            int nArgs = argExprs.length;
            AnalyzedArguments a = new AnalyzedArguments(nParams, nArgs);
            boolean[] usedArgs = new boolean[nArgs];

            outerLoop: for (int i = 0; i < nArgs; i++) { // matching by name
                if (argNames[i] != null) {
                    for (int j = 0; j < nParams; j++) {
                        if (argNames[i] == paramNames[j]) {
                            a.argPositions[i] = j;
                            a.paramPositions[j] = i;
                            a.providedParams[j] = true;
                            usedArgs[i] = true;
                            continue outerLoop;
                        }
                    } // reachable even in non-error case - when threeDots is among parameter names or for partial matches
                }
            }

            for (int i = 0; i < nArgs; i++) { // partial matching by name
                if (usedArgs[i] || argNames[i] == null) {
                    continue;
                }
                boolean matchFound = false;
                for (int j = 0; j < nParams; j++) {
                    if (a.providedParams[j]) {
                        continue;
                    }
                    String argStr = argNames[i].pretty();
                    String paramStr = paramNames[j].pretty();
                    if (paramStr.startsWith(argStr)) {
                        // found a match
                        if (matchFound) {
                            throw RError.getGenericError(null,  "Argument " + i + " matches multiple formal arguments.");
                        }

                        a.argPositions[i] = j;
                        a.paramPositions[j] = i;
                        a.providedParams[j] = true;
                        usedArgs[i] = true;
                        matchFound = true;
                    }
                }
            }

            int nextParam = 0;
            for (int i = 0; i < nArgs; i++) { // matching by position
                if (!usedArgs[i]) {
                    while (nextParam < nParams && a.providedParams[nextParam]) {
                        nextParam++;
                    }
                    if (nextParam == nParams) {
                        // FIXME: handle passing of ``...'' objects
                        if (a.unusedArgs == null) {
                            a.unusedArgs = new ArrayList<>();
                        }
                        a.unusedArgs.add(i);
                    } else {
                        if (paramNames[nextParam] == RSymbol.THREE_DOTS_SYMBOL) {
                            /* usedArgs[i] = true; - not needed */
                            a.argPositions[i] = nextParam;
                            a.paramPositions[nextParam] = i; // so record the last argument that was taken by ...
                            continue;
                        }
                        if (argExprs[i] != null) {
                            /* usedArgs[i] = true; - not needed */
                            if (argNames[i] != null) {
                                throw RError.getGenericError(null, "Unknown parameter " + argNames[i].pretty() + " passed to a builtin"); // FIXME: better error message
                            }
                            a.argPositions[i] = nextParam;
                            a.paramPositions[nextParam] = i;
                            a.providedParams[nextParam] = true;
                        } else {
                            nextParam++;
                        }
                    }
                }
            }
            return a;
        }
    }

    @Override
    public Object execute(Frame frame) {
        return doBuiltIn(frame, evalArgs(frame));
    }

    public abstract RAny doBuiltIn(Frame frame, RAny[] params);

    @ExplodeLoop
    private RAny[] evalArgs(Frame frame) {
        int len = argExprs.length;
        RAny[] args = new RAny[len];
        for (int i = 0; i < len; i++) {
            args[i] = (RAny) argExprs[i].execute(frame);
        }
        return args;
    }
}
