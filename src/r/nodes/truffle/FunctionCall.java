package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

public class FunctionCall extends BaseR {

    RNode closureExpr;
    final RSymbol[] names; // arguments of the call (not of the function)
    RNode[] expressions;

    public FunctionCall(ASTNode ast, RNode closureExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast);
        this.closureExpr = updateParent(closureExpr);
        this.names = argNames;
        this.expressions = updateParent(argExprs);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RClosure tgt = (RClosure) closureExpr.execute(context, frame);
        RFunction func = tgt.function();
        RFrame fframe = new RFrame(tgt.environment(), func);

        // FIXME: now only positional argument passing
        // FIXME: now only eager evaluation (no promises)
        // FIXME: now no treatment of default values
        // FIXME: no checks
        RSymbol[] fargs = func.argNames();
        Utils.check(names.length == fargs.length);
        for (int i = 0; i < expressions.length; i++) {
            fframe.writeAt(i, (RAny) expressions[i].execute(context, frame));
        }
        RNode code = func.body();
        Object res = code.execute(context, fframe);
        return res;
    }
}
