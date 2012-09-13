package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;

public abstract class BuiltIn {

    @SuppressWarnings("unused")
    public RAny fire(RContext context, RFrame frame, RAny[] args) {
        throw RError.getNYI("No generic case for " + args.length + " arguments");
    }

    @SuppressWarnings("unused")
    public RAny fire(RContext context, RFrame frame) {
        throw RError.getNYI("No special case for 0 args");
    }

    @SuppressWarnings("unused")
    public RAny fire(RContext context, RFrame frame, RAny arg) {
        throw RError.getNYI("No special case for 1 args");
    }

    @SuppressWarnings("unused")
    public RAny fire(RContext context, RFrame frame, RAny arg0, RAny arg1) {
        throw RError.getNYI("No special case for 2 args");
    }

    abstract static class BuiltIn0 extends BuiltIn {

        @Override
        public final RAny fire(RContext context, RFrame frame, RAny[] params) {
            return fire(context, frame);
        }
    }

    abstract static class BuiltIn1 extends BuiltIn {

        @Override
        public final RAny fire(RContext context, RFrame frame, RAny[] params) {
            return fire(context, frame, params[0]);
        }
    }

    abstract static class BuiltIn2 extends BuiltIn {

        @Override
        public final RAny fire(RContext context, RFrame frame, RAny[] params) {
            return fire(context, frame, params[0], params[1]);
        }
    }

    abstract static class BuiltInFactory implements CallFactory {
        public RNode create(final FunctionCall call, final RSymbol[] names, final RNode[] exprs) {
            final BuiltIn body = createBuiltIn(call, names, exprs);

            switch (exprs.length) {
                case 0:
                    return new BaseR(call) {

                        @Override
                        public Object execute(RContext context, RFrame frame) {
                            return body.fire(context, frame);
                        }
                    };
                case 1:
                    return new BaseR(call) {

                        @Override
                        public Object execute(RContext context, RFrame frame) {
                            return body.fire(context, frame, (RAny) exprs[0].execute(context, frame));
                        }
                    };
                case 2:
                    return new BaseR(call) {

                        @Override
                        public Object execute(RContext context, RFrame frame) {
                            return body.fire(context, frame, (RAny) exprs[0].execute(context, frame), (RAny) exprs[1].execute(context, frame));
                        }
                    };
                default:
                    return new BaseR(call) {

                        @Override
                        public Object execute(RContext context, RFrame frame) {
                            return body.fire(context, frame, evalArgs(context, frame));
                        }

                        private RAny[] evalArgs(RContext context, RFrame frame) {
                            int len = exprs.length;
                            RAny[] args = new RAny[len];
                            for (int i = 0; i < len; i++) {
                                args[i] = (RAny) exprs[i].execute(context, frame);
                            }
                            return args;
                        }

                    };
            }
        }

        protected abstract BuiltIn createBuiltIn(final FunctionCall call, final RSymbol[] names, final RNode[] exprs);
    }
}
