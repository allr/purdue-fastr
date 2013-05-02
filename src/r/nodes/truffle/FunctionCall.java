package r.nodes.truffle;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

public abstract class FunctionCall extends AbstractCall {

    final RNode callableExpr;

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast, argNames, argExprs);
        this.callableExpr = adoptChild(callableExpr);
    }

    public static FunctionCall getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        // place optimized nodes here, e.g. for positional-only argument passing
        // these are present in the old truffle api version, but not here, as they were not really helping
        return new FirstFunctionCall(ast, callableExpr, argNames, argExprs);
    }

    public static FunctionCall getSimpleFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        return new FunctionCall(ast, callableExpr, argNames, argExprs) {

            @Override protected final Object[] matchParams(RFunction func, Frame parentFrame, Frame callerFrame) {
                return placeArgs(callerFrame, func.nparams());
            }
        };
    }

    public static CallFactory FACTORY = new CallFactory("<empty>") {

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
            RNode fexp = r.nodes.truffle.MatchCallable.getUninitialized(call, fcall.getName());
            return getFunctionCall(fcall, fexp, names, exprs);
        }
    };

    @Override public Object execute(Frame callerFrame) {
        RCallable callable = (RCallable) callableExpr.execute(callerFrame);
        try {
            if (callable instanceof RClosure) {
                // FIXME: this type check could be avoided through caching and checking on a callable reference (like in GenericCall)
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                MaterializedFrame enclosingFrame = closure.enclosingFrame();
                Object[] argValues = matchParams(function, enclosingFrame, callerFrame);
                RFrameHeader arguments = new RFrameHeader(function, enclosingFrame, argValues);
                return function.callTarget().call(arguments);
            } else {
                throw new UnexpectedResultException(null);
            }
        } catch (UnexpectedResultException e) {
            if (callable instanceof RBuiltIn) {
                return replace(new FastPathBuiltInCall(ast, callableExpr, argNames, argExprs, (RBuiltIn) callable)).builtInNode.execute(callerFrame);
            }
            GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
            replace(n, "install GenericCall from FunctionCall");
            return generic(callerFrame, callable);
        }
    }

    // this is used in node-rewriting instead of having a special execute method that takes callable
    // having such method slows down the fast path by one virtual call (this had measurable overhead in binarytrees)
    // TODO: get rid of this (a private function should do the trick)

    RNode dummyNode;

    public Object generic(Frame callerFrame, RCallable callable) {
        if (callable instanceof RClosure) {
            RClosure closure = (RClosure) callable;
            RFunction function = closure.function();
            MaterializedFrame enclosingFrame = closure.enclosingFrame();
            RSymbol[] argsNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
            int[] argPositions = computePositions(function, argsNames);
            Object[] argValues = placeArgs(callerFrame, argPositions, argsNames, function.nparams());
            RFrameHeader arguments = new RFrameHeader(function, enclosingFrame, argValues);
            return function.callTarget().call(arguments);
        } else {
            // callable instanceof RBuiltin
            RBuiltIn builtIn = (RBuiltIn) callable;
            dummyNode = adoptChild(builtIn.callFactory().create(ast, argNames, argExprs));
            // FIXME: adoptChild should not be used outside constructor, but we'll get rid of this, anyway
            return dummyNode.execute(callerFrame); // yikes, this can be slow
        }
    }

    /** GRAAL The fastpath builtin node is used to make calling builtins fast without the need of huge inlining of the
     * code that never gets used in most cases.
     */
    public static final class FastPathBuiltInCall extends FunctionCall {
        @Child final RNode builtInNode;
        final RBuiltIn builtIn;


        public FastPathBuiltInCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RBuiltIn callable) {
            super(ast, callableExpr, argNames, argExprs);
            builtIn = callable;
            builtInNode = adoptChild(builtIn.callFactory().create(ast, argNames, argExprs));
        }

        @Override public Object execute(Frame callerFrame) {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable != builtIn) {
                CompilerDirectives.transferToInterpreter();
                return replace(new GenericCall(ast, callableExpr, argNames, argExprs)).execute(callerFrame, callable);
            }
            return builtInNode.execute(callerFrame);
        }
    }

    public static final class FirstFunctionCall extends FunctionCall {

        private FirstFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override
        public Object execute(Frame frame) {
            RCallable callable = (RCallable) callableExpr.execute(frame);
            if (callable instanceof RBuiltIn) {
                CompilerDirectives.transferToInterpreter();
                return replace(new FastPathBuiltInCall(ast,callableExpr, argNames, argExprs, (RBuiltIn) callable)).builtInNode.execute(frame);
            } else {
                CompilerDirectives.transferToInterpreter();
                return replace(new GenericCall(ast, callableExpr, argNames, argExprs)).execute(frame, callable);
            }
        }
    }

    public static final class GenericCall extends FunctionCall {

        RCallable lastCallable;
        boolean lastWasFunction;

        // for functions
        RClosure lastClosure; // null when last callable wasn't a function (closure)
        RFunction closureFunction;
        RSymbol[] functionArgNames; // FIXME: is this faster than remembering just the function?
        int[] functionArgPositions;
        CallTarget functionCallTarget;
        MaterializedFrame closureEnclosingFrame;

        // for builtins
        RBuiltIn lastBuiltIn; // null when last callable wasn't a builtin
        RSymbol builtInName;
        @Child RNode builtInNode;

        GenericCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
            // TODO this is terribly inefficient, and should likely be rewritten to a node rewrite or something, but
            // it works, so I do not care that much atm.
            builtInNode = adoptChild(new Dummy());
        }

        @Override public Object execute(Frame callerFrame) {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            return execute(callerFrame, callable);
        }

        final public Object execute(Frame callerFrame, RCallable callable) {
//            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return functionCallTarget.call(arguments);
            }
            if (callable == lastBuiltIn) { return builtInNode.execute(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                    functionArgPositions = computePositions(closureFunction, functionArgNames);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return functionCallTarget.call(arguments);
            } else {
                // callable instanceof RBuiltin                
                RBuiltIn builtIn = (RBuiltIn) callable;
                RSymbol name = builtIn.name();
                if (name != builtInName) {
                    builtInName = name;
                    try {
                        builtInNode.replace(builtIn.callFactory().create(ast, argNames, argExprs));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
                lastBuiltIn = builtIn;
                lastClosure = null;
                return builtInNode.execute(callerFrame);
            }
        }

        // FIXME: essentially copy paste of execute
        @Override public int executeScalarLogical(Frame callerFrame) throws UnexpectedResultException {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarLogical((RAny) functionCallTarget.call(arguments));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarLogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                    functionArgPositions = computePositions(closureFunction, functionArgNames);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarLogical((RAny) functionCallTarget.call(arguments));
            } else {
                // callable instanceof RBuiltin
                RBuiltIn builtIn = (RBuiltIn) callable;
                RSymbol name = builtIn.name();
                if (name != builtInName) {
                    builtInName = name;
                    builtInNode.replace(builtIn.callFactory().create(ast, argNames, argExprs));
                }
                lastBuiltIn = builtIn;
                lastClosure = null;
                return builtInNode.executeScalarLogical(callerFrame);
            }
        }

        // FIXME: essentially copy paste of execute
        @Override public int executeScalarNonNALogical(Frame callerFrame) throws UnexpectedResultException {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarNonNALogical((RAny) functionCallTarget.call(arguments));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarNonNALogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgNames = new RSymbol[argExprs.length]; // FIXME: escaping allocation - can we keep it statically?
                    functionArgPositions = computePositions(closureFunction, functionArgNames);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionArgNames, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarNonNALogical((RAny) functionCallTarget.call(arguments));
            } else {
                // callable instanceof RBuiltin
                RBuiltIn builtIn = (RBuiltIn) callable;
                RSymbol name = builtIn.name();
                if (name != builtInName) {
                    builtInName = name;
                    builtInNode.replace(builtIn.callFactory().create(ast, argNames, argExprs));
                }
                lastBuiltIn = builtIn;
                lastClosure = null;
                return builtInNode.executeScalarNonNALogical(callerFrame);
            }
        }
    }

    protected Object[] matchParams(RFunction func, Frame parentFrame, Frame callerFrame) {
        return null;
    }

    // providedArgNames is an output parameter, it should be an array of argExprs.length nulls before the call
    // FIXME: what do we need the parameter for?

    @ExplodeLoop protected final Object[] placeArgs(Frame callerFrame, int nparams) {
        Object[] argValues = new Object[nparams];
        int i = 0;

        for (; i < argExprs.length; i++) {
            if (i < nparams) {
                // TODO: create a promise here, instead
                argValues[i] = argExprs[i].execute(callerFrame);
            } else {
                // TODO support ``...''
                throw RError.getUnusedArgument(ast, argNames[i], argExprs[i]);
            }
        }
        return argValues;
    }
}
