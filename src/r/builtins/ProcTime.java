package r.builtins;

import r.*;
import r.data.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// TODO: add S3 support
// TODO: add support for other elements then elapsed time (need to use a native call getrusage, see sys-unix.c in GNU-R sources
public class ProcTime extends CallFactory {

    static final CallFactory _ = new ProcTime("proc.time", new String[]{}, new String[]{});

    private ProcTime(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin0(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame) {
                return procTime();
            }
        };
    }

    private static final RArray.Names names = RArray.Names.create(RSymbol.getSymbols(new String[]{"user.self", "sys.self", "elapsed", "user.child", "sys.child"}));

    public static RDouble procTime() {
        double elapsed = (System.nanoTime() - Console.startTime) / 1e9;
        double[] res = new double[] {0, 0, elapsed, 0, 0 };
        return RDouble.RDoubleFactory.getFor(res, null, names, null);
    }
}
