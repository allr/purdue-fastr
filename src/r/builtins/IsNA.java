package r.builtins;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: add more specializations, e.g. for a numeric vector
// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
public class IsNA {

    public static RAny generic(RAny arg) {
        if (arg instanceof RArray) {
            final RArray a = (RArray) arg;
            final int asize = a.size();
            if (asize == 1) {
                return a.isNAorNaN(0) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
            }
            if (asize > 1) {
                return new View.RLogicalView() {

                    @Override
                    public int size() {
                        return asize;
                    }

                    @Override
                    public int getLogical(int i) {
                        return a.isNAorNaN(i) ? RLogical.TRUE : RLogical.FALSE;
                    }
                };
            }
            // asize == 0
            return RLogical.EMPTY;
        }
        Utils.nyi("unsupported argument");
        return null;
    }

    public static BuiltIn createGeneric(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn(ast, names, exprs) {
            @Override
            public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                return generic(params[0]);
            }
        };
    }

    enum Transition {
        SCALAR,
        GENERIC
    }

    public abstract static class IsNAAction {
        public abstract RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException;
    }

    public static class Specialized extends BuiltIn {
        IsNAAction isNA;

        public Specialized(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, IsNAAction isNA) {
            super(orig, argNames, argExprs);
            this.isNA = isNA;
        }

        public static Specialized createUninitialized(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            return createTransition(ast, names, exprs, Transition.SCALAR);
        }

        public static Specialized createTransition(ASTNode ast, RSymbol[] names, RNode[] exprs, final Transition t) {
            IsNAAction a = new IsNAAction() {
                @Override
                public final RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException {
                    throw new UnexpectedResultException(t);
                }
            };
            return new Specialized(ast, names, exprs, a);
        }

        private static void checkScalar(RArray a, Transition t) throws UnexpectedResultException {
            if (a.size() != 1) {
                throw new UnexpectedResultException(t);
            }
        }

        public static Specialized createScalar(ASTNode ast, RSymbol[] names, RNode[] exprs, RAny typeTemplate) {
            if (typeTemplate instanceof RDouble) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RDouble)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RDouble v = (RDouble) param;
                        checkScalar(v, Transition.GENERIC);
                        return RDouble.RDoubleUtils.isNAorNaN(v.getDouble(0)) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RInt) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RInt)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RInt v = (RInt) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getInt(0) == RInt.NA ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RLogical) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RLogical)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RLogical v = (RLogical) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getLogical(0) == RLogical.NA ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RString) {
                IsNAAction a = new IsNAAction() {
                    @Override
                    public final RLogical isNA(RContext context, Frame frame, RAny param) throws UnexpectedResultException {
                        if (!(param instanceof RString)) {
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        RString v = (RString) param;
                        checkScalar(v, Transition.GENERIC);
                        return v.getString(0) == RString.NA ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            return createTransition(ast, names, exprs, Transition.GENERIC);
        }

        @Override
        public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
            RAny param = params[0];
            try {
                return isNA.isNA(context, frame, param);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                Specialized s = null;
                switch(t) {
                    case SCALAR:
                        s = createScalar(ast, argNames, argExprs, param);
                        replace(s, "install SimpleScalars in IsNA.Specialized");
                        return s.doBuiltIn(context, frame, params);

                    case GENERIC:
                    default:
                        replace(createGeneric(ast, argNames, argExprs), "install Generic in IsNA.Specialized");
                        return generic(param);
                }
             }
        }
    }

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return Specialized.createUninitialized(call, names, exprs);
        }
    };
}

