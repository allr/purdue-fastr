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

        // for profiling and testing only
    private static final boolean ALWAYS_GENERIC_CALL = false;
    private static final boolean ALWAYS_CACHED_GENERIC_FUNCTION_CALL = false;

        // tuning, experiments
    private static final boolean PREFER_GENERIC_CALL = false;  // use instead of cached generic function call

    private static final boolean PREFER_SIMPLE_FUNCTION_CALL = false;   // use instead of trivial function call
    private static final boolean PREFER_TRIVIAL_FUNCTION_CALL = false;  // use instead of cached callable trivial function call

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast, argNames, argExprs);
        this.callableExpr = updateParent(callableExpr);
    }

    public static FunctionCall getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        if (ALWAYS_GENERIC_CALL) {
            return new GenericCall(ast, callableExpr, argNames, argExprs);
        }
        if (ALWAYS_CACHED_GENERIC_FUNCTION_CALL) {
            return getCachedGenericFunctionCall(ast, callableExpr, argNames, argExprs);
        }
        for (int i = 0; i < argNames.length; i++) { // FIXME this test is kind of inefficient, part of this job can be done by splitArguments
            if (argNames[i] != null || argExprs[i] == null) { // FIXME this test is a bit too strong, but I need a special node when there are default args

                if (PREFER_GENERIC_CALL) {
                    return new GenericCall(ast, callableExpr, argNames, argExprs);
                } else {
                    return getCachedGenericFunctionCall(ast, callableExpr, argNames, argExprs);
                }
            }
        }
        if (PREFER_SIMPLE_FUNCTION_CALL) {
            return getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
        } else if (PREFER_TRIVIAL_FUNCTION_CALL) {
            return new TrivialFunctionCall(ast, callableExpr, argNames, argExprs);
        } else {
            return new CachedCallableTrivialFunctionCall(ast, callableExpr, argNames, argExprs);
        }
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

            RClosure lastClosure;
            RFunction lastFunction;
            RSymbol[] names;
            int[] positions;

            // Note: it is intentional that the FunctionCall.execute is not re-used (e.g. through implementing only matchParams), because it saves a virtual call,
            // which gives a measurable speedup (binarytrees)

            // Note: caching on lastClosure allows to elide a typecheck on RClosure, which makes the execution measurably faster (binarytrees)
            @Override
            public Object execute(RContext context, Frame callerFrame) {
                RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
                try {
                    if (callable != lastClosure) {
                        if (!(callable instanceof RClosure)) {
                            throw new UnexpectedResultException(null);
                        }
                        lastClosure = (RClosure) callable;
                    }
                    RFunction func = lastClosure.function();
                    if (func != lastFunction) {
                        lastFunction = func;
                        names = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                        positions = computePositions(context, func, names);
                    }
                    Object[] argValues = placeArgs(context, callerFrame, positions, names, func.nparams());
                    return lastClosure.call(context, argValues);
                } catch (UnexpectedResultException e) {
                    GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
                    replace(n, "install GenericCall from FunctionCall");
                    return generic(context, callerFrame, callable);
                }
            }
        };
    }

    public abstract static class FillArgs {
        public abstract Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs);

        public static FillArgs create(final RNode[] argExprs) {
            // FIXME: with partial evaluation, these can probably be generated by a simple template, with a "final"
            //        number of arguments and with explodeloop
            switch(argExprs.length) {
                case 0 : return new FillArgs() {
                    Object[] args = new Object[0];
                    @Override
                    public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                        return args;
                    }
                };

                case 1 : return new FillArgs() {
                    Object[] args = new Object[1];
                    @Override
                    public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                        args[0] = argExprs[0].execute(context, callerFrame);
                        return args;
                    }
                };

                case 2 : return new FillArgs() {
                    Object[] args = new Object[2];
                    @Override
                    public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                        args[0] = argExprs[0].execute(context, callerFrame);
                        args[1] = argExprs[1].execute(context, callerFrame);
                        return args;
                    }
                };

                case 3 : return new FillArgs() {
                    Object[] args = new Object[3];
                    @Override
                    public final Object[] fill(RContext context, Frame callerFrame, RNode[] argExprs) {
                        args[0] = argExprs[0].execute(context, callerFrame);
                        args[1] = argExprs[1].execute(context, callerFrame);
                        args[2] = argExprs[2].execute(context, callerFrame);
                        return args;
                    }
                };
            }
            Utils.nyi("unsupported number of argumets");
            return null;
        }
    }

    public abstract static class DoCall {
        public abstract Object doCall(RClosure closure, RContext context, Frame frame);
        public static DoCall create(final RNode[] argExprs) {
            switch(argExprs.length) {
                case 0 : return new DoCall() {
                    @Override
                    public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                        return closure.trivialCall(context);
                    }
                };

                case 1 : return new DoCall() {
                    @Override
                    public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                        return closure.trivialCall(context, argExprs[0].execute(context, callerFrame));
                    }
                };

                case 2 : return new DoCall() {
                    @Override
                    public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                        return closure.trivialCall(context, argExprs[0].execute(context, callerFrame), argExprs[1].execute(context, callerFrame));
                    }

                };

                case 3 : return new DoCall() {
                    @Override
                    public Object doCall(RClosure closure, RContext context, Frame callerFrame) {
                        return closure.trivialCall(context, argExprs[0].execute(context, callerFrame), argExprs[1].execute(context, callerFrame), argExprs[2].execute(context, callerFrame));
                    }
                };
            }
            Utils.nyi("unsupported number of arguments");
            return null;
        }
    }

    public static final class TrivialFunctionCall extends FunctionCall {

        public TrivialFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        protected Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (!isTrivialFunctionCall(func)) {
                    FunctionCall f = getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
                    replace(f, "install SimpleFunctionCall from TrivialFunctionCall");
                    return f.matchParams(context, func, parentFrame, callerFrame);
                }

                if (CAN_BYPASS_TRUFFLE) {
                   FunctionCall f = new CachedFunctionNonTruffle(ast, callableExpr, argNames, argExprs, func, DoCall.create(argExprs));
                   replace(f, "specialize TrivialFunctionCall");
                   return placeArgs(context, callerFrame, argExprs.length);
                } else {
                   // Truffle version
                   FunctionCall f = new CachedFunctionTruffle(ast, callableExpr, argNames, argExprs, func, FillArgs.create(argExprs));
                   replace(f, "specialize TrivialFunctionCall");
                   return f.matchParams(context, func, parentFrame, callerFrame);
                }
            }
        }

        public static final class CachedFunctionTruffle extends FunctionCall {
            final RFunction cachedFunction;
            final FillArgs fill;

            public CachedFunctionTruffle(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RFunction function, FillArgs fill) {
                super(ast, callableExpr, argNames, argExprs);
                this.cachedFunction = function;
                this.fill = fill;
            }

            @Override
            protected Object[] matchParams(RContext context, RFunction func, Frame parentFrame, Frame callerFrame) {
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

        public static final class CachedFunctionNonTruffle extends FunctionCall {
            final RFunction cachedFunction;
            final DoCall doCall;

            public CachedFunctionNonTruffle(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RFunction function, DoCall doCall) {
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
                        return generic(context, callerFrame, callable);
                    }
                }
            }
        }
    }

    public static final class CachedCallableTrivialFunctionCall extends FunctionCall {

        public CachedCallableTrivialFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        public Object execute(RContext context, Frame callerFrame) {
            try {
                throw new UnexpectedResultException(null);
            } catch (Exception e) {
                RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
                if (!(callable instanceof RClosure)) {
                    FunctionCall f = new GenericCall(ast, callableExpr, argNames, argExprs);
                    replace(f, "install GenericCall");
                    return generic(context, callerFrame, callable);
                }
                RClosure closure = (RClosure) callable;
                RFunction func = closure.function();
                if (!isTrivialFunctionCall(func)) {
                    FunctionCall f = getSimpleFunctionCall(ast, callableExpr, argNames, argExprs);
                    replace(f, "install SimpleFunctionCall from TrivialFunctionCall");
                    return generic(context, callerFrame, callable);
                }

                if (CAN_BYPASS_TRUFFLE) {
                   FunctionCall f = new CachedClosureNonTruffle(ast, callableExpr, argNames, argExprs, closure, DoCall.create(argExprs));
                   replace(f, "specialize CachedClosureTrivialFunctionCall");
                   return f.generic(context,  callerFrame, callable);
                } else {
                   // Truffle version
                   Utils.nyi("Truffle version not yet supported");
                   return null;
                }
            }

        }

        public static final class CachedClosureNonTruffle extends FunctionCall {
            final RClosure cachedClosure;
            final DoCall doCall;

            public CachedClosureNonTruffle(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RClosure closure, DoCall doCall) {
                super(ast, callableExpr, argNames, argExprs);
                this.cachedClosure = closure;
                this.doCall = doCall;
            }

            @Override
            public Object execute(RContext context, Frame callerFrame) {
                RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
                try {
                    if (callable != cachedClosure) {
                        throw new UnexpectedResultException(null);
                    }
                    return doCall.doCall(cachedClosure, context, callerFrame);
                } catch (UnexpectedResultException e) {
                    if (callable instanceof RClosure) {
                        RClosure closure = (RClosure) callable;
                        FunctionCall f = new TrivialFunctionCall(ast, callableExpr, argNames, argExprs);
                        replace(f, "install TrivialFunctionCall from TrivialFunctionCall");
                        Object[] argValues = f.matchParams(context, closure.function(), closure.environment(), callerFrame);
                        return closure.call(context, argValues);
                    } else {
                        GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
                        replace(n, "install GenericCall from FunctionCall");
                        return generic(context, callerFrame, callable);
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
        RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
        try {
            if (callable instanceof RClosure) { // FIXME: this typecheck could be avoided through caching and checking on a callable reference (like in GenericCall)
                RClosure closure = (RClosure) callable;
                Object[] argValues = matchParams(context, closure.function(), closure.environment(), callerFrame);
                return closure.call(context, argValues);
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
            replace(n, "install GenericCall from FunctionCall");
            return generic(context, callerFrame, callable);
        }
    }

    // this is used in node-rewriting instead of having a special execute method that takes callable
    // having such method slows down the fast path by one virtual call (this had measurable overhead in binarytrees)
    // FIXME: maybe a private final method that takes the callable would do the trick ?

    RNode dummyNode;
    public Object generic(RContext context, Frame callerFrame, RCallable callable) {
        if (callable instanceof RClosure) {
            RClosure closure = (RClosure) callable;
            RFunction function = closure.function();
            RSymbol[] argNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
            int[] argPositions = computePositions(context, function, argNames);
            Object[] argValues = placeArgs(context, callerFrame, argPositions, argNames, function.nparams());
            return closure.call(context, argValues);
        } else {
            // callable instanceof RBuiltin
            RBuiltIn builtIn = (RBuiltIn) callable;
            dummyNode = updateParent(builtIn.callFactory().create(ast, argNames, argExprs));
            return dummyNode.execute(context, callerFrame); // yikes, this can be slow
        }
    }

    public static final class GenericCall extends FunctionCall {

        RCallable lastCallable;
        boolean lastWasFunction;

            // for functions
        RClosure lastClosure;  // null when last callable wasn't a function (closure)
        RFunction closureFunction;
        RSymbol[] functionArgNames;
        int[] functionArgPositions;

            // for builtins
        RBuiltIn lastBuiltIn; // null when last callable wasn't a builtin
        RSymbol builtInName;
        RNode builtInNode;

        GenericCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        public Object execute(RContext context, Frame callerFrame) {
            RCallable callable = (RCallable) callableExpr.execute(context, callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(context, callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                return lastClosure.call(context, argValues);
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
                Object[] argValues = placeArgs(context, callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                return lastClosure.call(context, argValues);
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

    // providedArgNames is an output parameter, it should be an array of argExprs.length nulls before the call
    // FIXME: what do we need the parameter for?

    protected final int[] computePositions(final RContext context, final RFunction func, RSymbol[] usedArgNames) {
        return computePositions(context, func.paramNames(), usedArgNames);
    }

    protected final int[] computePositions(final RContext context, RSymbol[] paramNames, RSymbol[] usedArgNames) {

        int nArgs = argExprs.length;
        int nParams = paramNames.length;

        boolean[] provided = new boolean[nParams]; // Alloc in stack if the VM is not a shitty one (i.e. trivial escape analysis)

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

    protected boolean isTrivialFunctionCall(RFunction func) {
        if (argExprs.length != func.nparams() || argExprs.length > 3) {
            return false;
        } else {
            // exclude functions with default parameters
            // because we preallocate the Object[] array with arguments for now, and default parameters could have a recursive call

            // also, the trivial calls on the callee side do not handle default arguments (do not traverse the arguments checking if any of them is null, etc)
            RNode[] dfl = func.paramValues();
            for (int i = 0; i < dfl.length; i++) {
                if (dfl[i] != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
