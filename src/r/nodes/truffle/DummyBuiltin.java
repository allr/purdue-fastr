package r.nodes.truffle;

import r.*;
import r.data.*;
import r.nodes.*;

// temporary and simplistic implementation of some builtins needed to test other things

public class DummyBuiltin extends BaseR {

    RNode[] argExprs;
    RSymbol[] argNames;
    RSymbol call;

    public DummyBuiltin(ASTNode ast, RSymbol call, RSymbol[] argNames, RNode[] argExprs) {
        super(ast);
        this.argExprs = updateParent(argExprs);
        this.argNames = argNames;
        this.call = call;
    }

    private static RSymbol C = RSymbol.getSymbol("c");
    private static RSymbol REP = RSymbol.getSymbol("rep");

    public static boolean handles(RSymbol sym) {
        return sym == C || sym == REP;
    }

    @Override
    public Object execute(RContext context, RFrame frame) {

        if (call == C) {
            // only support double vectors
            RDouble res = RDouble.RDoubleFactory.getUninitializedArray(argExprs.length);
            for(int i = 0; i < argExprs.length; i++) {
                RAny value = (RAny) argExprs[i].execute(context, frame);
                if (value instanceof RDouble) {
                    RDouble dvalue = (RDouble)value;
                    if (dvalue.size()==1) {
                        res.set(i, dvalue.getDouble(0));
                        continue;
                    }
                }
                Utils.nyi("unsupported arguments to C");
            }
            return res;

        }
        Utils.nyi("unsupported dummy builtin");
        return null;
    }

}
