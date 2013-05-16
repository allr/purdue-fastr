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
        final Object res = quote(ast, expr);
        return new BaseR(call) {

            @Override
            public Object execute(Frame frame) {
                return res;
            }

        };
    }

    public static Object quote(ASTNode ast, RNode expr) {
        if (ast instanceof r.nodes.Constant) {
            return expr.execute(null);
        }
        if (ast instanceof r.nodes.SimpleAccessVariable) {
            return ((r.nodes.SimpleAccessVariable) ast).getSymbol();
        }
        return new RLanguage(ast);
    }
}
