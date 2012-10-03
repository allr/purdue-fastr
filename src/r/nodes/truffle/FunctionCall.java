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
                    return displaceArgs(context, callerFrame, positions, names, func.nparams());
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
                    return displaceArgs(context, callerFrame, positions, names, func.nparams());

                }
            };
        }

        public FunctionCall getSimpleFunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
            return new FunctionCall(ast, closureExpr, argNames, argExprs) {

                @Override
                protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                    return displaceArgs(context, callerFrame, func.nparams());
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

    protected final int[] computePositions(final RContext context, final RFunction func, RSymbol[] names) {
        RSymbol[] defaultsNames = func.paramNames();

        int nbArgs = argExprs.length;
        int nbFormals = defaultsNames.length;

        boolean[] used = new boolean[nbFormals]; // Alloc in stack if we are lucky !

        boolean has3dots = false;
        int[] positions = new int[has3dots ? (nbArgs + nbFormals) : nbFormals]; // The right size is unknown in presence of ``...'' !

        for (int i = 0; i < nbArgs; i++) {
            if (argNames[i] != null) {
                for (int j = 0; j < nbFormals; j++) {
                    if (argNames[i] == defaultsNames[j]) {
                        names[i] = argNames[i];
                        positions[i] = j;
                        used[j] = true;
                    }
                }
            }
        }

        int nextParam = 0;
        for (int i = 0; i < nbArgs; i++) {
            if (names[i] == null) {
                while (nextParam < nbFormals && used[nextParam]) {
                    nextParam++;
                }
                if (nextParam == nbFormals) {
                    // TODO either error or ``...''
                    context.error(getAST(), "unused argument(s) (" + argExprs[i].getAST() + ")");
                }
                if (argExprs[i] != null) {
                    names[i] = defaultsNames[nextParam]; // This is for now useless but needed for ``...''
                    positions[i] = nextParam;
                    used[nextParam] = true;
                } else {
                    nextParam++;
                }
            }
        }

        int j = nbArgs;
        while (j < nbFormals) {
            if (!used[nextParam]) {
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
    protected final Object[] displaceArgs(RContext context, Frame callerFrame, int[] positions, RSymbol[] names, int nparams) {
        //int dfltsArgs = positions.length;

        Object[] argValues = new Object[nparams];
        int i;
        for (i = 0; i < argExprs.length; i++) {
            int p = positions[i];
            if (p >= 0) {
                RNode v = argExprs[i];
                if (v != null) {
                    argValues[p] = argExprs[i].execute(context, callerFrame); // FIXME this is wrong ! We have to build a promise at this point and not evaluate
                    // FIXME and it's even worst since it's not the good frame at all !
                } /*   this is now done in executeHelper (at the callee)
                else {
                    v = fdefs[positions[i]];
                    if (v != null) { // TODO insert special value for missing
                        // FIXME: can't execute now because the callee Frame does not yet exist
                        argValues[positions[i]] = null;  fdefs[positions[i]].execute(context, calleeFrame));
                    }

                } */
            } else {
                // TODO add to ``...''
                // Note that names[i] contains a key if needed
                context.warning(argExprs[i].getAST(), "need to be put in ``...'', which is NYI");
            }
        }

        /* this is now done in executeHelper (at the callee)
        for (; i < dfltsArgs; i++) { // For now we populate frames with prom/value.
            // I'm not found of this, there should be a way to only create/evaluate when needed.
            // Thus there could be a bug if a default values depends on another
            RNode v = fdefs[positions[i]];
            if (v != null) { // TODO insert special value for missing
                // FIXME: can't execute now because the callee Frame does not yet exist
                argValues[positions[i]] = null; // fdefs[positions[i]].execute(context, calleeFrame));
            }
        }
        */
        return argValues;
    }

    @ExplodeLoop
    protected final Object[] displaceArgs(RContext context, Frame callerFrame, int nparams) {
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
        /* this is now done in executeHelper (at the callee)
        for (; i < fdefs.length; i++) {
            RNode v = fdefs[i];
            if (v != null) { // TODO insert special value for missing
                // FIXME: can't execute now because the callee Frame does not yet exist
                argValues[i] = null; // fdefs[i].execute(context, calleeFrame));
            }
        }
        */
        return argValues;
    }
}
