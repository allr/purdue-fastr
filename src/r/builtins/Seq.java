package r.builtins;

import r.*;
import r.Truffle.Frame;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: this would have been easier to write in R
//        GNU R has this written in R, but the code depends on too many things we don't support yet
final class Seq extends CallFactory {

    static final CallFactory _ = new Seq("seq", new String[]{"from", "to", "by", "length.out", "along.with", "..."}, new String[]{});

    Seq(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        // TODO: this is only a very small subset of R functionality, names are ignored
        // TODO: the error handling is not like in R

        if (names.length == 1) { return new Builtin.Builtin1(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RDouble) {
                    RDouble d = (RDouble) arg;
                    if (d.size() != 1) { throw Utils.nyi(); }
                    return Colon.create(1, d.getDouble(0));
                }
                if (arg instanceof RInt) {
                    RInt i = (RInt) arg;
                    if (i.size() != 1) { throw Utils.nyi(); }
                    int imax = i.getInt(0);
                    return RInt.RIntFactory.forSequence(1, imax, imax >= 1 ? 1 : -1);
                }
                throw Utils.nyi("unsupported type");
            }
        }; }

        final int posFrom = ia.position("from");
        final int posTo = ia.position("to");
        final int posBy = ia.position("by");

        if (names.length == 3) { return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny argfrom = args[posFrom];
                RAny argto = args[posTo];
                RAny argby = args[posBy];

                if (argfrom instanceof RDouble || argto instanceof RDouble || argby instanceof RDouble) {
                    double from = extractDouble(argfrom, ast);
                    double to = extractDouble(argto, ast);
                    double by = extractDouble(argby, ast);

                    if (by > 0) {
                        int size = (int) ((to - from) / by) + 1;
                        // TODO: note exactly R semantics
                        double[] res = new double[size];
                        double x = from;
                        int i = 0;
                        while (x <= to + RDouble.EPSILON) { // TODO: handle rounding errors
                            res[i++] = x;
                            x += by;
                        }
                        return RDouble.RDoubleFactory.getFor(res);
                    } else {
                        int size = (int) ((from - to) / by) + 1;
                        // TODO: note exactly R semantics
                        double[] res = new double[size];
                        double x = from;
                        int i = 0;
                        while (x >= to - RDouble.EPSILON) { // TODO: handle rounding errors
                            res[i++] = x;
                            x -= by;
                        }
                        return RDouble.RDoubleFactory.getFor(res);
                    }
                } else {
                    int from = extractInt(argfrom, ast);
                    int to = extractInt(argto, ast);
                    int by = extractInt(argby, ast);
                    return RInt.RIntFactory.forSequence(from, to, by);
                }
            }
        }; }

        throw Utils.nyi("seq (variant) to be implemented");
    }

    public static double extractDouble(RAny d, ASTNode ast) {
        RDouble x = Convert.coerceToDoubleError(d, ast);
        if (x.size() != 1) { throw Utils.nyi(); }
        double res = x.getDouble(0);
        if (!RDouble.RDoubleUtils.isFinite(res)) { throw Utils.nyi("non-finite range"); }
        return res;

    }

    public static int extractInt(RAny d, ASTNode ast) {
        if (!(d instanceof RInt)) {
            Utils.nyi("unsupported type");
        }
        RInt x = (RInt) d;
        if (x.size() != 1) { throw Utils.nyi(); }
        int res = x.getInt(0);
        if (res == RInt.NA) { throw Utils.nyi("NA range"); }
        return res;

    }
}
