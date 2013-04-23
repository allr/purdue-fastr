package r.builtins;

import r.*;
import r.Truffle.*;

import r.data.*;
import r.data.RComplex.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

// main/fourier.c
// appl/fft.c
final class Fft extends CallFactory {

    static final CallFactory _ = new Fft("fft", new String[]{"z", "inverse"}, new String[]{"z"});

    private Fft(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseInverse(RAny arg) {
        RLogical l = arg.asLogical();
        if (l.getLogical(0) == RLogical.TRUE) { return true; }
        return false;
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int zPosition = ia.position("z");
        final int inversePosition = ia.position("inverse");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw Utils.nyi();
            }
        };
    }

}
