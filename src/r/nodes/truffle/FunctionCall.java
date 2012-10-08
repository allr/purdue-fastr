package r.nodes.truffle;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.nodes.*;

public abstract class FunctionCall extends AbstractCall {

    final RNode closureExpr;

    private FunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast, argNames, argExprs);
        this.closureExpr = updateParent(closureExpr);
    }

    public static CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
            RNode fexp = r.nodes.truffle.ReadVariable.getUninitialized(call, fcall.getName()); // FIXME: ReadVariable CANNOT be used ! Function lookup are != from variable lookups

            return getFunctionCall(fcall, fexp, names, exprs);
        }

        public FunctionCall getFunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
            for (int i = 0; i < argNames.length; i++) { // FIXME this test is kind of inefficient, part of this job can be done by splitArguments
                if (argNames[i] != null || argExprs[i] == null) { // FIXME this test is a bit too strong, but I need a special node when there are defaults args
                    return getCachedGenericFunctionCall(ast, closureExpr, argNames, argExprs);
                }
            }
            return getSimpleFunctionCall(ast, closureExpr, argNames, argExprs);
        }

        // This class is more or less useless since the cached version is always as efficient (or at least one test + affectation for nothing which is meaningless in this case)
        @SuppressWarnings("unused")
        public FunctionCall getGenericFunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
            return new FunctionCall(ast, closureExpr, argNames, argExprs) {

                @Override
                protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                    RSymbol[] names = new RSymbol[argExprs.length];
                    int[] positions = computePositions(context, func, names);
                    return placeArgs(context, callerFrame, positions, names, func.nparams());
                }
            };
        }

        public FunctionCall getCachedGenericFunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
            return new FunctionCall(ast, closureExpr, argNames, argExprs) {

                RFunction lastCall;
                RSymbol[] names;
                int[] positions;

                @Override
                protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                    if (func != lastCall) {
                        lastCall = func;
                        names = new RSymbol[argExprs.length];
                        positions = computePositions(context, func, names);
                    }
                    return placeArgs(context, callerFrame, positions, names, func.nparams());

                }
            };
        }

        public FunctionCall getSimpleFunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
            return new FunctionCall(ast, closureExpr, argNames, argExprs) {

                @Override
                protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                    return placeArgs(context, callerFrame, func.nparams());
                }
            };
        }
    };

    @Override
    public final Object execute(RContext context, Frame callerFrame) {
        RClosure tgt = (RClosure) closureExpr.execute(context, callerFrame);
        Object[] argValues = matchParams(context, tgt.function(), tgt.environment(), callerFrame);
        return tgt.call(context, argValues);
    }

    protected abstract Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame);

    // providedArgNames is an output parameter, should be an array of argExprs.length nulls before the call
    // FIXME: what do we need the parameter for?

    protected final int[] computePositions(final RContext context, final RFunction func, RSymbol[] usedArgNames) {
        return computePositions(context, func.paramNames(), usedArgNames);
    }

    protected final int[] computePositions(final RContext context, RSymbol[] paramNames, RSymbol[] usedArgNames) {

        int nArgs = argExprs.length;
        int nParams = paramNames.length;

        boolean[] provided = new boolean[nParams]; // Alloc in stack if we are lucky !

        boolean has3dots = false;
        int[] positions = new int[has3dots ? (nArgs + nParams) : nParams]; // The right size is unknown in presence of ``...'' !

        for (int i = 0; i < nArgs; i++) { // matching by name
            if (argNames[i] != null) {
                for (int j = 0; j < nParams; j++) {
                    if (argNames[i] == paramNames[j]) {
                        usedArgNames[i] = argNames[i];
                        positions[i] = j;
                        provided[j] = true;
                    }
                }
            }
        }

        int nextParam = 0;
        for (int i = 0; i < nArgs; i++) { // matching by position
            if (usedArgNames[i] == null) {
                while (nextParam < nParams && provided[nextParam]) {
                    nextParam++;
                }
                if (nextParam == nParams) {
                    // TODO either error or ``...''
                    context.error(getAST(), "unused argument(s) (" + argExprs[i].getAST() + ")");
                }
                if (argExprs[i] != null) {
                    usedArgNames[i] = paramNames[nextParam]; // This is for now useless but needed for ``...''
                    positions[i] = nextParam;
                    provided[nextParam] = true;
                } else {
                    nextParam++;
                }
            }
        }

     // FIXME ??? - what is this? - why more positions than nArgs?
        int j = nArgs;
        while (j < nParams) {
            if (!provided[nextParam]) {
                positions[j++] = nextParam;
            }
            nextParam++;
        }

        return positions;
    }


    /**
     * Displace args provided at the good position in the frame.
     *
     * @param context The global context (needed for warning ... and for know for evaluate)
     * @param callerFrame The frame to evaluate exprs (it's the last argument, since with promises, it should be removed or at least changed)
     * @param positions Where arguments need to be displaced (-1 means ``...'')
     * @param names Names of extra arguments (...).
     */
    @ExplodeLoop
    protected final Object[] placeArgs(RContext context, Frame callerFrame, int[] positions, RSymbol[] names, int nparams) {

        Object[] argValues = new Object[nparams];
        int i;
        for (i = 0; i < argExprs.length; i++) {
            int p = positions[i];
            if (p >= 0) {
                RNode v = argExprs[i];
                if (v != null) {
                    argValues[p] = argExprs[i].execute(context, callerFrame); // FIXME this is wrong ! We have to build a promise at this point and not evaluate
                }
            } else {
                // TODO add to ``...''
                // Note that names[i] contains a key if needed
                context.warning(argExprs[i].getAST(), "need to be put in ``...'', which is NYI");
            }
        }
        return argValues;
    }

    @ExplodeLoop
    protected final Object[] placeArgs(RContext context, Frame callerFrame, int nparams) {
        Object[] argValues = new Object[nparams];
        int i = 0;

        for (; i < argExprs.length; i++) {
            if (i < nparams) {
                argValues[i] = argExprs[i].execute(context, callerFrame); // FIXME this is wrong ! We have to build a promise at this point and not evaluate
            } else {
                // TODO either error or ``...''
                context.error(argExprs[i].getAST(), "unused argument(s)");
            }
        }
        return argValues;
    }
}
