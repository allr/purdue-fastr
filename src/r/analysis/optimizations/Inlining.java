package r.analysis.optimizations;

import r.analysis.codegen.annotations.behavior.VariableWrite;
import r.analysis.guards.*;
import r.analysis.nodes.*;
import r.analysis.visitors.BehaviorChecker;
import r.data.*;
import r.nodes.truffle.*;


/** Inlining optimizations.
 *
 * This class defines all the possible inlining optimizations. It is never meant to be instantiated and all members
 * should remain static.
 *
 * See the respective optimize methods for more details. All the methods in general return the following node tree:
 *
 * GuardHolder:
 *   - guard (SameEvaluationResult guard on the function object)
 *   - GuardedNode:
 *       - points to the guard above
 *       - fallback is the old FunctionCall node
 *       - content is the inlined function itself. 
 */
public class Inlining {


    static boolean isSimpleFunctionCall(FunctionCall call) {
        // make sure it is not a static builtin, or dots
        if (! (call instanceof FunctionCall.GenericCall))
            return false;
        FunctionCall.GenericCall gcall = (FunctionCall.GenericCall) call;
        // make sure it is a function call
        return !gcall.isBuiltin();
    }

    static boolean hasNoArguments(FunctionCall call) {
        return call.argumentsCount() == 0;
    }

    static boolean noSideEffectsInFunctionLookup(FunctionCall call) {
        RNode lookup = call.callableExpr;
        // TODO this might in theory use behaviors
        return lookup instanceof MatchCallable;
    }

    static RClosure getLastClosure(FunctionCall call) {
        return ((FunctionCall.GenericCall) call).lastClosure();
    }

    static RFunction getFunctionCallBody(FunctionCall call) {
        return getLastClosure(call).function();
    }


    static BehaviorChecker VARIABLE_WRITE = BehaviorChecker.create(VariableWrite.class);

    /** Inlines a function with no arguments and no writes of any variables.
     *
     * This is the most trivial example.
     *
     * @param call Call to be optimized.
     * @return Returns a guard holder and guarded node for the optimization, or null if the optimization cannot be applied.
     */
    public static RNode optimizeNoArgsNoWrites(FunctionCall call) {
        // make sure it is a call to a function with no arguments
        if (!isSimpleFunctionCall(call) || !hasNoArguments(call))
            return null;
        // check that the callableExpression is a simple lookup and it can be guarded
        if (!noSideEffectsInFunctionLookup(call))
            return null;
        // check that the body of the function does not write any variables
        RFunction function = getFunctionCallBody(call);
        if (VARIABLE_WRITE.test(function.body()))
            return null;
        // now we know we can create the inlined node as all conditions are satisfied, create the inlined body
        RNode inlinedFunction = (RNode) function.body().deepCopy();
        // create the guard
        Guard g = new SameEvaluationResult(getLastClosure(call), (RNode) call.callableExpr.deepCopy());
        // construct the guarded node and return it
        return new GuardHolder.SingleGuard(inlinedFunction.getAST(), g, new  GuardedNode(g, inlinedFunction, call));
    }
}
