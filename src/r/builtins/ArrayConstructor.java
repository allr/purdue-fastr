package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class ArrayConstructor {

    private static void checkLength(int len, ASTNode ast) {
        // FIXME: could be optimized for common case, assuming NA < 0
        if (len < 0) {
            throw RError.getVectorSizeNegative(ast);
        }
        if (len == RInt.NA) {
            throw RError.getInvalidLength(ast);
        }
    }

    private static int extractArrayLength(RAny arg0, ASTNode ast) {
        if (arg0 == null) {
            return 0;
        }
        if (arg0 instanceof RInt) {
            RInt ilen = (RInt) arg0;
            if (ilen.size() != 1) {
                throw RError.getInvalidLength(ast);
            }
            int len = ilen.getInt(0);
            checkLength(len, ast);
            return len;
        }
        if (arg0 instanceof RDouble) {
            RDouble dlen = (RDouble) arg0;
            if (dlen.size() != 1) {
                throw RError.getInvalidLength(ast);
            }
            int len = Convert.double2int(dlen.getDouble(0));
            checkLength(len, ast);
            return len;
        }
        throw RError.getInvalidLength(ast);
    }

    public static final CallFactory STRING_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (names.length != 0) {
                BuiltIn.ensureArgName(call, "length", names[0]);
            }
            return new BuiltIn(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    if (args.length == 0) {
                        return RString.EMPTY;
                    }
                    int len = extractArrayLength(args[0], ast);
                    String[] content = new String[len];
                    for (int i = 0; i < len; i++) {
                        content[i] = "";
                    }
                    return RString.RStringFactory.getFor(content);
                }
            };
        }
    };

    public static final CallFactory DOUBLE_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (names.length != 0) {
                BuiltIn.ensureArgName(call, "length", names[0]);
            }
            return new BuiltIn(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    if (args.length == 0) {
                        return RDouble.EMPTY;
                    }
                    int len = extractArrayLength(args[0], ast);
                    return RDouble.RDoubleFactory.getUninitializedArray(len);
                }
            };
        }
    };

    public static final CallFactory INT_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (names.length != 0) {
                BuiltIn.ensureArgName(call, "length", names[0]);
            }
            return new BuiltIn(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    if (args.length == 0) {
                        return RInt.EMPTY;
                    }
                    int len = extractArrayLength(args[0], ast);
                    return RInt.RIntFactory.getUninitializedArray(len);
                }
            };
        }
    };

    public static final CallFactory LOGICAL_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (names.length != 0) {
                BuiltIn.ensureArgName(call, "length", names[0]);
            }
            return new BuiltIn(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    if (args.length == 0) {
                        return RLogical.EMPTY;
                    }
                    int len = extractArrayLength(args[0], ast);
                    return RLogical.RLogicalFactory.getUninitializedArray(len);
                }
            };
        }
    };

    public static final CallFactory RAW_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            if (names.length != 0) {
                BuiltIn.ensureArgName(call, "length", names[0]);
            }
            return new BuiltIn(call, names, exprs) {
                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    if (args.length == 0) {
                        return RRaw.EMPTY;
                    }
                    int len = extractArrayLength(args[0], ast);
                    return RRaw.RRawFactory.getUninitializedArray(len);
                }
            };
        }
    };

    // TODO: complex constructor (it is more elaborate than constructors for the other types)

}
