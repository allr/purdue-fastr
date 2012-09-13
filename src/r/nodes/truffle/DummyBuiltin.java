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
            // only supports a vector of integers, doubles, or logical
            if (argExprs.length == 0) {
                return RNull.getNull();
            }
            RAny[] values = new RAny[ argExprs.length];
            boolean hasDouble = false;
            boolean hasLogical = false;
            boolean hasInt = false;
            for (int i = 0; i < argExprs.length; i++) {
                RAny v = (RAny) argExprs[i].execute(context, frame);
                values[i] = v;
                if (v instanceof RDouble) {
                    hasDouble = true;
                } else if (v instanceof RLogical) {
                    hasLogical = true;
                } else if (v instanceof RInt) {
                    hasInt = true;
                } else {
                    Utils.nyi("unsupported vector element");
                }
            }
            if (hasDouble && hasLogical || hasDouble && hasInt || hasLogical && hasInt) {
                Utils.nyi("only homogeneous vectors are supported");
            }
            if (hasDouble) {
                RDouble res = RDouble.RDoubleFactory.getUninitializedArray(values.length);
                for (int i = 0; i < values.length; i++) {
                    RDouble dvalue = (RDouble) values[i];
                    if (dvalue.size() == 1) {
                        res.set(i, dvalue.getDouble(0));
                        continue;
                    }
                    Utils.nyi("only atomic elements are supported");
                }
                return res;
            }
            if (hasInt) {
                RInt res = RInt.RIntFactory.getUninitializedArray(values.length);
                for (int i = 0; i < values.length; i++) {
                    RInt ivalue = (RInt) values[i];
                    if (ivalue.size() == 1) {
                        res.set(i, ivalue.getInt(0));
                        continue;
                    }
                    Utils.nyi("only atomic elements are supported");
                }
                return res;
            }
            if (hasLogical) {
                RLogical res = RLogical.RLogicalFactory.getUninitializedArray(values.length);
                for (int i = 0; i < values.length; i++) {
                    RLogical lvalue = (RLogical) values[i];
                    if (lvalue.size() == 1) {
                        res.set(i, lvalue.getLogical(0));
                        continue;
                    }
                    Utils.nyi("only atomic elements are supported");
                }
                return res;
            }
        }
        Utils.nyi("unsupported dummy builtin " + call.pretty());
        return null;
    }

}
