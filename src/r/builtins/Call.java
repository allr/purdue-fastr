package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.Constant;
import r.nodes.FunctionCall;
import r.nodes.truffle.*;


// FIXME: GNU-R distinguishes the storage.mode of a language object (type "language")... we don't do it yet
public class Call extends CallFactory {

    static final CallFactory _ = new Call("call", new String[]{"name", "..."}, new String[] {"name"});

    private Call(String name, String[] params, String[] required) {
        super(name, params, required);
    }


    @Override
    public RNode create(ASTNode call, final RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posName = ia.position("name");

        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RSymbol functionName = parseName(args[posName], ast);
                ArgumentList list = new ArgumentList.Default();
                for (int i = 0; i < args.length; i++) {
                    if (i == posName) {
                        continue;
                    }
                    list.add(names[i], new Constant(args[i]));
                }
                return new RLanguage(new FunctionCall(functionName, list));
            }
        };
    }

    public static RSymbol parseName(RAny arg, ASTNode ast) {
        if (arg instanceof RString) {
            RString sarg = (RString) arg;
            if (sarg.size() == 1) {
                RSymbol s = RSymbol.getSymbol(sarg.getString(0));
                if (s == RSymbol.EMPTY_SYMBOL) {
                    throw RError.getZeroLengthVariable(ast);
                }
                return s;
            }
        }
        throw RError.getFirstArgMustBeArray(ast);
    }

}
