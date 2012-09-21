package r.nodes.truffle;

import com.oracle.truffle.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

public abstract class Not extends BaseR {
    RNode lhs;

    Not(ASTNode ast, RNode lhs) {
        super(ast);
        this.lhs = updateParent(lhs);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RAny value = (RAny) lhs.execute(context, frame);
        return execute(context, frame, value);
    }

    abstract RLogical execute(RContext context, RFrame frame, RAny value);

    // when the argument is a logical scalar
    public static class LogicalScalar extends Not {
        public LogicalScalar(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RLogical execute(RContext context, RFrame frame, RAny value) {
            try {
                if (!(value instanceof RLogical)) {
                    throw new UnexpectedResultException(null);
                }
                RLogical lvalue = (RLogical) value;
                if (lvalue.size() != 1) {
                    throw new UnexpectedResultException(null);
                }
                int l = lvalue.getLogical(0);
                switch(l) {
                    case RLogical.TRUE: return RLogical.BOXED_FALSE;
                    case RLogical.FALSE: return RLogical.BOXED_TRUE;
                    default: return RLogical.BOXED_NA;
                }
            } catch (UnexpectedResultException e) {
                Generic gn = new Generic(ast, lhs);
                replace(gn, "install Generic from LogicalScalar");
                return gn.execute(context, frame, value);
            }
        }
    }

    public static class Generic extends Not {
        public Generic(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RLogical execute(RContext context, RFrame frame, RAny value) {
            final RLogical lvalue = value.asLogical();
            final int vsize = lvalue.size();
            if (vsize == 0) {
                throw RError.getInvalidArgType(ast);
            }
            return new View.RLogicalView() {
                @Override
                public int size() {
                    return vsize;
                }

                @Override
                public int getLogical(int i) {
                    int l = lvalue.getLogical(i);
                    if (l == RLogical.TRUE) {
                        return RLogical.FALSE;
                    } else if (l == RLogical.FALSE) {
                        return RLogical.TRUE;
                    } else {
                        return RLogical.NA;
                    }
                }
            };
        }
    }
}
