package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.data.internal.Connection.PipeConnection;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class OpenConnection {

    public static String getScalarString(RAny arg, RContext context, ASTNode ast, String argName) {
        if (arg instanceof RString) {
            RString s = (RString) arg;
            if (s.size() == 1) {
                return s.getString(0);
            }
        }
        throw RError.getInvalidArgument(ast, argName);
    }

    static final ConnectionMode defaultMode = ConnectionMode.get("rt");

    public abstract static class Open { // FIXME: may not be so easy to abstract this out once more arguments of file are implemented
        public abstract Connection createUnopened(String description, ConnectionMode defaultMode);
        public abstract Connection createOpened(String description, ConnectionMode mode, ASTNode ast);

        public RInt open(String description, String open, RContext context, ASTNode ast) {
            if (description.length() == 0) {
                Utils.nyi("temporary file");
            }

            Connection con = null;
            if (open.length() == 0) {
                con = createUnopened(description, defaultMode);
            } else {
                ConnectionMode mode = ConnectionMode.get(open);
                if (mode == null) {
                    throw RError.getInvalidArgument(ast, "open"); // not exactly same as the GNU R error message
                }
                con = createOpened(description, mode, ast);
            }

            int handle = context.allocateConnection(con);
            if (handle != -1) {
                return RInt.RIntFactory.getScalar(handle); // TODO: set proper attributes, class
            } else {
                throw RError.getAllConnectionsInUse(ast);
            }
        }

    }

    public static final Open OPEN_FILE = new Open() {

        @Override
        public Connection createUnopened(String description, ConnectionMode defaultMode) {
            return FileConnection.createUnopened(description, defaultMode);
        }

        @Override
        public Connection createOpened(String description, ConnectionMode mode, ASTNode ast) {
            return FileConnection.createOpened(description, mode, ast);
        }

    };

    private static final String[] fileParamNames = new String[]{"description", "open", "blocking", "encoding", "raw"};

    public static final CallFactory FILE_FACTORY = new CallFactory() {

        private static final int IDESCRIPTION = 0;
        private static final int IOPEN = 1;
        private static final int IBLOCKING = 2;
        private static final int IENCODING = 3;
        private static final int IRAW = 4;

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, fileParamNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[IBLOCKING] || provided[IENCODING] || provided[IRAW]) {
                Utils.nyi();
            }

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    String description = provided[IDESCRIPTION] ? getScalarString(args[paramPositions[IDESCRIPTION]], context, ast, fileParamNames[IDESCRIPTION]) : "";
                    String open = provided[IOPEN] ? getScalarString(args[paramPositions[IOPEN]], context, ast, fileParamNames[IOPEN]) : "";
                    // FIXME: support additional arguments

                    return OPEN_FILE.open(description, open, context, ast);
                }
            };
        }
    };

    public static final Open OPEN_PIPE = new Open() {

        @Override
        public Connection createUnopened(String description, ConnectionMode defaultMode) {
            return PipeConnection.createUnopened(description, defaultMode);
        }

        @Override
        public Connection createOpened(String description, ConnectionMode mode, ASTNode ast) {
            return PipeConnection.createOpened(description, mode, ast);
        }

    };

    private static final String[] pipeParamNames = new String[]{"description", "open", "encoding"};

    public static final CallFactory PIPE_FACTORY = new CallFactory() {

        private static final int IDESCRIPTION = 0;
        private static final int IOPEN = 1;
        private static final int IENCODING = 2;

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, fileParamNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[IENCODING]) {
                Utils.nyi();
            }
            if (!provided[IDESCRIPTION]) {
                BuiltIn.missingArg(call, pipeParamNames[IDESCRIPTION]);
            }

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    String description = provided[IDESCRIPTION] ? getScalarString(args[paramPositions[IDESCRIPTION]], context, ast, pipeParamNames[IDESCRIPTION]) : "";
                    String open = provided[IOPEN] ? getScalarString(args[paramPositions[IOPEN]], context, ast, fileParamNames[IOPEN]) : "";
                    // FIXME: support additional arguments

                    return OPEN_PIPE.open(description, open, context, ast);
                }
            };
        }
    };
}
