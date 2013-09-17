package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

// FIXME: not exactly R semantics
public class Stop extends CallFactory {

    static final CallFactory _ = new Stop("stop", new String[]{"..."}, null);

    private Stop(String name, String[] params, String[] required) {
        super(name, params, required);
    }


    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                throw RError.getGenericError(ast, makeMessage(args, ast));
            }

        };
    }

    public static String makeMessage(RAny[] args, ASTNode ast) {
        StringBuilder str = new StringBuilder();
        for (RAny arg : args) {
            RString rs = AsBase.genericAsString(ast, arg);
            int rsize = rs.size();
            for (int i = 0; i < rsize; i++) {
                String s = rs.getString(i);
                if (s == RString.NA) {
                    str.append("NA");
                } else {
                    str.append(s);
                }
            }
        }
        return str.toString();
    }
}
