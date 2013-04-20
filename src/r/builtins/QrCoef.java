package r.builtins;

import org.netlib.lapack.*;
import org.netlib.util.*;

import r.Truffle.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.gnur.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class QrCoef extends CallFactory {

    static final CallFactory _ = new QrCoef("qr.coef", new String[]{"qr", "y"}, null);

    private QrCoef(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static boolean parseUseLapack(RAny.Attributes attr, ASTNode ast) {
        if (attr == null) { return false; }
        RAny v = attr.map().get(useLAPACKSymbol);
        if (v != null && v instanceof RLogical) {
            return parseUncheckedLogical(v, ast);
        } else {
            return false;
        }
    }

    static final RSymbol pivotSymbol = RSymbol.getSymbol("pivot");
    static final RSymbol qrauxSymbol = RSymbol.getSymbol("qraux");
    static final RSymbol qrSymbol = RSymbol.getSymbol("qr");
    static final RSymbol rankSymbol = RSymbol.getSymbol("rank");
    static final RSymbol useLAPACKSymbol = RSymbol.getSymbol("useLAPACK");

    public static RAny coef(RAny qrArg, RAny yArg, ASTNode ast) {
        throw Utils.nyi("not imeplemented");

    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int qrPosition = ia.position("qr");
        final int yPosition = ia.position("y");

        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny qrArg = args[qrPosition];
                RAny yArg = args[yPosition];
                return coef(qrArg, yArg, ast);
            }
        };
    }

}
