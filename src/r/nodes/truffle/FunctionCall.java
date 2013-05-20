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

    public final static boolean PROMISES = true;

    final RNode callableExpr;

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast, argNames, argExprs);
        this.callableExpr = adoptChild(callableExpr);
    }

    public static FunctionCall getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        // place optimized nodes here, e.g. for positional-only argument passing
        // these are present in the old truffle api version, but not here, as they were not really helping

        //        return new GenericCall(ast, callableExpr, argNames, argExprs);
        return new UninitializedCall(ast, callableExpr, argNames, argExprs);  // FIXME: this is not helping as much as one would think, why?
    }

//    public static FunctionCall getSimpleFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) { // FIXME: is this still needed?
//        return new FunctionCall(ast, callableExpr, argNames, argExprs) {
//
//            @Override protected final Object[] matchParams(RFunction func, Frame parentFrame, Frame callerFrame) {
//                return placeArgs(callerFrame, func.nparams());
//            }
//        };
//    }

    public static CallFactory FACTORY = new CallFactory("<empty>") {

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
            RSymbol fname = fcall.getName();
            RNode fexp = r.nodes.truffle.MatchCallable.getUninitialized(call, fname);
            return getFunctionCall(fcall, fexp, names, exprs);
        }
    };

    public static RNode createBuiltinCall(ASTNode call, RSymbol[] names, RNode[] exprs) {
        r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
        RSymbol fname = fcall.getName();

        RBuiltIn builtin = Primitives.getBuiltIn(fname, null);
        if (builtin != null) {
            // probably calling a builtin, but maybe not
            RNode builtinNode;
            try {
                builtinNode = builtin.callFactory().create(call, names, exprs);
            } catch (RError e) {
                // not a builtin
                // TODO: what if the attempt to create a builtin has produced warnings???
                return null;
            }
            return new SimpleBuiltinCall(fcall, fname, names, exprs, builtinNode);
        }
        return null;
    }

