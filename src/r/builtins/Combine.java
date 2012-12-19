package r.builtins;

import com.oracle.truffle.nodes.*;
import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: set of specializations already implemented is biased by binarytrees benchmark
// TODO: do more specializations, obvious opportunities include: vectors of same type, same result type, perhaps something for lists as well
public class Combine {

    private static <T extends RArray, U extends T> int fillIn(U result, T input, int offset) {
        int len = input.size();
        for (int i = 0; i < len; i++) {
            result.set(offset + i, input.get(i));
        }
        return offset + len;
    }

    public static RAny genericCombine(RAny[] params) {
        int len = 0;
        boolean hasNull = false;
        boolean hasRaw = false;
        boolean hasLogical = false;
        boolean hasInt = false;
        boolean hasList = false;
        boolean hasDouble = false;
        boolean hasComplex = false;
        boolean hasString = false;
        for (int i = 0; i < params.length; i++) {
            RAny v = params[i];

            if (v instanceof RNull) {
                hasNull = true;
                continue;
            }
            if (v instanceof RList) {
                hasList = true;
            } else if (v instanceof RString) {
                hasString = true;
            } else if (v instanceof RComplex) {
                hasComplex = true;
            } else if (v instanceof RDouble) {
                hasDouble = true;
            } else if (v instanceof RInt) {
                hasInt = true;
            } else if (v instanceof RLogical) {
                hasLogical = true;
            } else if (v instanceof RRaw) {
                hasRaw = true;
            } else {
                Utils.nyi("unsupported type");
                return null;
            }
            len += ((RArray) v).size();
        }
        int offset = 0;
        if (hasList) {
            ListImpl res = RList.RListFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                RArray a = (RArray) v;
                int asize = a.size();
                if (v instanceof RList) {
                    RList l = (RList) v;
                    for (int i = 0; i < asize; i++) { // shallow copy
                        RAny ll = l.getRAnyRef(i);
                        res.set(offset + i, ll);
                    }
                } else {
                    for (int i = 0; i < asize; i++) {
                        res.set(offset + i, a.boxedGet(i));
                    }
                }
                offset += asize;
            }
            return res;
        }
        if (hasString) {
            RString res = RString.RStringFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RString ? (RString) v : v.asString(), offset);
            }
            return res;
        }
        if (hasComplex) {
            RComplex res = RComplex.RComplexFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RComplex ? (RComplex) v : v.asComplex(), offset);
            }
            return res;
        }
        if (hasDouble) {
            RDouble res = RDouble.RDoubleFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RDouble ? (RDouble) v : v.asDouble(), offset);
            }
            return res;
        }
        if (hasInt) {
            RInt res = RInt.RIntFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RInt ? (RInt) v : v.asInt(), offset);
            }
            return res;
        }
        if (hasLogical) {
            RLogical res = RLogical.RLogicalFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RLogical ? (RLogical) v : v.asLogical(), offset);
            }
            return res;
        }
        if (hasRaw) {
            RRaw res = RRaw.RRawFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RRaw ? (RRaw) v : v.asRaw(), offset);
            }
            return res;
        }
        if (hasNull) {
            return RNull.getNull();
        }
        Utils.nyi("Unreacheable");
        return null;
    }

    public static BuiltIn createGeneric(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn(ast, names, exprs) {

            @Override
            public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
                return genericCombine(params);
            }
        };
    }

    enum Transition {
        SIMPLE_SCALARS,
        CASTING_SCALARS,
        SIMPLE_VECTORS,
        GENERIC
    }

    public abstract static class CombineAction {
        public abstract RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException;
    }

    public static class Specialized extends BuiltIn {
        CombineAction combine;

        public Specialized(ASTNode orig, RSymbol[] argNames, RNode[] argExprs, CombineAction combine) {
            super(orig, argNames, argExprs);
            this.combine = combine;
        }

        public static Specialized createUninitialized(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            return createTransition(ast, names, exprs, Transition.SIMPLE_SCALARS);
        }

        public static Specialized createTransition(ASTNode ast, RSymbol[] names, RNode[] exprs, final Transition t) {
            CombineAction a = new CombineAction() {
                @Override
                public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                    throw new UnexpectedResultException(t);
                }
            };
            return new Specialized(ast, names, exprs, a);
        }

        public static Specialized createSimpleScalars(ASTNode ast, RSymbol[] names, RNode[] exprs, RAny typeTemplate) {
            if (typeTemplate instanceof ScalarStringImpl) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        String[] content = new String[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarStringImpl)) {
                                throw new UnexpectedResultException(Transition.CASTING_SCALARS);
                            }
                            content[i] = ((ScalarStringImpl) v).getString();
                        }
                        return RString.RStringFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarDoubleImpl) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        double[] content = new double[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarDoubleImpl)) {
                                throw new UnexpectedResultException(Transition.CASTING_SCALARS);
                            }
                            content[i] = ((ScalarDoubleImpl) v).getDouble();
                        }
                        return RDouble.RDoubleFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarIntImpl) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        int[] content = new int[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarIntImpl)) {
                                throw new UnexpectedResultException(Transition.CASTING_SCALARS);
                            }
                            content[i] = ((ScalarIntImpl) v).getInt();
                        }
                        return RInt.RIntFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarLogicalImpl) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        int[] content = new int[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarLogicalImpl)) {
                                throw new UnexpectedResultException(Transition.CASTING_SCALARS);
                            }
                            content[i] = ((ScalarLogicalImpl) v).getLogical();
                        }
                        return RLogical.RLogicalFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            // FIXME: add support for strings
            return createTransition(ast, names, exprs, Transition.GENERIC);
        }

        public static Specialized createCastingScalars(ASTNode ast, RSymbol[] names, RNode[] exprs, RAny typeTemplate) {
            if (typeTemplate instanceof RDouble) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        double[] content = new double[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (v instanceof ScalarDoubleImpl) {
                                content[i] = ((ScalarDoubleImpl) v).getDouble();
                                continue;
                            }
                            if (v instanceof ScalarIntImpl) {
                                content[i] = Convert.int2double(((ScalarIntImpl) v).getInt());
                                continue;
                            }
                            if (v instanceof ScalarLogicalImpl) {
                                content[i] = Convert.logical2double(((ScalarLogicalImpl) v).getLogical());
                                continue;
                            }
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        return RDouble.RDoubleFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof RInt) {
                CombineAction a = new CombineAction() {
                    @Override
                    public final RAny combine(RContext context, Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        int[] content = new int[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (v instanceof ScalarIntImpl) {
                                content[i] = ((ScalarIntImpl) v).getInt();
                                continue;
                            }
                            if (v instanceof ScalarLogicalImpl) {
                                content[i] = Convert.logical2int(((ScalarLogicalImpl) v).getLogical());
                                continue;
                            }
                            throw new UnexpectedResultException(Transition.GENERIC);
                        }
                        return RInt.RIntFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            return createTransition(ast, names, exprs, Transition.GENERIC);
        }
        @Override
        public final RAny doBuiltIn(RContext context, Frame frame, RAny[] params) {
            try {
                return combine.combine(context, frame, params);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                Specialized s = null;
                switch(t) {
                    case SIMPLE_SCALARS:
                        s = createSimpleScalars(ast, argNames, argExprs, params[0]);
                        replace(s, "install SimpleScalars in Combine.Specialized");
                        return s.doBuiltIn(context, frame, params);

                    case CASTING_SCALARS:
                        RAny res = genericCombine(params);
                        s = createCastingScalars(ast, argNames, argExprs, res);
                        replace(s, "install CastingScalars in Combine.Specialized");
                        return res;

                    case GENERIC:
                    default:
                        replace(createGeneric(ast, argNames, argExprs), "install Generic in Combine.Specialized");
                        return genericCombine(params);
                }
             }
        }
    }

    public static final CallFactory FACTORY = new CallFactory() {

        // only supports a vector of integers, doubles, or logical
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RNull.getNull();
                    }

                };
            }
            return Specialized.createUninitialized(call, names, exprs);
        }
    };
}
