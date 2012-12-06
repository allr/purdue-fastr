package r.builtins;

import r.*;
import r.Convert;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;


public class CharUtils {

    public abstract static class Operation {
        abstract String op(RContext context, ASTNode ast, String input);
    }

    public static final class CharCallFactory extends CallFactory {
        private final Operation op;

        public CharCallFactory(Operation op) {
            this.op = op;
        }

        public RString convert(final RContext context, final ASTNode ast, final RString value) {
            final int size = value.size();
            if (size == 1) {
                return RString.RStringFactory.getScalar(op.op(context, ast, value.getString(0)), value.dimensions());
            } else if (size > 0) {
                return new View.RStringView() {

                    @Override
                    public int size() {
                        return size;
                    }

                    @Override
                    public String getString(int i) {
                        return op.op(context, ast, value.getString(i));
                    }

                    @Override
                    public boolean isSharedReal() {
                        return value.isShared();
                    }

                    @Override
                    public void ref() {
                        value.ref();
                    }

                    @Override
                    public int[] dimensions() {
                        return value.dimensions();
                    }
                };
            } else {
                return RString.EMPTY;
            }
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);

            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny value) {
                    RString str = Convert.coerceToStringError(value, ast);
                    return convert(context, ast, str);
                }

            };
        }
    }

    public static final CallFactory TOLOWER_FACTORY = new CharCallFactory(new Operation() {

        @Override
        public String op(RContext context, ASTNode ast, String string) {
            if (string != RString.NA) {
                return string.toLowerCase();
            } else {
                return RString.NA;
            }
        }
    });

    public static final CallFactory TOUPPER_FACTORY = new CharCallFactory(new Operation() {

        @Override
        public String op(RContext context, ASTNode ast, String string) {
            if (string != RString.NA) {
                return string.toUpperCase();
            } else {
                return RString.NA;
            }
        }
    });
}
