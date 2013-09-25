package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

/**
 * "seq"
 *
 * <pre>
 * from, to -- the starting and (maximal) end values of the sequence. Of length 1 unless just from is supplied
 *        as an unnamed argument.
 *  by -- number: increment of the sequence.
 *  length.out -- desired length of the sequence. A non-negative number, which for seq and seq.int will be
 *        rounded up if fractional.
 * along.with -- take the length from the length of this argument.
 * ... -- arguments passed to or from methods.
 * </pre>
 */
// FIXME: this would have been easier to write in R
//        GNU R has this written in R, but the code depends on too many things we don't support yet
final class Seq extends CallFactory {

    static final CallFactory _ = new Seq("seq", new String[]{"from", "to", "by", "length.out", "along.with", "..."}, new String[]{});

    Seq(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        // LICENSE: transcribed code from GNU R, which is licensed under GPL
        if (exprs.length == 0) { return new Builtin.Builtin0(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame) {
                return RInt.RIntFactory.getScalar(1);
            }
        }; }
        ArgumentInfo ia = check(call, names, exprs);
        final int posFrom = ia.position("from");
        final int posTo = ia.position("to");
        final int posBy = ia.position("by");
        final int posLengthOut = ia.position("length.out");
        // handle common cases statically
        if (posFrom != -1 && posTo != -1) {
            if (exprs.length == 2) {
                // from:to
                if (posFrom == 0) { //
                    return Colon._.create(call, names, exprs);
                } else {
                    RSymbol[] newNames = new RSymbol[2];
                    newNames[0] = names[1];
                    newNames[1] = names[0];
                    RNode[] newExprs = new RNode[2];
                    newExprs[0] = exprs[1];
                    newExprs[1] = exprs[0];
                    return Colon._.create(call, newNames, newExprs);
                }
            }
            if (exprs.length == 3) {
                if (posBy != -1) {
                    //from, to, by
                    return new Builtin(call, names, exprs) {
                        // note: does not implement the full semantics
                        @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                            RAny argfrom = args[posFrom]; // FIXME: will this be optimized out?
                            RAny argto = args[posTo];
                            RAny argby = args[posBy];

                            if (!(argfrom instanceof RArray && argto instanceof RArray && argby instanceof RArray)) {
                                Utils.nyi("unsupported argument types");
                            }

                            RArray afrom = (RArray) argfrom;
                            RArray ato = (RArray) argto;
                            RArray aby = (RArray) argby;

                            Colon.checkScalar(afrom, ast);
                            Colon.checkScalar(ato, ast);
                            Colon.checkScalar(aby, ast);

                            // FIXME: perhaps could be optimized for integer-only case
                            double from = afrom.asDouble().getDouble(0);
                            double to = ato.asDouble().getDouble(0);
                            double by = aby.asDouble().getDouble(0);

                            double del = to - from;
                            if (del == 0 && to == 0) { return argto; }
                            double n = del / by;
                            if (!RDouble.RDoubleUtils.isFinite(n)) {
                                if (by == 0 && del == 0) { return argfrom; }
                                throw RError.getInvalidTFB(ast);
                            }
                            if (n < 0) { throw RError.getWrongSignInBy(ast); }
                            if (n > java.lang.Integer.MAX_VALUE) { throw RError.getByTooSmall(ast); }
                            double dd = Math.abs(del) / Math.max(Math.abs(to), Math.abs(from));
                            if (dd < 100 * RDouble.EPSILON) { return argfrom; }
                            if (ato instanceof RInt && afrom instanceof RInt && aby instanceof RInt) {
                                return RInt.RIntFactory.forSequence((int) from, (int) (from + ((int) n) * by), (int) by);
                            } else {
                                int in = (int) (n + 1e-10);
                                double[] content = new double[in + 1];
                                if (by > 0) {
                                    for (int i = 0; i <= in; i++) {
                                        double x = from + i * by;
                                        content[i] = x <= to ? x : to;
                                    }
                                } else {
                                    for (int i = 0; i <= in; i++) {
                                        double x = from + i * by;
                                        content[i] = x >= to ? x : to;
                                    }
                                }
                                return RDouble.RDoubleFactory.getFor(content);
                            }
                        }
                    };
                }
                if (posLengthOut != -1) {
                    // from, to, length.out
                    return new Builtin(call, names, exprs) {

                        // note: does not implement the full semantics
                        @Override public RAny doBuiltIn(Frame frame, RAny[] args) {

                            RAny argfrom = args[posFrom]; // FIXME: will this be optimized out ?
                            RAny argto = args[posTo];
                            RAny arglengthOut = args[posLengthOut];

                            if (!(argfrom instanceof RArray && argto instanceof RArray && arglengthOut instanceof RArray)) { throw Utils.nyi("unsupported argument types"); }

                            RArray afrom = (RArray) argfrom;
                            RArray ato = (RArray) argto;
                            RArray alengthOut = (RArray) arglengthOut;

                            Colon.checkScalar(afrom, ast);
                            Colon.checkScalar(ato, ast);
                            Colon.checkScalar(alengthOut, ast);

                            double from = afrom.asDouble().getDouble(0);
                            double to = ato.asDouble().getDouble(0);
                            double lengthOut = alengthOut.asDouble().getDouble(0);

                            if (!RDouble.RDoubleUtils.isFinite(lengthOut) || lengthOut < 0) { throw RError.getLengthNonnegative(ast); }
                            if (lengthOut == 0) { return RInt.EMPTY; }
                            if (lengthOut == 1) { return argfrom; }
                            if (lengthOut == 2) { return C.genericCombine(null, new RAny[]{argfrom, argto}); }
                            if (from == to) { return Rep.genericRepInt(ast, argfrom, arglengthOut); }

                            double by = ((to - from) / (lengthOut - 1)); // FIXME: this may not reflect exactly R semantics in corner cases

                            // FIXME: could do a view here
                            int len = (int) lengthOut;
                            RAny[] vec = new RAny[len];
                            vec[0] = argfrom;
                            vec[vec.length - 1] = argto;
                            for (int i = 1; i <= len - 2; i++) {
                                vec[i] = RDouble.RDoubleFactory.getScalar(from + i * by);
                            }
                            return C.genericCombine(null, vec);
                        }
                    };
                }
            }
        } else {
            if (exprs.length == 1) {
                if (ia.provided("along.with")) { return new Builtin.Builtin1(call, names, exprs) {

                    // note: some error messages are not exactly like in R, but they are quite close
                    @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                        if (!(arg instanceof RArray)) { throw Utils.nyi(); }
                        RArray aarg = (RArray) arg;
                        int len = aarg.size();
                        if (len == 0) { return RInt.EMPTY; }
                        return Colon.create(1, len);
                    }
                }; }
                if (ia.provided("from")) { return new Builtin.Builtin1(call, names, exprs) {
                    // note: some error messages are not exactly like in R, but they are quite close
                    @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                        if (arg instanceof RArray) {
                            RArray aarg = (RArray) arg;
                            int len = aarg.size();
                            if (len == 0) { return RInt.EMPTY; }
                            if (len == 1) {
                                if (aarg instanceof RInt) {
                                    int arginfo = ((RInt) aarg).getInt(0);
                                    Colon.checkNA(arginfo, ast);
                                    return Colon.create(1, arginfo);
                                }
                                if (aarg instanceof RDouble) {
                                    double da = ((RDouble) aarg).getDouble(0);
                                    Colon.checkNAandNaN(da, ast);
                                    if (RDouble.RDoubleUtils.fitsRInt(da)) {
                                        return Colon.create(1, (int) da);
                                    } else {
                                        return Colon.create(1.0, da);
                                    }
                                }
                            }
                            return Colon.create(1, len);
                        }
                        throw Utils.nyi();
                    }
                }; }
                if (ia.provided("length.out")) { return new Builtin.Builtin1(call, names, exprs) {

                    // note: some error messages are not exactly like in R, but they are quite close
                    @Override public RAny doBuiltIn(Frame frame, RAny arg) {

                        if (arg instanceof RInt) {
                            RInt lint = (RInt) arg;
                            Colon.checkScalar(lint, ast);
                            int li = lint.getInt(0);
                            if (li == 0) { return RInt.EMPTY; }
                            if (li < 0) { throw RError.getLengthNonnegative(ast); }
                            Colon.checkNA(li, ast);
                            return Colon.create(1, li);
                        }
                        if (arg instanceof RDouble) {
                            RDouble ldbl = (RDouble) arg;
                            Colon.checkScalar(ldbl, ast);
                            double ld = ldbl.getDouble(0);
                            if (RDouble.RDoubleUtils.isNAorNaN(ld) || ld < 0) { throw RError.getLengthNonnegative(ast); }
                            Colon.checkNAandNaN(ld, ast);
                            if (ld == 0) { return RInt.EMPTY; }
                            double id = Math.ceil(ld);
                            if (RDouble.RDoubleUtils.fitsRInt(id)) {
                                return Colon.create(1, (int) id);
                            } else {
                                return Colon.create(1.0, ld);
                            }
                        }
                        if (arg instanceof RLogical) {
                            RLogical llog = (RLogical) arg;
                            Colon.checkScalar(llog, ast);
                            int ll = llog.getLogical(0);
                            if (ll == RLogical.TRUE) { return RInt.BOXED_ONE; }
                            if (ll == RLogical.FALSE) { return RInt.EMPTY; }
                            throw RError.getLengthNonnegative(ast);
                        }

                        return RInt.RIntFactory.getScalar(1);
                    }

                }; }
            }
        }
        throw Utils.nyi("General case for seq to be implemented (in R?)");
    }
}
