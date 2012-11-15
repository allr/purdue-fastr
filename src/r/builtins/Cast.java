package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't handle BuiltIn1
public class Cast {

    public static RAny genericAsInt(ASTNode ast, RAny arg) {
        if (arg instanceof RList) {
            RList l = (RList) arg;
            int size = l.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                RAny v = l.getRAny(i);
                RArray a = (RArray) v;
                int asize = a.size();

                if (asize == 1) {
                    if (a instanceof RList) {
                        content[i] = RInt.NA;
                    } else {
                        content[i] = Convert.scalar2int(a);
                    }
                } else {
                    if (asize > 1 || a instanceof RList) {
                        throw RError.getGenericError(ast, String.format(RError.LIST_COERCION, "integer"));
                    }
                    // asize == 0
                    content[i] = RInt.NA;
                }
            }
            return RInt.RIntFactory.getFor(content); // drop attributes
        } else {
            return arg.asInt().stripAttributes();
        }
    }
    public static final CallFactory INT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 0) {
                return new BuiltIn.BuiltIn0(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame) {
                        return RInt.EMPTY;
                    }

                };
            }
            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    return genericAsInt(ast, arg);
                }
            };
        }
    };

    public static RAny genericAsDouble(ASTNode ast, RAny arg) {
        Utils.nyi();
        return null;
    }

    public static RAny genericAsLogical(ASTNode ast, RAny arg) {
        Utils.nyi();
        return null;
    }

    public static RAny genericAsList(ASTNode ast, RAny arg) {
        Utils.nyi();
        return null;
    }

    public static RAny genericAsCharacter(ASTNode ast, RAny arg) {
        Utils.nyi();
        return null;
    }

    private static final String[] asVectorParamNames = new String[]{"x", "mode"};

    public static RAny genericAsVector(ASTNode ast, RAny arg) {
        if (arg instanceof RList) {
            return arg; // is it a bug of GNU-R that list attributes are not stripped?
        }
        return arg.stripAttributes();
    }


    public static RAny genericAsVector(ASTNode ast, RAny arg0, RAny arg1) {
        if (!(arg1 instanceof RString)) {
            throw RError.getInvalidMode(ast);
        }
        RString ms = (RString) arg1;
        if (ms.size() != 1) {
            throw RError.getInvalidMode(ast);
        }
        String mode = ms.getString(0);

        if (mode.equals("any")) {
            return genericAsVector(ast, arg0);
        }
        if (mode.equals("integer")) {
            return genericAsInt(ast, arg0);
        }
        if (mode.equals("numeric") || mode.equals("double")) {
            return genericAsDouble(ast, arg0);
        }
        if (mode.equals("list")) {
            return genericAsList(ast, arg0);
        }
        if (mode.equals("character")) {
            return genericAsCharacter(ast, arg0);
        }
        Utils.nyi("unsupported mode");
        return null;
    }

    public static final CallFactory VECTOR_FACTORY = new CallFactory() {

        private static final int IX = 0;
        private static final int IMODE = 1;

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {

            if (exprs.length == 1) {
                BuiltIn.ensureArgName(call, asVectorParamNames[IX], names[0]);
                return new BuiltIn.BuiltIn1(call, names, exprs) {

                    @Override
                    public final RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                        return genericAsVector(ast, arg);
                    }

                };
            }

            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, asVectorParamNames);
            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, asVectorParamNames[IX]);
            }
            if (!provided[IMODE]) {
                BuiltIn.missingArg(call, asVectorParamNames[IMODE]);
            }

            return new BuiltIn.BuiltIn2(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny arg0, RAny arg1) {
                    final boolean xfirst = paramPositions[IX] == 0;
                    return genericAsVector(ast, xfirst ? arg0 : arg1, xfirst ? arg1 : arg0);
                }
            };
        }
    };

}
