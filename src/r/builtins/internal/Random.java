package r.builtins.internal;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;

public class Random {
    private static RSymbol seedSymbol = RSymbol.getSymbol(".Random.seed");

    // returns the retrieved direct pointer to the workspace seed, to be later passed to updateWorkspaceSeed
    public static int[] updateNativeSeed(ASTNode ast) {
        RAny v = seedSymbol.getValue(); // FIXME: check R semantics when running in eval
        if (!(v instanceof RInt)) {
            throw RError.getSeedType(ast, v.typeOf());
        }
        RInt iv = (RInt) v;
        int size = iv.size();
        if (size < 3) {
            throw RError.getSeedLength(ast);
        }
        if (!(iv instanceof IntImpl)) {
            iv = RInt.RIntFactory.copy(iv);
        }
        int[] kind = iv.getContent();
        GNUR.set_seed(kind);
        return kind;
    }

    // to be called after returning from native code (/random number generation)
    public static void updateWorkspaceSeed(int[] res) {
        GNUR.get_seed(res);
    }

}
