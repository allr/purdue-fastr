package r.builtins;

import r.Truffle.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Recall extends CallFactory {

    static final CallFactory _ = new Recall("Recall", new String[]{"..."}, new String[]{});

    private Recall(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Uninitialized(call, names, exprs);
    }

    // FIXME: could probably make this faster by subclassing AbstractCall directly
    public static class Uninitialized extends Builtin {
        public Uninitialized(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            try {
                throw new UnexpectedResultException(null);
            } catch (UnexpectedResultException e) {
                if (frame == null) { throw RError.getRecallCalledOutsideClosure(ast); }
                RFunction function = RFrameHeader.function(frame);
                if (function == null) { throw RError.getRecallCalledOutsideClosure(ast); }
                Fixed fn = new Fixed(ast, argNames, argExprs, function);
                replace(fn, "install Fixed from Recall.Uninitialized");
                return fn.doBuiltIn(frame, params);
            }
        }
    }

    // NOTE: this will only work when the Recall is NOT within eval/language object; for eval/language object will need another implementation
    // FIXME: could probably make this faster by subclassing AbstractCall directly
    public static class Fixed extends Builtin {
        final RFunction function;
        final RSymbol[] usedArgNames;
        final int[] argPositions;
        final CallTarget callTarget;
        final int nparams;

        public Fixed(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, RFunction function) {
            super(orig, argNames, argExprs);
            this.function = function;
            usedArgNames = new RSymbol[argExprs.length];
            argPositions = computePositions(function, usedArgNames);
            callTarget = function.callTarget();
            nparams = function.nparams();
        }

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            Object[] argValues = placeArgs(frame, argPositions, usedArgNames, nparams);
            RFrameHeader arguments = new RFrameHeader(function, (MaterializedFrame) RFrameHeader.enclosingFrame(frame), argValues);
            return (RAny) callTarget.call(arguments);
        }
    }

}
