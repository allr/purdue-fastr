package r.nodes.truffle;

import com.oracle.truffle.api.nodes.*;

import r.*;
import r.builtins.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.runtime.*;

// TODO: fix (extend?) the propagation of scalar values and values with guards, currently it is very restricted
public abstract class FunctionCall extends AbstractCall {

    public final static boolean PROMISES = true;

    final RNode callableExpr;

    private FunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs, int[] dotsArgs) {
        super(ast, argNames, argExprs, dotsArgs);
        this.callableExpr = adoptChild(callableExpr);
    }

    public static RNode getFunctionCall(ASTNode ast, RNode callableExpr, RSymbol[] argNames, RNode[] argExprs) {
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
        if (dotsArgs != null) { return null; // can't use a fixed builtin node when calling using ...
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
            assert Utils.check(builtinNode != null);
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

        @Override public Object execute(Frame callerFrame) {
            try {
                if (builtinName.getValue() != null || builtinName.getVersion() != 0) { throw new UnexpectedResultException(null); }
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

        @Override public Object execute(Frame callerFrame) {
            Object callable = callableExpr.execute(callerFrame);
            try {
                if (callable != builtIn) { throw new UnexpectedResultException(null); }
                return builtInNode.execute(callerFrame);
            } catch (UnexpectedResultException e) {
                GenericCall n = new GenericCall(ast, callableExpr, rememberedArgNames, rememberedArgExprs);
                return replace(callableExpr, callable, n, callerFrame);
            }
        }
    }

    public static final class GenericCall extends FunctionCall {

        Object lastCallable; // RCallable, but using Object to avoid cast

        // for functions
        RClosure lastClosure; // null when last callable wasn't a function (closure)
        RFunction closureFunction;
        int[] functionArgPositions;
        Frame closureEnclosingFrame;
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
            if (callable == lastClosure) {
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return closureFunction.call(newFrame);
            }
            if (callable == lastBuiltIn) { return builtInNode.execute(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return closureFunction.call(newFrame);
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
        // TODO: it would be far more important to have these in simple and stable builtin call than here
        @Override public int executeScalarLogical(Frame callerFrame) throws UnexpectedResultException {
            Object callable = callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return RValueConversion.expectScalarLogical((RAny) closureFunction.call(newFrame));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarLogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return RValueConversion.expectScalarLogical((RAny) closureFunction.call(newFrame));
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
                return builtInNode.executeScalarLogical(callerFrame);
            }
        }

        // FIXME: essentially copy paste of execute
        // TODO: it would be far more important to have these in simple and stable builtin call than here
        @Override public int executeScalarNonNALogical(Frame callerFrame) throws UnexpectedResultException {
            Object callable = callableExpr.execute(callerFrame);
            if (callable == lastClosure) {
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return RValueConversion.expectScalarNonNALogical((RAny) closureFunction.call(newFrame));
            }
            if (callable == lastBuiltIn) { return builtInNode.executeScalarNonNALogical(callerFrame); }
            if (callable instanceof RClosure) {
                RClosure closure = (RClosure) callable;
                RFunction function = closure.function();
                if (function != closureFunction) {
                    closureFunction = function;
                    functionArgPositions = computePositions(closureFunction, functionDotsInfo);
                }
                closureEnclosingFrame = closure.enclosingFrame();
                lastClosure = closure;
                lastBuiltIn = null;
                Frame newFrame = lastClosure.createFrame();
                placeArgs(callerFrame, newFrame, functionArgPositions, functionDotsInfo, closureFunction.dotsIndex(), closureFunction.nparams());
                return RValueConversion.expectScalarNonNALogical((RAny) closureFunction.call(newFrame));
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
                Frame newFrame = closure.createFrame();
                placeDotsArgs(callerFrame, newFrame, function.paramNames());
                return function.call(newFrame);

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
                int nArgs = argExprs.length + ndots * (dotsArgLen - 1);

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