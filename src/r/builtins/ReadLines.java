package r.builtins;

import java.io.*;

import r.*;
import r.builtins.BuiltIn.NamedArgsBuiltIn.*;
import r.data.*;
import r.data.internal.*;
import r.data.internal.Connection.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.runtime.*;

// TODO: finish this
public class ReadLines {

    private static final String[] paramNames = new String[]{"con", "n", "ok", "warn", "encoding"};

    private static final int ICON = 0;
    private static final int IN = 1;
    private static final int IOK = 2;
    private static final int IWARN = 3;
    private static final int IENCODING = 4;

    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            AnalyzedArguments a = BuiltIn.NamedArgsBuiltIn.analyzeArguments(names, exprs, paramNames);

            final boolean[] provided = a.providedParams;
            final int[] paramPositions = a.paramPositions;

            if (provided[IENCODING]) {
                Utils.nyi();
            }

            final ConnectionMode defaultMode = ConnectionMode.get("rt");

            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {

                    Connection con = null;
                    boolean wasOpen = false;

                    if (!provided[ICON]) {
                        Utils.nyi("stdin");
                        return null;
                    } else {
                        RAny conArg = args[paramPositions[ICON]];
                        if (conArg instanceof RString) {
                            String description = OpenConnection.getScalarString(conArg, context, ast, "description");
                            con = FileConnection.createOpened(description, defaultMode, ast);
                        } else if (conArg instanceof RInt) {
                            // FIXME: check if it is a connection when attributes are implemented
                            RInt iarg = (RInt) conArg;
                            if (iarg.size() != -1) {
                                throw RError.getNotConnection(ast, paramNames[ICON]);
                            }
                            int handle = iarg.getInt(0);
                            con = context.getConnection(handle);
                            Utils.check(con != null);
                            if (con.isOpen()) {
                                ConnectionMode mode = con.currentMode();
                                if (!mode.read()) {
                                   throw RError.getCannotReadConnection(ast);
                                }
                                Utils.check(mode.text()); // TODO: GNU-R seems to be happily reading from binary connections, too
                                wasOpen = true;
                            } else {
                                con.open(defaultMode, ast);
                            }
                        }
                    }

                    BufferedReader reader = new BufferedReader(con.reader());
                    Utils.nyi("unfinished");
                    return null;
                }
            };
        }
    };
}
