package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;

public abstract class AbstractCall extends BaseR {

    private static final boolean MATERIALIZE_ON_FUNCTION_CALL = true;

    protected final RSymbol[] argNames;
    @Children protected final RNode[] argExprs;

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = adoptChildren(argsExprs);
    }

    @Override public String toString() {
        String args = "";
        for (int i = 0; i < argExprs.length; i++) {
            if (argNames != null && argNames[i] != null) {
                args += argNames[i] + "=";
            }
            args += argExprs[i] + ",";
        }
        return this.getClass() + "[" + args + "]";
    }

    // dotsArgNames is an output array to be filled in with names of arguments that will go into ... (but is it needed?)
    // argument positions are 1-based (!)
    protected final int[] computePositions(RSymbol[] paramNames, RSymbol[] dotsArgNames) {

        int nArgs = argExprs.length;
        int nParams = paramNames.length;

        boolean[] providedParams = new boolean[nParams];
        int[] argPositions = new int[nArgs]; // 1-based !!!  (0 means unallocated)

        for (int i = 0; i < nArgs; i++) { // matching by name
            RSymbol argName = argNames[i];
            if (argName == null) {
                continue;
            }
            for (int j = 0; j < nParams; j++) {
                if (argName == paramNames[j]) {
                    if (providedParams[j]) {
                        throw RError.getFormalMatchedMultiple(ast, argName.name());
                    }
                    argPositions[i] = j + 1;
                    providedParams[j] = true;
                }
            }
        }

        // do we need to do partial matching at all?
        boolean hasUnmatchedNamedArgs = false;
        for (int i = 0; i < nArgs; i++) {
            if (argNames[i] != null && argPositions[i] == 0) {
                hasUnmatchedNamedArgs = true;
                break;
            }
        }

        if (hasUnmatchedNamedArgs) { // partial matching
            boolean[] argMatchedViaPatternMatching = new boolean[nArgs];
            for (int j = 0; j < nParams; j++) {
                if (providedParams[j]) {
                    continue;
                }
                RSymbol paramName = paramNames[j];
                if (paramName == RSymbol.THREE_DOTS_SYMBOL) {
                    // only exact matches after ...
                    // NOTE: GNU-R continues in the search, but I don't see why - exact matching would have established such matches already
                    break;
                }

                boolean paramMatched = false;
                for (int i = 0; i < nArgs; i++) {
                    RSymbol argName = argNames[i];
                    if (argName == null) {
                        continue;
                    }
                    if (argMatchedViaPatternMatching[i]) {
                        if (paramName.startsWith(argName)) {
                            throw RError.getArgumentMatchesMultiple(ast, i + 1);
                        }
                    } else if (argPositions[i] == 0 && paramName.startsWith(argName)) {
                        if (paramMatched) {
                            throw RError.getFormalMatchedMultiple(ast, paramName.name());
                        }
                        argPositions[i] = j + 1;
                        providedParams[j] = true;
                        argMatchedViaPatternMatching[i] = true;
                        paramMatched = true;
                    }
                }
            }
        }

        int i = 0; // positional matching
        int j = 0;
        boolean hasUnusedArgsWithNames = false;

        outer: for(;;) {
            for(;;) {
                if (i == nArgs) {
                    if (hasUnusedArgsWithNames) {
                        reportUnusedArgsError(nArgs, argPositions);
                    }
                    break outer;
                }
                if (argPositions[i] == 0) {
                    break;
                }
                i++;
            }
            // i now points to unused argument

            for(;;) {
                if (j == nParams) {
                    reportUnusedArgsError(nArgs, argPositions);
                }
                if (!providedParams[j]) {
                    break;
                }
                j++;
            }
            // j now points to unmatched parameter

            RSymbol paramName = paramNames[j];
            if (paramName == RSymbol.THREE_DOTS_SYMBOL) { // handle three dots in parameters
                for (;;) {
                    argPositions[i] = -1; // part of three dots
                    dotsArgNames[i] = argNames[i]; // ?? really needed ??
                    i++;
                    if (i < nArgs) {
                        while(argPositions[i] != 0) {
                            i++;
                            if (i == nArgs) {
                                break outer;
                            }
                        }
                    }
                } // not reached
            }
            // j now points to unmatched parameter, which is not the three dots

            if (argNames[i] == null) {
                argPositions[i] = j + 1;
                providedParams[j] = true;
                i++;
                j++;
            } else {
                i++;
                hasUnusedArgsWithNames = true;
            }
        }

        return argPositions;
    }

    private int reportUnusedArgsError(int nArgs, int[] argPositions) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for(int i = 0; i < nArgs; i++) {
            if (argPositions[i] == 0) {
                if (!first) {
                    str.append(", ");
                } else {
                    first = false;
                }
                RSymbol argName = argNames[i];
                if (argName != null) {
                    str.append(argName);
                    str.append(" = ");
                }
                RNode argExpr = argExprs[i];
                if (argExpr != null) {
                    str.append(PrettyPrinter.prettyPrint(argExpr.getAST()));
                }
            }
        }
        throw RError.getUnusedArgument(ast, str.toString());
    }

    protected final int[] computePositions(final RFunction func, RSymbol[] usedArgNames) {
        return computePositions(func.paramNames(), usedArgNames);
    }

    /**
     * Displace args provided at the good position in the frame.
     *
     * @param callerFrame
     *            The frame to evaluate exprs (it's the last argument, since with promises, it should be removed or at
     *            least changed)
     * @param argPositions
     *            Where arguments need to be displaced (-2 means ``...''), positions are 1-based
     * @param names
     *            Names of extra arguments (...).
     */
    @ExplodeLoop protected final Object[] placeArgs(Frame callerFrame, int[] argPositions, RSymbol[] names, int nParams) {

        Object[] argValues = new Object[nParams];
        int i;
        for (i = 0; i < argExprs.length; i++) {
            int p = argPositions[i] - 1;
            assert Utils.check(p != -1);
            if (p >= 0) {
                RNode argExpr = argExprs[i];
                if (argExpr != null) {
                    if (FunctionCall.PROMISES) {
                        argValues[p] = RPromise.createNormal(argExpr, callerFrame);
                    } else {
                        Object argV = argExpr.execute(callerFrame);
                        if (MATERIALIZE_ON_FUNCTION_CALL) {
                            if (argV instanceof View) {
                                argV = ((View) argV).materialize();
                            }
                        }
                        argValues[p] = argV;
                    }
                }
            } else {
                // TODO support ``...''
                // Note that names[i] contains a key if needed
                RContext.warning(argExprs[i].getAST(), "need to be put in ``...'', which is NYI");
            }
        }
        return argValues;
    }
}
