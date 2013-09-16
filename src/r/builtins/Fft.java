package r.builtins;

import r.data.*;
import r.data.RComplex.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

// main/fourier.c
// appl/fft.c
final class Fft extends CallFactory {

    static final CallFactory _ = new Fft("fft", new String[]{"z", "inverse"}, new String[] {"z"});

    private Fft(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseInverse(RAny arg) {
        RLogical l = arg.asLogical();
        if (l.getLogical(0) == RLogical.TRUE) {
            return true;
        }
        return false;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int zPosition = ia.position("z");
        final int inversePosition = ia.position("inverse");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                // LICENSE: transcribed code from GNU R, which is licensed under GPL
                RAny zarg = args[zPosition];
                RComplex res;

                if (zarg instanceof RDouble || zarg instanceof RInt || zarg instanceof RLogical) {
                   res =  zarg.asComplex().materialize(); // this will always copy
                } else if (zarg instanceof RComplex) {
                    if (zarg.isTemporary()) {
                        res = (RComplex) zarg;
                    } else {
                        res = RComplexFactory.copy((RComplex) zarg);
                    }
                } else {
                    throw RError.getNonNumericArgument(ast);
                }
                if (res.size() <= 1) {
                    return res;
                }
                double[] z = res.getContent();
                boolean inverse = inversePosition == -1 ? false : parseInverse(args[inversePosition]);

                int ap_inv = inverse ? 2 : -2;
                int[] ap_maxf = new int[1];
                int[] ap_maxp = new int[1];
                int[] dims = res.dimensions();
                if (dims == null) {
                    int n = res.size();
                    GNUR.fft_factor(n, ap_maxf, ap_maxp);
                    int maxp = ap_maxp[0];
                    int maxf = ap_maxf[0];
                    if (maxf == 0) {
                        throw RError.getFFTFactorization(ast);
                    }
                    double[] ap_work = new double[4 * maxf];
                    int[] ap_iwork = new int[maxp];
                    GNUR.fft_work(z, 1, n, 1, ap_inv, ap_work, ap_iwork);
                    return res;
                }
                int maxmaxf = 1;
                int maxmaxp = 1;
                int ndims = dims.length;
                for (int i = 0; i < ndims; i++) {
                    int d = dims[i];
                    if (d > 1) {
                        GNUR.fft_factor(d, ap_maxf, ap_maxp);
                        int maxp = ap_maxp[0];
                        int maxf = ap_maxf[0];
                        if (maxf == 0) {
                            throw RError.getFFTFactorization(ast);
                        }
                        if (maxf > maxmaxf) {
                            maxmaxf = maxf;
                        }
                        if (maxp > maxmaxp) {
                            maxmaxp = maxp;
                        }
                    }
                }
                double[] ap_work = new double[4 * maxmaxf];
                int[] ap_iwork = new int[maxmaxp];
                int nseg = res.size();
                int n = 1;
                int nspn = 1;
                for (int i = 0; i < ndims; i++) {
                    int d = dims[i];
                    if (d > 1) {
                        nspn *= n;
                        n = d;
                        nseg /= n;
                        GNUR.fft_factor(d, ap_maxf, ap_maxp); // this has a necessary side effect, see comments in appl/fft.c
                        GNUR.fft_work(z, nseg, n, nspn, ap_inv, ap_work, ap_iwork);
                    }
                }
                return res;
            }
        };
    }

}
