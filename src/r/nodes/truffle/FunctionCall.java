package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

public class FunctionCall extends BaseR {

    RNode functionExpr;
    final RSymbol[] names; // arguments of the call (not of the function)
    RNode[] expressions;

    public FunctionCall(ASTNode ast, RNode functionExpr, RSymbol[] argNames, RNode[] argExprs) {
        super(ast);
        this.functionExpr = updateParent(functionExpr);
        this.names = argNames;
        this.expressions = updateParent(argExprs);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RFunction func = (RFunction) functionExpr.execute(context, frame);
        RFrame fframe = new RFrame(frame, func); // FIXME: parent here means enclosing

        // FIXME: now only positional argument passing
        // FIXME: now only eager evaluation (no promises)
        // FIXME: now no treatment of default values
        // FIXME: no checks
        RSymbol[] fargs = func.argNames();
        Utils.check(names.length == fargs.length);
        for (int i = 0; i < expressions.length; i++) {
            fframe.writeAt(0, (RAny) expressions[i].execute(context, frame));
        }
        RNode code = func.body();
        Object res = code.execute(context, fframe);
        return res;
    }
}
