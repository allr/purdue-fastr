package r.builtins;

import r.*;
import r.Convert;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

public class Environment {

    public static final CallFactory EMPTYENV_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn.BuiltIn0(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame) {
                    return REnvironment.EMPTY;
                }
            };
        }
    };

    public static final CallFactory NEWENV_FACTORY = new CallFactory() {

        private final String[] paramNames = new String[] {"hash", "parent", "size"};

        private final int IHASH = 0;
        private final int IPARENT = 1;
        private final int ISIZE = 2;

        private final int DEFAULT_SIZE = 29;
        private final boolean DEFAULT_HASH = true;

        // FIXME: note that R coerces to int instead of logical that one could expect here
        private boolean parseHash(RAny arg, RContext context, ASTNode ast) { // not exactly R semantics
            RInt i = Convert.coerceToIntWarning(arg, context, ast);
            if (i.size() > 0 && i.getInt(0) != 0) {
                return false;
            }
            return DEFAULT_HASH;
        }

        private int parseSize(RAny arg, RContext context, ASTNode ast) {
            RInt i = Convert.coerceToIntWarning(arg, context, ast);
            if (i.size() > 0) {
                int v = i.getInt(0);
                if (v != RInt.NA) {
                    return v;
                }
            }
            return DEFAULT_SIZE;
        }

        private REnvironment parseParent(RAny arg, RContext context, ASTNode ast) {
            if (arg instanceof REnvironment) {
                return (REnvironment) arg;
            }
            throw RError.getMustBeEnviron(ast, "parent"); // GNU-R says "environ", but it is a bug
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            return new BuiltIn(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                    boolean hash = provided[IHASH] ? parseHash(args[paramPositions[IHASH]], context, ast) : DEFAULT_HASH;
                    Frame parentFrame = provided[IPARENT] ? parseParent(args[paramPositions[IPARENT]], context, ast).frame() : frame;
                    Frame newFrame = new Frame(RFrame.RESERVED_SLOTS, parentFrame);
                    newFrame.setObject(RFrame.FUNCTION_SLOT, REnvironment.DUMMY_FUNCTION);

                    // TODO: finish this, currently no hashing is supported
                    int size = DEFAULT_SIZE;
                    if (hash) {
                        if (provided[ISIZE]) {
                            size = parseSize(args[paramPositions[ISIZE]], context, ast);
                        }
                    }
                    // TODO: finish this, currently no hashing is supported

                    return new EnvironmentImpl(newFrame);
                }
            };
        }
    };

    public static final CallFactory ASSIGN_FACTORY = new CallFactory() {

        // "immediate" argument is ignored by GNU-R
        private final String[] paramNames = new String[] {"x", "value", "pos", "envir", "inherits", "immediate"};

        private final int IX = 0;
        private final int IVALUE = 1;
        private final int IPOS = 2;
        private final int IENVIR = 3;
        private final int IINHERITS = 4;

        private final boolean DEFAULT_INHERITS = false;

        private RSymbol parseX(RAny arg, RContext context, ASTNode ast) {
            if (arg instanceof RString) {
                RString sarg = (RString) arg;
                int size = sarg.size();
                if (size > 0) {
                    String s = sarg.getString(0);
                    if (size > 1) {
                        context.warning(ast, RError.ONLY_FIRST_VARIABLE_NAME);
                    }
                    return RSymbol.getSymbol(s);
                }
            }
            throw RError.getInvalidFirstArgument(ast);
        }

        private REnvironment parseEnvir(RAny arg, RContext context, ASTNode ast) {
            if (arg instanceof REnvironment) {
                return (REnvironment) arg;
            }
            throw RError.getInvalidArgument(ast,  paramNames[IENVIR]);
        }

        private boolean parseInherits(RAny arg, RContext context, ASTNode ast) {
            RLogical larg = arg.asLogical();
            int size = larg.size();
            if (size > 0) {
                int v = larg.getLogical(0);
                if (v != RLogical.NA) {
                    return v == RLogical.TRUE;
                }
            }
            throw RError.getInvalidArgument(ast, paramNames[IINHERITS]);
        }

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (!provided[IX]) {
                BuiltIn.missingArg(call, paramNames[IX]);
            }
            if (!provided[IVALUE]) {
                BuiltIn.missingArg(call, paramNames[IVALUE]);
            }

            return new BuiltIn(call, names, exprs) {
                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                    RSymbol name = parseX(args[paramPositions[IX]], context, ast);
                    RAny value = args[paramPositions[IVALUE]];

                    REnvironment envir;
                    if (provided[IENVIR]) {
                        envir = parseEnvir(args[paramPositions[IENVIR]], context, ast);
                    } else {
                        if (provided[IPOS]) {
                            envir = asEnvironment(context, frame, ast, args[paramPositions[IPOS]], true);
                        } else {
                            envir = frame == null ? REnvironment.GLOBAL : RFrame.getEnvironment(frame);
                        }
                    }
                    boolean inherits = provided[IINHERITS] ? parseInherits(args[paramPositions[IINHERITS]], context, ast) : DEFAULT_INHERITS;
                    envir.assign(name, value, inherits);
                    return value;
                }
            };
        }
    };

    public static REnvironment asEnvironment(RContext context, Frame frame, ASTNode ast, RAny arg) {
        return asEnvironment(context, frame, ast, arg, false);
    }

    public static REnvironment asEnvironment(RContext context, Frame frame, ASTNode ast, RAny arg, boolean fakePromise) {
        if (arg instanceof REnvironment) {
            return (REnvironment) arg;
        }
        if (arg instanceof RInt || arg instanceof RDouble) {
            RInt iarg = arg.asInt();
            int size = iarg.size();
            if (size == 0) {
                throw RError.getInvalidArgument(ast, "pos");
            }
            if (size == 1) {
                int idx = iarg.getInt(0);
                if (idx == -1) {
                    if (frame != null) {
                        if (fakePromise) {
                            return RFrame.getEnvironment(frame);
                        } else {
                            Frame parentFrame = RFrame.getParent(frame);
                            if (parentFrame == null) {
                                return REnvironment.GLOBAL;
                            } else {
                                return RFrame.getEnvironment(RFrame.getParent(frame));
                            }
                        }
                    } else {
                        if (fakePromise) {
                            return REnvironment.GLOBAL;
                        } else {
                            throw RError.getNoEnclosingEnvironment(ast);
                        }
                    }
                }
                if (idx == 1) {
                    return REnvironment.GLOBAL;
                }
            }
            if (size > 1) {
                Utils.nyi("create a list...");
                return null;
            }
            // FIXME: add other environments when supported
        }
        throw RError.getInvalidArgument(ast, "pos");
    }

    public static final CallFactory ASENVIRONMENT_FACTORY = new CallFactory() {
        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            BuiltIn.ensureArgName(call, "x", names[0]);
            return new BuiltIn.BuiltIn1(call, names, exprs) {

                @Override
                public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                    return asEnvironment(context, frame, ast, arg);
                }
            };
        }
    };
}
