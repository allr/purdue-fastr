package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

final class Quote extends CallFactory {

    static final CallFactory _ = new Quote("quote", new String[]{"expr"}, null);

    private Quote(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);

        RNode expr = exprs[0];
        ASTNode ast = expr.getAST();

        // TODO: (or not) GNU-R returns a symbol representation - e.g. we would return RSymbol - if ast is just a variable read
        final Object res = (ast instanceof r.nodes.Constant) ? expr.execute(null) : new RLanguage(ast);
        return new BaseR(call) {

            @Override
            public Object execute(Frame frame) {
                return res;
            }

        };
    }
}
