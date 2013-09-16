package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.runtime.*;

public abstract class AbstractCall extends BaseR {

    private static final boolean MATERIALIZE_ON_FUNCTION_CALL = true;

    protected final RSymbol[] argNames;
    @Children protected final RNode[] argExprs;
    protected final int[] dotsArgs; // FIXME: move to FunctionCall?


    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs, int[] dotsArgs) {
        super(orig);
        this.argNames = argNames;
        this.argExprs = adoptChildren(argsExprs);
        this.dotsArgs = dotsArgs;
    }

    public AbstractCall(ASTNode orig, RSymbol[] argNames, RNode[] argsExprs) {
        this(orig, argNames, argsExprs, null);
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

    public static class DotsInfo {
        RSymbol[] names; // names of arguments to be passed into ... parameter
    }

    // argument positions are 1-based (!)
    protected final int[] computePositions(RSymbol[] paramNames, DotsInfo dotsInfo, int dotsIndex) {

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
        int nDotsArgs = 0;

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
                argPositions[i] = -1; // part of three dots
                i++;
                nDotsArgs++;
                for (;;) {
                    if (i == nArgs) {
                        break outer;
                    }
                    while(argPositions[i] != 0) {
                        i++;
                        if (i == nArgs) {
                            break outer;
                        }
                    }
                    argPositions[i] = -1;
                    i++;
                    nDotsArgs++;
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

        if (dotsIndex >= 0) {  // need to create an empty "RDots" in case no arguments matched ...
            RSymbol[] dnames = new RSymbol[nDotsArgs];
            int di = 0;
            for (i = 0; i < nArgs; i++) {
                if (argPositions[i] < 0) {
                    dnames[di++] = argNames[i];
                }
            }
            dotsInfo.names = dnames;
        } else {
            dotsInfo.names = null;
        }

        return argPositions;
    }

    // used when the call is passing "..." (note, ... can be passed more than once)
    protected final void placeDotsArgs(Frame callerFrame, Frame newFrame, RSymbol[] paramNames) {

        int nextDots = dotsArgs[0];
        RDots dotsArg = (RDots) argExprs[nextDots].execute(callerFrame);
        RSymbol[] dotsArgNames = dotsArg.names();
        Object[] dotsArgValues = dotsArg.values();
        int dotsArgLen = dotsArgNames.length;
        int ndots = dotsArgs.length;
        int nArgs =  argExprs.length + ndots * (dotsArgLen - 1);
        int nParams = paramNames.length;

        RSymbol[] actualArgNames = new RSymbol[nArgs];
        Object[] actualArgValues = new Object[nArgs];
        int dotsIndex = 0;

        // copy all arguments from dots into a single arguments array, which makes the matching easier
        // FIXME: could do without this, but the iteration over arguments would be more difficult, and perhaps
        // the whole call won't then be faster, anyway
        for (int i = 0, j = 0; j < nArgs; i++) {
            if (i == nextDots) {
                for (int k = 0; k < dotsArgLen; k++, j++) {
                    actualArgNames[j] = dotsArgNames[k];
                    actualArgValues[j] = dotsArgValues[k]; // FIXME: GNU-R would create a recursive promise here
                }
                dotsIndex++;
                if (dotsIndex < ndots) {
                    nextDots = dotsIndex;
                }
            } else {
                actualArgNames[j] = argNames[i];
                actualArgValues[j] = promiseForArgument(callerFrame, i);
                j++;
            }
        }

        boolean[] usedArgs = new boolean[nArgs];

        for (int i = 0; i < nArgs; i++) { // matching by name
            RSymbol argName = actualArgNames[i];
            if (argName == null) {
                continue;
            }
            for (int j = 0; j < nParams; j++) {
                if (argName == paramNames[j]) {
                    if (newFrame.get(j) != null) {
                        throw RError.getFormalMatchedMultiple(ast, argName.name());
                    }
                    newFrame.set(j, actualArgValues[i]);
                    usedArgs[i] = true;
                }
            }
        }

        // do we need to do partial matching at all?
        boolean hasUnmatchedNamedArgs = false;
        for (int i = 0; i < nArgs; i++) {
            if (actualArgNames[i] != null && !usedArgs[i]) {
                hasUnmatchedNamedArgs = true;
                break;
            }
        }

        if (hasUnmatchedNamedArgs) { // partial matching
            boolean[] argMatchedViaPartialMatching = new boolean[nArgs];
            for (int j = 0; j < nParams; j++) {
                if (newFrame.get(j) != null) {
                    continue;
                }
                RSymbol paramName = paramNames[j];
                if (paramName == RSymbol.THREE_DOTS_SYMBOL) {
                    // only exact matches after ...
                    // NOTE: GNU-R continues in the search, but I don't see why - exact matching would have established such matches already
                    break;
                }

                for (int i = 0; i < nArgs; i++) {
                    RSymbol argName = actualArgNames[i];
                    if (argName == null) {
                        continue;
                    }
                    if (argMatchedViaPartialMatching[i]) {
                        if (paramName.startsWith(argName)) {
                            throw RError.getArgumentMatchesMultiple(ast, i + 1);
                        }
                    } else if (!usedArgs[i] && paramName.startsWith(argName)) {
                        if (newFrame.get(j) != null) {
                            throw RError.getFormalMatchedMultiple(ast, paramName.name());
                        }
                        newFrame.set(j, actualArgValues[i]);
                        usedArgs[i] = true;
                        argMatchedViaPartialMatching[i] = true;
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
                        reportUnusedArgsError(usedArgs, actualArgValues, actualArgNames);
                    }
                    break outer;
                }
                if (!usedArgs[i]) {
                    break;
                }
                i++;
            }
            // i now points to unused argument

            for(;;) {
                if (j == nParams) {
                    reportUnusedArgsError(usedArgs, actualArgValues, actualArgNames);
                }
                if (newFrame.get(j) == null) {
                    break;
                }
                j++;
            }
            // j now points to unmatched parameter

            RSymbol paramName = paramNames[j];
            if (paramName == RSymbol.THREE_DOTS_SYMBOL) { // handle three dots in parameters

                int nToDots = 0;
                for(int ii = i; ii < nArgs; ii++) {
                    if (!usedArgs[ii]) {
                        nToDots++;
                    }
                }

                RSymbol[] dnames = new RSymbol[nToDots];
                Object[] dvalues = new Object[nToDots];

                for (int di = 0; i < nArgs; i++) {
                    if (!usedArgs[i]) {
                        dnames[di] = actualArgNames[i];
                        dvalues[di] = actualArgValues[i];
                        di++;
                        usedArgs[i] = true;
                    }
                }

                newFrame.set(j, new RDots(dnames, dvalues));
                continue;
            }
            // j now points to unmatched parameter, which is not the three dots

            if (actualArgNames[i] == null) {
                newFrame.set(j, actualArgValues[i]);
                usedArgs[i] = true;
                i++;
                j++;
            } else {
                i++;
                hasUnusedArgsWithNames = true;
            }
        }
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

    private int reportUnusedArgsError(boolean[] usedArgs, Object[] actualArgValues, RSymbol[] actualArgNames) {
        StringBuilder str = new StringBuilder();
        boolean first = true;
        for(int i = 0; i < usedArgs.length; i++) {
            if (!usedArgs[i]) {
                if (!first) {
                    str.append(", ");
                } else {
                    first = false;
                }
                RSymbol argName = actualArgNames[i];
                if (argName != null) {
                    str.append(argName);
                    str.append(" = ");
                }
                Object argValue = actualArgValues[i];
                if (argValue != null) {
                    RNode argExpr;
                    if (argValue instanceof RPromise) {
                        argExpr = ((RPromise) argValue).expression();
                        str.append(PrettyPrinter.prettyPrint(argExpr.getAST()));
                    } else if (argValue instanceof RAny) {
                        str.append( ((RAny) argValue).pretty());
                    }
                }
            }
        }
        throw RError.getUnusedArgument(ast, str.toString());
    }


    protected final int[] computePositions(final RFunction func, DotsInfo dotsInfo) {
        return computePositions(func.paramNames(), dotsInfo, func.dotsIndex());
    }

    protected final Object promiseForArgument(Frame callerFrame, int argIndex) {
        RNode argExpr = argExprs[argIndex];
        if (argExpr != null) {
            if (FunctionCall.PROMISES) {
                return RPromise.createNormal(argExpr, callerFrame);
            } else {
                Object argV = argExpr.execute(callerFrame);
                if (MATERIALIZE_ON_FUNCTION_CALL) {
                    if (argV instanceof View) {
                        argV = ((View) argV).materialize();
                    }
                }
                RAny rany = Utils.cast(argV);
                rany.ref();
                return rany;
            }
        } else {
            return null;
        }
    }

    // argPositions
    //   1-based, giving parameter index for argument index
    //   == -1 for arguments to be placed into ...
    // dots
    //   dots.names == null when there are no ... in parameters
    //   otherwise, names of symbols that will appear in ...
    @ExplodeLoop protected final void placeArgs(Frame callerFrame, Frame newFrame, int[] argPositions, DotsInfo dotsInfo, int dotsIndex, int nParams) {

        int i;
        RSymbol[] dnames = dotsInfo.names;
        if (dotsIndex == -1) {  // FIXME: turn into node-rewriting ?
            // no dots symbol in target
            for (i = 0; i < argExprs.length; i++) {
                int p = argPositions[i] - 1;
                assert Utils.check(p >= 0);
                newFrame.set(p, promiseForArgument(callerFrame, i));
            }
        } else {
            Object[] dargs = new Object[dnames.length];
            int di = 0;
            for (i = 0; i < argExprs.length; i++) {
                int p = argPositions[i] - 1;
                if (p >= 0) {
                    newFrame.set(p,promiseForArgument(callerFrame, i));
                } else {
                    dargs[di++] =  promiseForArgument(callerFrame, i);
                }
            }
            newFrame.set(dotsIndex, new RDots(dnames, dargs));

        }
    }
}