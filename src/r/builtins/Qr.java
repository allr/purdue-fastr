package r.builtins;

import r.*;
import r.Truffle.Frame;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: S3
public class Qr extends CallFactory {

    static final CallFactory _ = new Qr("qr", new String[]{"x", "tol", "LAPACK"}, new String[]{"x"});

    private Qr(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static RArray.Names resultNames = RArray.Names.create(RSymbol.getSymbols(new String[]{"qr", "rank", "qraux", "pivot"}));
    static RAny.Attributes useLAPACKAttr = RAny.Attributes.createAndPut("useLAPACK", RLogical.BOXED_TRUE);

    public static double parseTol(RAny arg, ASTNode ast) {
        RDouble d = Convert.coerceToDoubleError(arg, ast);
        if (d.size() != 1) { throw RError.getInvalidArgument(ast, "tol"); // FIXME: not an R error message, R passes the arg without checking to Fortran code
        }
        return d.getDouble(0);
    }

    public static RAny qr(RAny xArg, RAny tolArg, RAny lapackArg, ASTNode ast) {
        throw Utils.nyi("not implemented");

    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int xPosition = ia.position("x");
        final int tolPosition = ia.position("tol");
        final int lapackPosition = ia.position("LAPACK");

        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                return qr(args[xPosition], tolPosition == -1 ? null : args[tolPosition], lapackPosition == -1 ? null : args[lapackPosition], ast);
            }
        };
    }

}
