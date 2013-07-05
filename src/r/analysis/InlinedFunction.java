package r.analysis;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import r.fastr;
import r.nodes.truffle.*;

/** Function inlining.
 *
 */
public class InlinedFunction {

    /** Determines whether the given function should be inlined and if so, returns the new root. Otherwise returns the
     * original FunctionCall object.
     */
    static RNode inline(FunctionCall.GenericCall call) {


        return call;
    }



    static class InlinedFunctionNoArgs extends RNode {

        @Child final FunctionCall.GenericCall call;
        @Child RNode inlinedBody;

        protected InlinedFunctionNoArgs(FunctionCall.GenericCall call, RNode inlinedBody) {
            this.call = call;
            this.inlinedBody = inlinedBody;
        }

        @Override
        public Object execute(Frame frame) {
            // first make sure that we still evaluate to the inlined version
            Object callable = call.callableExpr.execute(frame);
            if (callable != call.lastClosure) {
                CompilerDirectives.transferToInterpreter();
                fastr.println("Function " + call.callableExpr.getAST().toString()+" reevaluated, reverting to non-inlined version");
                this.replace(call);
                return call.execute(frame, callable);
            }
            // everything looks fine, proceed to the inlined version of the function
            return inlinedBody.execute(frame);
        }
    }
}
