package r.builtins;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class TypeCheck {

    public abstract static class CheckAction {
        public abstract boolean is(RAny arg);
    }

    public static final class CheckCallFactory extends CallFactory {
        private final CheckAction action;

        public CheckCallFactory(CheckAction action) {
            this.action = action;
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);

            return new AbstractCall(call, names, exprs) {

                @Override
                public Object execute(RContext context, Frame frame) {
                    try {
                        return RLogical.RLogicalFactory.getScalar(executeScalarLogical(context, frame));
                    } catch (UnexpectedResultException e) {
                        return e.getResult();
                    }
                }

                @Override
                public int executeScalarLogical(RContext context, Frame frame) throws UnexpectedResultException {
                    RAny value = (RAny) argExprs[0].execute(context, frame);
                    return action.is(value) ? RLogical.TRUE : RLogical.FALSE;
                }
            };
        }
    }

    public static final CallFactory STRING_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RString;
        }

    });

    public static final CallFactory COMPLEX_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RComplex;
        }

    });

    public static final CallFactory DOUBLE_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RDouble;
        }

    });

    public static final CallFactory INT_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RInt;
        }

    });

    public static final CallFactory LOGICAL_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RLogical;
        }

    });

    public static final CallFactory RAW_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RRaw;
        }

    });

    public static final CallFactory LIST_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RList;
        }

    });

    public static final CallFactory NUMERIC_FACTORY = new CheckCallFactory(new CheckAction() {

        @Override
        public boolean is(RAny arg) {
            return arg instanceof RNumber;
        }

    });
}
