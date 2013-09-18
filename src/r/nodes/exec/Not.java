package r.nodes.exec;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.runtime.*;

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

    @Override
    protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
        assert oldNode != null;
        if (lhs == oldNode) {
            lhs = newNode;
            return adoptInternal(newNode);
        }
        return super.replaceChild(oldNode, newNode);
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
                if (value instanceof ScalarLogicalImpl) {
                    switch(((ScalarLogicalImpl) value).getLogical()) {
                        case RLogical.TRUE: return RLogical.BOXED_FALSE;
                        case RLogical.FALSE: return RLogical.BOXED_TRUE;
                        default: return RLogical.BOXED_NA;
                    }
                } else {
                    throw new SpecializationException(null);
                }
            } catch (SpecializationException e) {
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
                    throw new SpecializationException(null);
                }
                RRaw rvalue = (RRaw) value;
                // TODO: get rid of this, perhaps by creating a ScalarRawImpl type
                if (rvalue.size() != 1 || rvalue.dimensions() != null || rvalue.names() != null || rvalue.attributes() != null) {
                    throw new SpecializationException(null);
                }
                byte b = rvalue.getRaw(0);
                return RRaw.RRawFactory.getScalar((byte) ~b);
            } catch (SpecializationException e) {
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

                return new View.RLogicalProxy<RLogical>(lvalue) {

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
                    public Attributes attributes() {
                        return null; // drop attributes
                        // FIXME: the RLogicalProxy mark the attributes shared unnecessarily
                    }
                };
            }
            if (value instanceof RRaw) {
                final RRaw rvalue = (RRaw) value;

                return new View.RRawProxy<RRaw>(rvalue) {

                    @Override
                    public byte getRaw(int i) {
                        byte v = rvalue.getRaw(i);
                        return (byte) ~v;
                    }

                    @Override
                    public Attributes attributes() {
                        return null; // drop attributes
                        // FIXME: the RLogicalProxy mark the attributes shared unnecessarily
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
