package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.FileConnection;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;


public class OpenConnection {

    private static final String[] fileParamNames = new String[]{"description", "open", "blocking", "encoding", "raw"};

    private static final int IDESCRIPTION = 0;
    private static final int IOPEN = 1;
    private static final int IBLOCKING = 2;
    private static final int IENCODING = 3;
    private static final int IRAW = 4;

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

    public static RInt file(String description, String open, RContext context, ASTNode ast) {
        if (description.length() == 0) {
            Utils.nyi("temporary file");
        }

        FileConnection con = null;
        if (open.length() == 0) {
            con = FileConnection.createUnopened(description, defaultMode);
        } else {
            ConnectionMode mode = ConnectionMode.get(open);
            if (mode == null) {
                throw RError.getInvalidArgument(ast, "open"); // not exactly same as the GNU R error message
            }
            con = FileConnection.createOpened(description, mode, ast);
        }

        int handle = context.allocateConnection(con);
        if (handle != -1) {
            return RInt.RIntFactory.getScalar(handle); // TODO: set proper attributes, class
        } else {
            throw RError.getAllConnectionsInUse(ast);
        }
    }

    public static final CallFactory FILE_FACTORY = new CallFactory() {

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

                    return file(description, open, context, ast);
                }
            };
        }
    };
}
