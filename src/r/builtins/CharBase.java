package r.builtins;

import r.Convert;
import r.data.*;
import r.data.internal.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.runtime.*;

abstract class CharBase extends CallFactory {

    CharBase(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    abstract String op(ASTNode ast, String input);

    public RString convert(final ASTNode ast, final RString value) {
        final int size = value.size();
        if (value instanceof ScalarStringImpl) {
            return RString.RStringFactory.getScalar(op(ast, value.getString(0)), value.dimensions());
        } else {
            return TracingView.ViewTrace.trace(new View.RStringProxy<RString>(value) {
                @Override public int size() {
                    return size;
                }

                @Override public String getString(int i) {
                    return op(ast, value.getString(i));
                }
            });
        }
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny value) {
                RString str = Convert.coerceToStringError(value, ast);
                return convert(ast, str);
            }
        };
    }
}