//    @Override public Object execute(Frame callerFrame) { // FIXME: is this still needed?
//        RCallable callable = (RCallable) callableExpr.execute(callerFrame);
//        try {
//            if (callable instanceof RClosure) {
//                // FIXME: this type check could be avoided through caching and checking on a callable reference (like in GenericCall)
//                RClosure closure = (RClosure) callable;
//                RFunction function = closure.function();
//                MaterializedFrame enclosingFrame = closure.enclosingFrame();
//                Object[] argValues = matchParams(function, enclosingFrame, callerFrame);
//                RFrameHeader arguments = new RFrameHeader(function, enclosingFrame, argValues);
//                return function.callTarget().call(arguments);
//            } else {
//                throw new UnexpectedResultException(null);
//            }
//        } catch (UnexpectedResultException e) {
//            GenericCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
//            return replace(callableExpr, callable, n, callerFrame);
//        }
//    }

    public static final class UninitializedCall extends FunctionCall {

        public UninitializedCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                RNode n;
                if (callable instanceof RBuiltIn) {
                    RBuiltIn builtIn = (RBuiltIn) callable;
                    RNode builtInNode = builtIn.callFactory().create(ast, argNames, argExprs);
                    n = new StableBuiltinCall(ast, callableExpr, argNames, argExprs, builtIn, builtInNode);
                } else {
                    n = new GenericCall(ast, callableExpr, argNames, argExprs);
                }
                return replace(callableExpr, callable, n, callerFrame);
            }
        }
    }

    // calling a non-overridden builtin via its standard name
    public static final class SimpleBuiltinCall extends BaseR {

        final RSymbol builtinName;
        @Child RNode builtinNode;

        final RNode[] rememberedArgExprs; // NOTE: not children - the real parent of the exprs is the builtin
        final RSymbol[] rememberedArgNames;


        SimpleBuiltinCall(ASTNode ast, RSymbol builtinName, RSymbol[] argNames, RNode[] argExprs, RNode builtInNode) {
            super(ast);
            this.builtinName = builtinName;
            this.rememberedArgNames = argNames;
            this.rememberedArgExprs = argExprs; // NOTE: not children
            this.builtinNode = adoptChild(builtInNode);
        }

        @Override
        public Object execute(Frame callerFrame) {
            try {
                if (builtinName.getValue() != null || builtinName.getVersion() != 0) {
                    throw new UnexpectedResultException(null);
                }
                return builtinNode.execute(callerFrame);
            } catch (UnexpectedResultException e) {
                RNode callableExpr = r.nodes.truffle.MatchCallable.getUninitialized(ast, builtinName);
                return replace(getFunctionCall(ast, callableExpr, rememberedArgNames, rememberedArgExprs)).execute(callerFrame);
            }
        }
    }

    public static final class StableBuiltinCall extends BaseR {

        final RBuiltIn builtIn; // null when last callable wasn't a builtin
        @Child RNode builtInNode;
        @Child RNode callableExpr;

        final RNode[] rememberedArgExprs; // NOTE: not children - the real parent of the exprs is the builtin
        final RSymbol[] rememberedArgNames;


        StableBuiltinCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, RBuiltIn builtIn, RNode builtInNode) {
            super(ast);
            this.callableExpr = adoptChild(callableExpr);
            this.rememberedArgNames = argNames;
            this.rememberedArgExprs = argExprs; // NOTE: not children
            this.builtIn = builtIn;
            this.builtInNode = adoptChild(builtInNode);
        }

        @Override
        public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            try {
                if (callable != builtIn) {
                    throw new UnexpectedResultException(null);
                }
                return builtInNode.execute(callerFrame);
            } catch (UnexpectedResultException e) {
                GenericCall n = new GenericCall(ast, callableExpr, rememberedArgNames, rememberedArgExprs);
                return replace(callableExpr, callable, n, callerFrame);
            }
        }
    }

    public static final class GenericCall extends FunctionCall {

        Object lastCallable; // RCallable, but using Object to avoid cast
        boolean lastWasFunction;

        // for functions
        RClosure lastClosure; // null when last callable wasn't a function (closure)
        RFunction closureFunction;
        int[] functionArgPositions;
        CallTarget functionCallTarget;
        MaterializedFrame closureEnclosingFrame;
        final DotsInfo functionDotsInfo = new DotsInfo();

        // for builtins
        RBuiltIn lastBuiltIn; // null when last callable wasn't a builtin
        RSymbol builtInName;
        @Child RNode builtInNode;

        GenericCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs);
        }

        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return functionCallTarget.call(arguments);
            }
            if (callable == lastBuiltIn) { return builtInNode.execute(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return functionCallTarget.call(arguments);
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
                return builtInNode.execute(callerFrame);
            }
        }

        // FIXME: essentially copy paste of execute
        @Override public int executeScalarLogical(Frame callerFrame) throws UnexpectedResultException {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarLogical((RAny) functionCallTarget.call(arguments));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarLogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarLogical((RAny) functionCallTarget.call(arguments));
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
                return builtInNode.executeScalarLogical(callerFrame);
            }
        }

        // FIXME: essentially copy paste of execute
        @Override public int executeScalarNonNALogical(Frame callerFrame) throws UnexpectedResultException {
            RCallable callable = (RCallable) callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarNonNALogical((RAny) functionCallTarget.call(arguments));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarNonNALogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                    functionCallTarget = function.callTarget();
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return RValueConversion.expectScalarNonNALogical((RAny) functionCallTarget.call(arguments));
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
                return builtInNode.executeScalarNonNALogical(callerFrame);
            }
        }
    }

//    protected Object[] matchParams(RFunction func, Frame parentFrame, Frame callerFrame) { // FIXME: is this still needed?
//        return null;
//    }
//
//    // providedArgNames is an output parameter, it should be an array of argExprs.length nulls before the call
//    // FIXME: what do we need the parameter for?
//
//    @ExplodeLoop protected final Object[] placeArgs(Frame callerFrame, int nparams) { // FIXME: is this still needed?
//        Object[] argValues = new Object[nparams];
//        int i = 0;
//
//        for (; i < argExprs.length; i++) {
//            if (i < nparams) {
//                // TODO: create a promise here, instead
//                argValues[i] = argExprs[i].execute(callerFrame);
//            } else {
//                // TODO support ``...''
//                throw RError.getUnusedArgument(ast, argNames[i], argExprs[i]);
//            }
//        }
//        return argValues;
//    }
}
