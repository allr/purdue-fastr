package r.builtins.internal;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;

public final class Random {
    private static RSymbol seedSymbol = RSymbol.getSymbol(".Random.seed");

    // the seed used by default by libRMath, changing this default will break tests
    public static final RInt defaultSeed = (RInt) RArray.RArrayUtils.markShared(RInt.RIntFactory.getFor(new int[] {401, 1234, 5678}));


    public static void resetSeed() {
        seedSymbol.setValue(defaultSeed);
    }

    // returns the retrieved direct pointer to the workspace seed, to be later passed to updateWorkspaceSeed
    public static int[] updateNativeSeed(ASTNode ast) {
        RAny v = seedSymbol.getValue(); // FIXME: check R semantics when running in eval
        int[] kind;
        if (v == null) {
                // TODO: should ideally randomize here
            v = defaultSeed;
        }
        if (!(v instanceof RInt)) {
            throw RError.getSeedType(ast, v.typeOf());
        }
        RInt iv = (RInt) v;
        int size = iv.size();
        if (size < 3) {
            throw RError.getSeedLength(ast);
        }
        if (!(iv instanceof IntImpl) || !iv.isTemporary()) {
            iv = RInt.RIntFactory.copy(iv);
            seedSymbol.setValue(iv);
        }
        kind = iv.getContent();

        GNUR.set_seed(kind);
        return kind;
    }

    // to be called after returning from native code (/random number generation)
    public static void updateWorkspaceSeed(int[] res) {
        GNUR.get_seed(res);
    }

    // for functions like rnorm, etc
    public static int parseNArgument(RAny narg, ASTNode ast) {
        if (!(narg instanceof RArray)) {
            throw RError.getInvalidUnnamedArguments(ast);
        }
        RArray a = (RArray) narg;
        int size = a.size();
        if (size == 1) {
            int i = a.asInt().getInt(0);
            if (i < 0) { // includes i == RInt.NA
                throw RError.getInvalidUnnamedArguments(ast);
            }
            return i;
        }
        return size;
    }

    public static double[] parseNumericArgument(RAny narg, ASTNode ast) {
        if (narg instanceof RDouble) {
            return ((RDouble) narg).getContent();
        }
        if (narg instanceof RInt || narg instanceof RLogical) {
            return narg.asDouble().getContent();
        }
        throw RError.getInvalidUnnamedArguments(ast);
    }

    public static RDouble allNAs(int n, ASTNode ast) {
        if (n == 0) {
            return RDouble.EMPTY;
        }
        RContext.warning(ast, RError.NA_PRODUCED);
        return RDouble.RDoubleFactory.getNAArray(n);
    }

}
