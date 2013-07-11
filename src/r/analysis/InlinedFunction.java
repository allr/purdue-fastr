package r.analysis;


import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.*;
import r.*;
import r.analysis.visitors.*;
import r.data.internal.FunctionImpl;
import r.nodes.truffle.*;

/** Function inlining. */
public class InlinedFunction {

    /**
     * Determines whether the given function should be inlined and if so, returns the new root. Otherwise returns the
     * original FunctionCall object.
     */
    public static RNode analyze(FunctionCall.GenericCall call, Frame frame) {
        try {
            FunctionImpl fimpl = (FunctionImpl) call.lastClosure.function();
            WriteSet a = WriteSet.analyze(fimpl.body(), fimpl);
            if (a.isEmpty() && fimpl.nparams() == 0) {
                fastr.println("Inlining the nodes because the writeset is empty and no arguments are present");
                return new NoArgs(call, fimpl);
            } else if (a.isArgumentsOnly()) {
                // we must make sure that we are not top level - that is we support local variables ourselves
                if (frame != null) {
                    fastr.println("inlining function with args only read");
                    return new ArgsOnly(call, fimpl, frame.getFrameDescriptor());
                } else {
                    fastr.println("Arguments only function in top level, cannot inline yet");
                }
            }
        } catch (Exception e) {
            // pass to return the call itself if anything goes wrong
        }
        return call;
    }


    static class NoArgs extends RNode {

        @DoNotVisit
        @Child
        final FunctionCall.GenericCall call;
        @Child
        RNode inlinedBody;

        protected NoArgs(FunctionCall.GenericCall call, FunctionImpl fimpl) {
            this.call = call;
            this.inlinedBody = fimpl.body().deepCopy(); // no  magic since the writeset of the function is empty
        }

        @Override
        public Object execute(Frame frame) {
            // first make sure that we still evaluate to the inlined version
            Object callable = call.callableExpr.execute(frame);
            if (callable != call.lastClosure) {
                CompilerDirectives.transferToInterpreter();
                fastr.println("Function " + call.callableExpr.getAST().toString() + " reevaluated, reverting to non-inlined version");
                this.replace(call);
                return call.execute(frame, callable);
            }
            // everything looks fine, proceed to the inlined version of the function
            return inlinedBody.execute(frame);
        }
    }

    static class ArgsOnly extends RNode {

        @DoNotVisit
        @Child
        final FunctionCall.GenericCall call;
        @Child
        RNode inlinedBody;

        final FrameSlot[] argSlots;

        protected ArgsOnly(FunctionCall.GenericCall call, FunctionImpl fimpl, FrameDescriptor fd) {
            this.call = call;
            this.inlinedBody = fimpl.body().deepCopy();
            argSlots = LocalReadWriteReplacer.execute(fd, fimpl.paramNames(), inlinedBody);
        }

        @Override
        public Object execute(Frame frame) {
            // first make sure that we still evaluate to the inlined version
            Object callable = call.callableExpr.execute(frame);
            if (callable != call.lastClosure) {
                CompilerDirectives.transferToInterpreter();
                fastr.println("Function " + call.callableExpr.getAST().toString() + " reevaluated, reverting to non-inlined version");
                this.replace(call);
                return call.execute(frame, callable);
            }
            // now make sure that the extension slots are empty, that is that their values
            // TODO this can be done better, but for the time being this should do just fine
//            Object[] argValues = call.placeArgs(frame, call.functionArgPositions, call.functionDotsInfo, call.closureFunction.dotsIndex(), call.closureFunction.nparams());
            for (int i = 0; i < argSlots.length; ++i)
                Utils.frameSetObject(frame, argSlots[i], call.argExprs[i].execute(frame));
            // when the arguments are placed, call the inlined body itself.
            return inlinedBody.execute(frame);
        }
    }
}
