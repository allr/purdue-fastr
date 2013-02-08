package r.builtins;

import r.Convert;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

public class CharUtils {

    public abstract static class Operation {
        abstract String op(ASTNode ast, String input);
    }

    public static final class CharCallFactory extends CallFactory {
        private final Operation op;

        public CharCallFactory(Operation op) {
            this.op = op;
        }

        public RString convert(final ASTNode ast, final RString value) {
            final int size = value.size();
            if (value instanceof ScalarStringImpl) {
                return RString.RStringFactory.getScalar(op.op(ast, value.getString(0)), value.dimensions());
            } else {
                return new View.RStringProxy<RString>(value) {

                    @Override
                    public int size() {
                        return size;
                    }

                    @Override
                    public String getString(int i) {
                        return op.op(ast, value.getString(i));
                    }
                };
            }
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(Frame frame, RAny value) {
                    RString str = Convert.coerceToStringError(value, ast);
                    return convert(ast, str);
                }

            };
        }
    }

    public static final CallFactory TOLOWER_FACTORY = new CharCallFactory(new Operation() {

        @Override
        public String op(ASTNode ast, String string) {
            if (string != RString.NA) {
                return string.toLowerCase();
            } else {
                return RString.NA;
            }
        }
    });

    public static final CallFactory TOUPPER_FACTORY = new CharCallFactory(new Operation() {

        @Override
        public String op(ASTNode ast, String string) {
            if (string != RString.NA) {
                return string.toUpperCase();
            } else {
                return RString.NA;
            }
        }
    });
}
