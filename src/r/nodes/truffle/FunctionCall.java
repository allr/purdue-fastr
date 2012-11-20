package r.nodes.truffle;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

public abstract class FunctionCall extends AbstractCall {

    final RNode callableExpr;

    private static final boolean CAN_BYPASS_TRUFFLE = true;
      // this feature is experimental-only, to be correct it would have to implement caching

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast, argNames, argExprs);
        this.callableExpr = updateParent(callableExpr);
    }

    public static FunctionCall getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        for (int i = 0; i < argNames.length; i++) { // FIXME this test is kind of inefficient, part of this job can be done by splitArguments
            if (argNames[i] != null || argExprs[i] == null) { // FIXME this test is a bit too strong, but I need a special node when there are defaults args
                return getCachedGenericFunctionCall(ast, callableExpr, argNames, argExprs);
            }
        }
//        return getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
        return new TrivialFunctionCall(ast, callableExpr, argNames, argExprs);
    }

    // This class is more or less useless since the cached version is always as efficient (or at least one test + affectation for nothing which is meaningless in this case)
    @SuppressWarnings("unused")
    public static FunctionCall getGenericFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        return new FunctionCall(ast, callableExpr, argNames, argExprs) {

            @Override
            protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                RSymbol[] names = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                int[] positions = computePositions(context, func, names);
                return placeArgs(context, callerFrame, positions, names, func.nparams());
            }
        };
    }

    public static FunctionCall getCachedGenericFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        return new FunctionCall(ast, callableExpr, argNames, argExprs) {

            RFunction lastCall;
            RSymbol[] names;
            int[] positions;

            @Override
            protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                if (func != lastCall) {
                    lastCall = func;
                    names = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                    positions = computePositions(context, func, names);
                }
                return placeArgs(context, callerFrame, positions, names, func.nparams());  // fixme: cache also nparams?

            }
        };
    }

    public static class TrivialFunctionCall extends FunctionCall {

        public TrivialFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                boolean trivial = true;
                if (argExprs.length != func.nparams() || argExprs.length > 3) {
                    trivial = false;
                } else {
                    // exclude functions with default parameters
                    // because we preallocate the Object[] array with arguments for now, and default parameters could have a recursive call
                    RNode[] dfl = func.paramValues();
                    for (int i = 0; i < dfl.length; i++) {
                        if (dfl[i] != null) {
                            trivial = false;
                            break;
                        }
                    }
                }
                if (!trivial) {
                    FunctionCall f = getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
                    replace(f, "install SimpleFunctionCall from TrivialFunctionCall");
                    return f.matchParams(context, func, parentFrame, callerFrame);
                }

                if (CAN_BYPASS_TRUFFLE) {
                   // non-truffle version
                   DoCall doCall = null;
                   switch(argExprs.length) {
                       case 0 : doCall = new DoCall() {
                           @Override
                           public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                               return closure.trivialCall(context);
                           }
                       }; break;

                       case 1 : doCall = new DoCall() {
                           @Override
                           public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                               return closure.trivialCall(context, argExprs[0].execute(context, callerFrame));
                           }
                       }; break;

                       case 2 : doCall = new DoCall() {
                           @Override
                           public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                               return closure.trivialCall(context, argExprs[0].execute(context, callerFrame), argExprs[1].execute(context, callerFrame));
                           }

                       }; break;

                       case 3 : doCall = new DoCall() {
                           @Override
                           public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                               return closure.trivialCall(context, argExprs[0].execute(context, callerFrame), argExprs[1].execute(context, callerFrame), argExprs[2].execute(context, callerFrame));
                           }
                       }; break;
                   }
                   FunctionCall f = new CachedNonTruffle(ast, callableExpr, argNames, argExprs, func, doCall);
                   replace(f, "specialize TrivialFunctionCall");
                   return placeArgs(context, callerFrame, argExprs.length);
                } else {
                    // Truffle version
                    FillArgs fillArgs = null;
                    // FIXME: with partial evaluation, these can probably be generated by a simple template, with a "final"
                    //        number of arguments and with explodeloop
                    switch(argExprs.length) {
                        case 0 : fillArgs = new FillArgs() {
                            Object[] args = new Object[0];
                            @Override
                            public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                                return args;
                            }
                        }; break;

                        case 1 : fillArgs = new FillArgs() {
                            Object[] args = new Object[1];
                            @Override
                            public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                                args[0] = argExprs[0].execute(context, callerFrame);
                                return args;
                            }
                        }; break;

                        case 2 : fillArgs = new FillArgs() {
                            Object[] args = new Object[2];
                            @Override
                            public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                                args[0] = argExprs[0].execute(context, callerFrame);
                                args[1] = argExprs[1].execute(context, callerFrame);
                                return args;
                            }
                        }; break;

                        case 3 : fillArgs = new FillArgs() {
                            Object[] args = new Object[3];
                            @Override
                            public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                                args[0] = argExprs[0].execute(context, callerFrame);
                                args[1] = argExprs[1].execute(context, callerFrame);
                                args[2] = argExprs[2].execute(context, callerFrame);
                                return args;
                            }
                        }; break;
                    }
                    FunctionCall f = new CachedTruffle(ast, callableExpr, argNames, argExprs, func, fillArgs);
                    replace(f, "specialize TrivialFunctionCall");
                    return f.matchParams(context, func, parentFrame, callerFrame);
                }
            }
        }

        public abstract static class FillArgs {
            public abstract Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs);
        }

        public static class CachedTruffle extends FunctionCall {
            final RFunction cachedFunction;
            final FillArgs fill;

            public CachedTruffle(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RFunction function, FillArgs fill) {
                super(ast, callableExpr, argNames, argExprs);
                this.cachedFunction = function;
                this.fill = fill;
            }

            @Override
            protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                try {
                    if (func != cachedFunction) {
                        throw new UnexpectedResultException(null);
                    }
                    return fill.fill(context, callerFrame, argExprs);
                } catch (UnexpectedResultException e) {
                    FunctionCall f = getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
                    replace(f, "install SimpleFunctionCall from TrivialFunctionCall");
                    return f.matchParams(context, func, parentFrame, callerFrame);
                }
            }
        }

        public abstract static class DoCall {
            public abstract Object doCall(RClosure closure, RContext context, Frame frame);
        }

        public static class CachedNonTruffle extends FunctionCall {
            final RFunction cachedFunction;
            final DoCall doCall;

            public CachedNonTruffle(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RFunction function, DoCall doCall) {
                super(ast, callableExpr, argNames, argExprs);
                this.cachedFunction = function;
                this.doCall = doCall;
            }

            @Override
            public Object execute(RContext context, Frame callerFrame) {
                RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
                try {
                    if (!(callable instanceof RClosure)) {  // FIXME: could get rid of this check through some node rewriting (trivial call could be done for builtins as well)
                        throw new UnexpectedResultException(null);
                    }
                    RClosure closure = (RClosure) callable;
                    if (closure.function() != cachedFunction) {
                        throw new UnexpectedResultException(null);
                    }
                    return doCall.doCall(closure, context, callerFrame);
                } catch (UnexpectedResultException e) {
                    if (callable instanceof RClosure) {
                        RClosure closure = (RClosure) callable;
                        FunctionCall f = getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
                        replace(f, "install SimpleFunctionCall from TrivialFunctionCall");
                        Object[] argValues = f.matchParams(context, closure.function(), closure.environment(), callerFrame);
                        return closure.call(context, argValues);
                    } else {
                        GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
                        replace(n, "install GenericCall from FunctionCall");
                        return n.execute(context, callerFrame, callable);
                    }
                }
            }
        }
    }

    public static FunctionCall getSimpleFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        return new FunctionCall(ast, callableExpr, argNames, argExprs) {

            @Override
            protected final Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
                return placeArgs(context, callerFrame, func.nparams());
            }
        };
    }

    public static CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
            RNode fexp = r.nodes.truffle.MatchCallable.getUninitialized(call, fcall.getName());
            return getFunctionCall(fcall, fexp, names, exprs);
        }
    };

    @Override
    public Object execute(RContext context, Frame callerFrame) {
        RCallable tgt = (RCallable) callableExpr.execute(context, callerFrame);
        try {
            if (tgt instanceof RClosure) {
                RClosure closure = (RClosure) tgt;
                Object[] argValues = matchParams(context, closure.function(), closure.environment(), callerFrame);
                return closure.call(context, argValues);
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
            replace(n, "install GenericCall from FunctionCall");
            return n.execute(context, callerFrame, tgt);
        }
    }

    public static final class GenericCall extends FunctionCall {

        RCallable lastCallable;
        boolean lastWasFunction;

            // for functions
        RClosure lastClosure;  // null when last wasn't function
        RFunction closureFunction;
        RSymbol[] functionArgNames;
        int[] functionArgPositions;

            // for builtins
        RBuiltIn lastBuiltIn; // null when last wasn't builtin
        RSymbol builtInName;
        RNode builtInNode;

        GenericCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        public Object execute(RContext context, Frame callerFrame) {
            RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
            return execute(context, callerFrame, callable);
        }

        public Object execute(RContext context, Frame callerFrame, RCallable callable) {
            if (callable == lastClosure) {
                return placeArgs(context, callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
            }
            if (callable == lastBuiltIn) {
                return builtInNode.execute(context, callerFrame);
            }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                    functionArgPositions = computePositions(context, closureFunction, functionArgNames);
                }
                lastClosure = closure;
                lastBuiltIn = null;
                return placeArgs(context, callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
            } else {
                // callable instanceof RBuiltin
                RBuiltIn builtIn = (RBuiltIn) callable;
                RSymbol name = builtIn.name();
                if (name != builtInName) {
                    builtInName = name;
                    replaceChild(builtInNode, builtIn.callFactory().create(ast, argNames, argExprs));
                }
                lastBuiltIn = builtIn;
                lastClosure = null;
                return builtInNode.execute(context, callerFrame);
            }
        }
    }

    protected Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
        return null;
    }

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
                    context.error(getAST(), RError.UNUSED_ARGUMENT + " (" + argExprs[i].getAST() + ")");
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
        //FIXME answer there may be missing.
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
                context.error(argExprs[i].getAST(), RError.UNUSED_ARGUMENT);
            }
        }
        return argValues;
    }
}
