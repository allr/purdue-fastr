package r.nodes.truffle;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;


public class FunctionCall extends BaseR {

    final RArgumentList args; // arguments of the call (not of the function)
    RNode functionExpr;

    public FunctionCall(ASTNode ast, RArgumentList args, RNode functionExpr) {
        super(ast);
        this.args = args;
        this.functionExpr = updateParent(functionExpr);
    }

    @Override
    public Object execute(RContext context, RFrame frame) {
        RFunction func = (RFunction) functionExpr.execute(context, frame);
        RFrame fframe = new RFrame(frame, func);

        // FIXME: now only positional argument passing
        // FIXME: now only eager evaluation (no promises)
        // FIXME: now no treatment of default values
        // FIXME: no checks
        RArgumentList fargs = func.args();
        Utils.check(args.length() == fargs.length());
        for (int i = 0; i < args.length(); i++) {
            fframe.writeAt(0, (RAny) args.expression(i).execute(context, frame));
        }
        RNode code = func.body();
        Object res = code.execute(context, fframe);
        return res;
    }

}
