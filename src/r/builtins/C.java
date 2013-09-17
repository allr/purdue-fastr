package r.builtins;

import com.oracle.truffle.api.nodes.*;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "c"
 *
 * <pre>
 * ... -- objects to be concatenated.
 * recursive -- logical. If recursive = TRUE, the function recursively descends through lists
 *              (and pairlists) combining all their elements into a vector.
 * </pre>
 */
// FIXME: the set of specializations already implemented is biased by the binarytrees benchmark
// TODO: do more specializations, obvious opportunities include: vectors of same type, same result type, perhaps something for lists as well
// TODO: implement "recursive" argument and note that once this is done, the code will become even closer to that of unlist (refactor)
final class C extends CallFactory {

    static final CallFactory _ = new C("c", new String[]{"...", "recursive"}, new String[]{});

    private C(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    // only supports a vector of integers, doubles, or logical
    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        RSymbol[] collapsedNames = collapseEmptyNames(names);
        if (exprs.length == 0) { return new Builtin.Builtin0(call, collapsedNames, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RNull.getNull();
            }
        }; }
        return Specialized.createUninitialized(call, collapsedNames, exprs);
    }

    private static <T extends RArray, U extends T> int fillIn(U result, T input, int offset) {
        int len = input.size();
        for (int i = 0; i < len; i++) {
            result.set(offset + i, input.get(i));
        }
        return offset + len;
    }

    private static int fillDoubleInComplex(RComplex result, RDouble input, int offset) {
        int len = input.size();
        for (int i = 0; i < len; i++) {
            result.set(offset + i, input.getDouble(i), 0);
        }
        return offset + len;
    }

    public static RAny genericCombine(RSymbol[] paramNames, RAny[] params) {
        return genericCombine(paramNames, params, false);
    }

    // drop names (only used from unlist, in combine always false)
    public static RAny genericCombine(RSymbol[] paramNames, RAny[] params, boolean dropNames) {
        int len = 0;
        boolean hasNames = (paramNames != null);
        boolean hasNull = false;
        boolean hasRaw = false;
        boolean hasLogical = false;
        boolean hasInt = false;
        boolean hasList = false;
        boolean hasDouble = false;
        boolean hasComplex = false;
        boolean hasString = false;
        for (int i = 0; i < params.length; i++) { // FIXME: maybe could refactor using the code in Unlist?
            RAny v = params[i];
            if (v instanceof RNull) {
                hasNull = true;
                continue;
            } else if (v instanceof RList) {
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
                throw Utils.nyi("unsupported type");
            }
            RArray a = (RArray) v;
            len += a.size();
            if (!dropNames && a.names() != null) {
                hasNames = true;
            }
        }
        int offset = 0;
        RArray.Names newNames = null;
        if (!dropNames && hasNames) {
            RSymbol[] names = new RSymbol[len];
            int j = 0;
            for (int i = 0; i < params.length; i++) {
                RAny v = params[i];
                if (v instanceof RNull) {
                    continue;
                }
                RArray a = (RArray) v;
                int asize = a.size();
                RArray.Names aNamesPacked = a.names();
                RSymbol[] aNames = aNamesPacked == null ? null : aNamesPacked.sequence();
                if (aNames == null) {
                    if (paramNames == null || paramNames[i] == null) {
                        for (int k = 0; k < asize; k++) {
                            names[j++] = RSymbol.EMPTY_SYMBOL;
                        }
                        continue;
                    }
                    if (asize == 1) {
                        names[j++] = paramNames[i];
                        continue;
                    }
                    if (asize == 0) {
                        continue;
                    }
                    String prefix = paramNames[i].pretty();
                    for (int k = 0; k < asize; k++) {
                        String n = prefix + (k + 1);
                        names[j++] = RSymbol.getSymbol(n);
                    }
                    continue;
                }
                // aNames != null
                if (paramNames == null || paramNames[i] == RSymbol.EMPTY_SYMBOL || paramNames[i] == null) {
                    for (int k = 0; k < asize; k++) {
                        names[j++] = aNames[k];
                    }
                    continue;
                }
                String eprefix = paramNames[i].pretty();
                String prefix = eprefix + ".";
                for (int k = 0; k < asize; k++) {
                    RSymbol ksymbol = aNames[k];
                    if (ksymbol == RSymbol.EMPTY_SYMBOL) {
                        if (asize == 1) {
                            names[j++] = paramNames[i];
                        } else {
                            names[j++] = RSymbol.getSymbol(eprefix + (k + 1));
                        }
                    } else {
                        String n = prefix + Convert.prettyNA(ksymbol.pretty());
                        names[j++] = RSymbol.getSymbol(n);
                    }
                }
            }
            newNames = RArray.Names.create(names);
        }
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
            return res.setNames(newNames);
        }
        if (hasString) {
            RString res = RString.RStringFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RString ? (RString) v : v.asString(), offset);
            }
            return res.setNames(newNames);
        }
        if (hasComplex) {
            RComplex res = RComplex.RComplexFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                if (v instanceof RDouble) { // NOTE: cannot use as.Complex(), before the semantics is different for NaN values
                    offset = fillDoubleInComplex(res, (RDouble) v, offset);
                } else {
                    offset = fillIn(res, v instanceof RComplex ? (RComplex) v : v.asComplex(), offset);
                }
            }
            return res.setNames(newNames);
        }
        if (hasDouble) {
            RDouble res = RDouble.RDoubleFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RDouble ? (RDouble) v : v.asDouble(), offset);
            }
            return res.setNames(newNames);
        }
        if (hasInt) {
            RInt res = RInt.RIntFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RInt ? (RInt) v : v.asInt(), offset);
            }
            return res.setNames(newNames);
        }
        if (hasLogical) {
            RLogical res = RLogical.RLogicalFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RLogical ? (RLogical) v : v.asLogical(), offset);
            }
            return res.setNames(newNames);
        }
        if (hasRaw) {
            RRaw res = RRaw.RRawFactory.getUninitializedArray(len);
            for (RAny v : params) {
                if (v instanceof RNull) {
                    continue;
                }
                offset = fillIn(res, v instanceof RRaw ? (RRaw) v : v.asRaw(), offset);
            }
            return res.setNames(newNames);
        }
        if (hasNull) { return RNull.getNull(); }
        throw Utils.nyi("Unreacheable");
    }

    public static Builtin createGeneric(ASTNode ast, final RSymbol[] names, RNode[] exprs) {
        return new Builtin(ast, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] params) {
                return genericCombine(names, params);
            }
        };
    }

    enum Transition {
        SIMPLE_SCALARS, CASTING_SCALARS, SIMPLE_VECTORS, GENERIC
    }

    public abstract static class CombineAction {
        public abstract RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException;
    }

    public static class Specialized extends Builtin {
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
                @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
                    throw new UnexpectedResultException(t);
                }
            };
            return new Specialized(ast, names, exprs, a);
        }

        public static Specialized createSimpleScalars(ASTNode ast, RSymbol[] names, RNode[] exprs, RAny typeTemplate) {
            if (names != null) { return createTransition(ast, names, exprs, Transition.GENERIC); }
            if (typeTemplate instanceof ScalarStringImpl) {
                CombineAction a = new CombineAction() {
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        String[] content = new String[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarStringImpl)) { throw new UnexpectedResultException(Transition.CASTING_SCALARS); }
                            content[i] = ((ScalarStringImpl) v).getString();
                        }
                        return RString.RStringFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarDoubleImpl) {
                CombineAction a = new CombineAction() {
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        double[] content = new double[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarDoubleImpl)) { throw new UnexpectedResultException(Transition.CASTING_SCALARS); }
                            content[i] = ((ScalarDoubleImpl) v).getDouble();
                        }
                        return RDouble.RDoubleFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarIntImpl) {
                CombineAction a = new CombineAction() {
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        int[] content = new int[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarIntImpl)) { throw new UnexpectedResultException(Transition.CASTING_SCALARS); }
                            content[i] = ((ScalarIntImpl) v).getInt();
                        }
                        return RInt.RIntFactory.getFor(content);
                    }
                };
                return new Specialized(ast, names, exprs, a);
            }
            if (typeTemplate instanceof ScalarLogicalImpl) {
                CombineAction a = new CombineAction() {
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
                        int size = params.length;
                        int[] content = new int[size];
                        for (int i = 0; i < size; i++) {
                            RAny v = params[i];
                            if (!(v instanceof ScalarLogicalImpl)) { throw new UnexpectedResultException(Transition.CASTING_SCALARS); }
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
            if (names != null) { return createTransition(ast, names, exprs, Transition.GENERIC); }
            if (typeTemplate instanceof RDouble) {
                CombineAction a = new CombineAction() {
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
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
                    @Override public final RAny combine(Frame frame, RAny[] params) throws UnexpectedResultException {
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

        @Override public final RAny doBuiltIn(Frame frame, RAny[] params) {
            try {
                return combine.combine(frame, params);
            } catch (UnexpectedResultException e) {
                Transition t = (Transition) e.getResult();
                Specialized s = null;
                switch (t) {
                case SIMPLE_SCALARS:
                    s = createSimpleScalars(ast, argNames, argExprs, params[0]);
                    replace(s, "install SimpleScalars in Combine.Specialized");
                    return s.doBuiltIn(frame, params);

                case CASTING_SCALARS:
                    RAny res = genericCombine(argNames, params);
                    s = createCastingScalars(ast, argNames, argExprs, res);
                    replace(s, "install CastingScalars in Combine.Specialized");
                    return res;

                case GENERIC:
                default:
                    replace(createGeneric(ast, argNames, argExprs), "install Generic in Combine.Specialized");
                    return genericCombine(argNames, params);
                }
            }
        }
    }

    public static RSymbol[] collapseEmptyNames(RSymbol[] names) {
        if (names == null) { return names; }
        for (RSymbol s : names) {
            if (s != null) { return names; }
        }
        return null;
    }

}
