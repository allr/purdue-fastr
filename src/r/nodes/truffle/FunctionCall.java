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

    /** Injected class that counts the number of calls to the function and upon reaching certain threshold triggers
     * the analysis. If the FunctionCall node is not stable during the time Counter is active, it is aborted and
     * removed, as such function is not a likely candidate for the optimization.
     */
    public static class Counter extends RNode {

        public static final int OPTIMIZATION_THRESHOLD = 2;

        @Child FunctionCall call;

        int count;

        public Counter(FunctionCall call) {
            this.call = adoptChild(call);
            this.count = 0;
        }


        /** Executes the actual call and increases the count if the call node is stable. Otherwise removes itself from
         * the execution tree.
         */
        @Override
        public Object execute(Frame frame) {
            FunctionCall last = call;
            Object result = call.execute(frame);
            if (last != call) {
                CompilerDirectives.transferToInterpreter();
                System.out.println("Counter for function "+last.ast.toString()+" discarded due to unstable execution tree.");
                this.replace(call);
            }
            ++count;
            if (count == OPTIMIZATION_THRESHOLD) {
                CompilerDirectives.transferToInterpreter();
                System.out.println("Counter for function "+last.ast.toString()+" reached the threshold");
                this.replace(call);
            }
            return result;
        }

        // TODO maybe this should also be guarded by the counter
        @Override
        public int executeScalarLogical(Frame callerFrame) throws UnexpectedResultException {
            CompilerDirectives.transferToInterpreter();
            this.replace(call);
            return call.executeScalarLogical(callerFrame);
        }

        // TODO maybe this should also be guarded by the counter
        @Override
        public int executeScalarNonNALogical(Frame callerFrame) throws UnexpectedResultException {
            CompilerDirectives.transferToInterpreter();
            this.replace(call);
            return call.executeScalarNonNALogical(callerFrame);
        }

    }



    public final RNode callableExpr;

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, int[] dotsArgs) {
        super(ast, argNames, argExprs, dotsArgs);
        this.callableExpr = adoptChild(callableExpr);
    }

    public static FunctionCall getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
        // place optimized nodes here, e.g. for positional-only argument passing
        // these are present in the old truffle api version, but not here, as they were not really helping

        int[] dotsArgs = findDotsArgs(argExprs);
        if (dotsArgs != null) {
            return new GenericDotsCall(ast, callableExpr, argNames, argExprs, dotsArgs);
        } else {
            return new UninitializedCall(ast, callableExpr, argNames, argExprs);
        }
    }

    // returns indexes of ... arguments (or null if none present)
    public static int[] findDotsArgs(RNode[] argExprs) {

        int[] res = new int[argExprs.length];
        int j = 0;
        for (int i = 0; i < argExprs.length; i++) {
            RNode expr = argExprs[i];
            if (expr == null) {
                continue;
            }
            ASTNode ast = expr.getAST();
            if (ast instanceof SimpleAccessVariable && ((SimpleAccessVariable) ast).getSymbol() == RSymbol.THREE_DOTS_SYMBOL) {
                res[j++] = i;
            }
        }
        if (j == 0) {
            return null;
        } else {
            int[] toret = new int[j];
            System.arraycopy(res, 0, toret, 0, j);
            return toret;
        }
    }

    public static CallFactory FACTORY = new CallFactory("<empty>") { // only used with static lookup of builtins

        @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
            RSymbol fname = fcall.getName();
            RNode fexp = r.nodes.truffle.MatchCallable.getUninitialized(call, fname);
            return getFunctionCall(fcall, fexp, names, exprs);
        }
    };

    public static RNode createBuiltinCall(ASTNode call, RSymbol[] argNames, RNode[] argExprs) {

        int[] dotsArgs = findDotsArgs(argExprs);
        if (dotsArgs != null) {
            return null; // can't use a fixed builtin node when calling using ...
        }

        r.nodes.FunctionCall fcall = (r.nodes.FunctionCall) call;
        RSymbol fname = fcall.getName();

        RBuiltIn builtin = Primitives.getBuiltIn(fname, null);
        if (builtin != null) {
            // probably calling a builtin, but maybe not
            RNode builtinNode;
            try {
                builtinNode = builtin.callFactory().create(call, argNames, argExprs);
            } catch (RError e) {
                // not a builtin
                // TODO: what if the attempt to create a builtin has produced warnings???
                return null;
            }
            return new SimpleBuiltinCall(fcall, fname, argNames, argExprs, builtinNode);
        }
        return null;
    }

    public static final class UninitializedCall extends FunctionCall {

        public UninitializedCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
            super(ast, callableExpr, argNames, argExprs, null);
        }

        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (callable instanceof RBuiltIn) {
                    RNode n;
                    RBuiltIn builtIn = (RBuiltIn) callable;
                    RNode builtInNode = builtIn.callFactory().create(ast, argNames, argExprs);
                    n = new StableBuiltinCall(ast, callableExpr, argNames, argExprs, builtIn, builtInNode);
                    return replace(callableExpr, callable, n, callerFrame);
                } else {
                    // for a stabilized generic call inject the counter before the generic call node
                    FunctionCall n = new GenericCall(ast, callableExpr, argNames, argExprs);
                    return replace(callableExpr, callable, new Counter(n), callerFrame);
                }
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

        // for functions
        public RClosure lastClosure; // null when last callable wasn't a function (closure)
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
            super(ast, callableExpr, argNames, argExprs, null);
        }


        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            return execute(callerFrame, callable);
        }

        /** Calls the given function knowing the evaluated callable. This is important for reversing the inlining whose
         * validity is determined by evaluating the callableExpr.
         */
        public Object execute(Frame callerFrame, Object callable) {
            if (callable == lastClosure) {
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
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
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                RFrameHeader arguments = new RFrameHeader(closureFunction, closureEnclosingFrame, argValues);
                return functionCallTarget.call(arguments);
            } else {
                // callable instanceof RBuiltin
                RBuiltIn builtIn = (RBuiltIn) callable;
                RSymbol name = builtIn.name();
                if (name != builtInName) {
                    builtInName = name;
                    if (builtInNode == null) {
                        builtInNode = adoptChild(builtIn.callFactory().create(ast, argNames, argExprs));
                    } else {
                        builtInNode.replace(builtIn.callFactory().create(ast, argNames, argExprs));
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
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
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
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
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
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
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
                Object[] argValues = placeArgs(callerFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
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

    // function call that passes "..."
    public static final class GenericDotsCall extends FunctionCall {

        GenericDotsCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, int[] dotsArgs) {
            super(ast, callableExpr, argNames, argExprs, dotsArgs);
            assert Utils.check(dotsArgs != null);
        }

        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                Object[] argValues = placeDotsArgs(callerFrame, function.paramNames());
                RFrameHeader arguments = new RFrameHeader(function, closure.enclosingFrame(), argValues);
                return function.callTarget().call(arguments);
            } else {
                // FIXME: these calls to builtin seem pretty expensive

                assert Utils.check(callable instanceof RBuiltIn);
                RBuiltIn builtIn = Utils.cast(callable);

                int nextDots = dotsArgs[0];
                RDots dotsArg = (RDots) argExprs[nextDots].execute(callerFrame);
                RSymbol[] dotsArgNames = dotsArg.names();
                Object[] dotsArgValues = dotsArg.values();
                int dotsArgLen = dotsArgNames.length;
                int ndots = dotsArgs.length;
                int nArgs =  argExprs.length + ndots * (dotsArgLen - 1);

                RSymbol[] actualArgNames = new RSymbol[nArgs];
                RNode[] actualArgExprs = new RNode[nArgs];
                int dotsIndex = 0;

                for (int i = 0, j = 0; j < nArgs; i++) {
                    if (i == nextDots) {
                        for (int k = 0; k < dotsArgLen; k++, j++) {
                            actualArgNames[j] = dotsArgNames[k];
                            Object value = dotsArgValues[k];

                            if (FunctionCall.PROMISES && value instanceof RPromise) {
                                final RPromise promise = (RPromise) value;
                                actualArgExprs[j] = new BaseR(promise.expression().getAST()) {

                                    @Override public Object execute(Frame frame) {
                                        return promise.forceOrGet();
                                    }

                                };
                            } else {
                                // NOTE: in GNU-R, dots arguments are re-promised on a call, so this would be unreachable
                                assert Utils.check(value instanceof RAny);
                                RAny rvalue = Utils.cast(value);

                                ASTNode dummyAST = new r.nodes.Constant(rvalue);
                                actualArgExprs[j] = new r.nodes.truffle.Constant(dummyAST, rvalue);
                            }

                        }
                        dotsIndex++;
                        if (dotsIndex < ndots) {
                            nextDots = dotsIndex;
                        }
                    } else {
                        actualArgNames[j] = argNames[i];
                        actualArgExprs[j] = argExprs[i];
                        j++;
                    }
                }
                return builtIn.callFactory().invokeDynamic(callerFrame, actualArgNames, actualArgExprs, ast);
            }
        }
    }

}