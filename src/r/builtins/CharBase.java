package r.builtins;

import r.Convert;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

abstract class CharBase extends CallFactory {

    CharBase(String name, String[] params, String[] required, Operation op) {
        super(name, params, required);
        this.op = op;
    }

    private final Operation op;

    abstract static class Operation {
        abstract String op(ASTNode ast, String input);
    }

    public RString convert(final ASTNode ast, final RString value) {
        final int size = value.size();
        if (value instanceof ScalarStringImpl) {
            return RString.RStringFactory.getScalar(op.op(ast, value.getString(0)), value.dimensions());
        } else {
            return new View.RStringProxy<RString>(value) {
                @Override public int size() {
                    return size;
                }

                @Override public String getString(int i) {
                    return op.op(ast, value.getString(i));
                }
            };
        }
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.BuiltIn1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny value) {
                RString str = Convert.coerceToStringError(value, ast);
                return convert(ast, str);
            }
        };
    }
}
