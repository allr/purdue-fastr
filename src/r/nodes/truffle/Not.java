package r.nodes.truffle;

import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.api.nodes.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;

// FIXME: this could and should be performance optimized
public abstract class Not extends BaseR {
    @Child RNode lhs;

    Not(ASTNode ast, RNode lhs) {
        super(ast);
        this.lhs = adoptChild(lhs);
    }

    @Override
    public final Object execute(Frame frame) {
        RAny value = (RAny) lhs.execute(frame);
        return execute(value);
    }

    abstract RAny execute(RAny value);

    // when the argument is a logical scalar
    public static class LogicalScalar extends Not { // TODO: optimize this using scalar types and executeScalarLogical
        public LogicalScalar(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RAny execute(RAny value) {
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
                RawScalar n = new RawScalar(ast, lhs);  // FIXME: also create a specialized note for a logical vector
                replace(n, "install RawScalar from LogicalScalar");
                return n.execute(value);
            }
        }
    }

    // when the argument is a raw scalar
    public static class RawScalar extends Not {
        public RawScalar(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RAny execute(RAny value) {
            try {
                if (!(value instanceof RRaw)) {
                    throw new UnexpectedResultException(null);
                }
                RRaw rvalue = (RRaw) value;
                if (rvalue.size() != 1) {
                    throw new UnexpectedResultException(null);
                }
                byte b = rvalue.getRaw(0);
                return RRaw.RRawFactory.getScalar((byte) ~b);
            } catch (UnexpectedResultException e) {
                Generic gn = new Generic(ast, lhs);
                replace(gn, "install Generic from LogicalScalar");
                return gn.execute(value);
            }
        }
    }

    public static class Generic extends Not {
        public Generic(ASTNode ast, RNode lhs) {
            super(ast, lhs);
        }

        @Override
        RAny execute(RAny value) {
            if (value instanceof RLogical || value instanceof RDouble || value instanceof RInt) {
                final RLogical lvalue = value.asLogical();
                final int vsize = lvalue.size();

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

                    @Override
                    public boolean isSharedReal() {
                        return lvalue.isShared();
                    }

                    @Override
                    public void ref() {
                        lvalue.ref();
                    }
                };
            }
            if (value instanceof RRaw) {
                final RRaw rvalue = (RRaw) value;
                final int vsize = rvalue.size();

                return new View.RRawView() {
                    @Override
                    public int size() {
                        return vsize;
                    }

                    @Override
                    public byte getRaw(int i) {
                        byte v = rvalue.getRaw(i);
                        return (byte) ~v;
                    }

                    @Override
                    public boolean isSharedReal() {
                        return rvalue.isShared();
                    }

                    @Override
                    public void ref() {
                        rvalue.ref();
                    }
                };
            }
            if (value instanceof RArray) {
                if (((RArray) value).size() == 0) {
                    return RLogical.EMPTY;
                }
            }
            throw RError.getInvalidArgType(ast);
        }
    }
}
