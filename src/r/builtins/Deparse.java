package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

// TODO: the GNU-R's deparse does much more
public class Deparse extends CallFactory {

    static final CallFactory _ = new Deparse("deparse", new String[]{"expr"}, null);

    private Deparse(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posExpr = ia.position("expr");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                return RString.RStringFactory.getScalar(AsBase.deparse(args[posExpr], false));
            }

        };
    }
}
