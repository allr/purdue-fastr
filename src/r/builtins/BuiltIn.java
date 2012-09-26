package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

public abstract class BuiltIn extends AbstractCall {

    public BuiltIn(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
        super(orig, argNames, argExprs);
    }

    abstract static class BuiltIn0 extends BuiltIn {

        public BuiltIn0(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public Object executeHelper(Context context, Frame frame) {
            return doBuiltIn((RContext) context, frame);
        }

        public abstract RAny doBuiltIn(RContext context, Frame frame);

        @Override
        public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
            // TODO or not runtime test, since it's not the entry point
            return doBuiltIn(context, frame);
        }
    }

    abstract static class BuiltIn1 extends BuiltIn {

        public BuiltIn1(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public Object executeHelper(Context context, Frame frame) {
            RContext rcontext = (RContext) context;
            return doBuiltIn(rcontext, frame, (RAny) argExprs[0].execute(rcontext, frame));
        }

        public abstract RAny doBuiltIn(RContext context, Frame frame, RAny arg);

        @Override
        public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
            // TODO or not runtime test, since it's not the entry point
            return doBuiltIn(context, frame, params[0]);
        }
    }

    abstract static class BuiltIn2 extends BuiltIn {

        public BuiltIn2(ASTNode orig, RSymbol[] argNames, RNode[] argExprs) {
            super(orig, argNames, argExprs);
        }

        @Override
        public Object executeHelper(Context context, Frame frame) {
            RContext rcontext = (RContext) context;
            return doBuiltIn(rcontext, frame, (RAny) argExprs[0].execute(rcontext, frame), (RAny) argExprs[1].execute(rcontext, frame));
        }

        public abstract RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1);

        @Override
        public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
            return doBuiltIn(context, frame, params[0], params[1]);
        }
    }

    @Override
    public Object executeHelper(Context context, Frame frame) {
        RContext rcontext = (RContext) context;
        return doBuiltIn(rcontext, frame, evalArgs(rcontext, frame));
    }

    public abstract RAny doBuiltIn(RContext context, Frame frame, RAny[] params);

    private RAny[] evalArgs(RContext context, Frame frame) {
        int len = argExprs.length;
        RAny[] args = new RAny[len];
        for (int i = 0; i < len; i++) {
            args[i] = (RAny) argExprs[i].execute(context, frame);
        }
        return args;
    }
}
