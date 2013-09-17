package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// FIXME: could re-factor as this shares a lot of code with All
// FIXME: add S4
public class Any extends CallFactory {
    static final CallFactory _ = new Any("any", new String[]{"...", "na.rm"}, new String[]{});

    private Any(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    // FIXME: this could be optimized for speed if needed (avoid coercion for some types, assert that naRM is last when checking, etc)
    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posNarm = ia.position("na.rm");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {

                boolean naRM = posNarm == -1 ? false : All.parseNarm(args[posNarm]);
                boolean didWarn = false;
                boolean hasNA = false;
                for (int i = 0; i < args.length; i++) {
                    if (i == posNarm) {
                        continue;
                    }
                    RAny v = args[i];
                    RLogical l;
                    if (v instanceof RLogical) {
                        l = (RLogical) v;
                    } else if (v instanceof RInt) {
                        l = v.asLogical();
                    } else {
                        l = v.asLogical();
                        if (!didWarn) {
                            RContext.warning(ast, RError.COERCING_ARGUMENT, v.typeOf(), "logical");
                            didWarn = true;
                        }
                    }
                    int size = l.size();
                    for (int j = 0; j < size; j++) {
                        int ll = l.getLogical(j);
                        switch(ll) {
                            case RLogical.TRUE: return RLogical.BOXED_TRUE;
                            case RLogical.FALSE: break;
                            case RLogical.NA: hasNA = true; break;
                        }
                    }
                }

                if (!naRM && hasNA) {
                    return RLogical.BOXED_NA;
                }
                return RLogical.BOXED_FALSE;
            }
        };
    }
}
