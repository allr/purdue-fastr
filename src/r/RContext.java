package r;

import java.util.*;

import org.antlr.runtime.*;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.tools.*;
import r.parser.*;
import r.runtime.*;

public class RContext {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug.gui", false);

    public static final String GNUR_LIBRARY_NAME = "gnurglue";
    public static final String SYSTEM_LIBS_LIBRARY_NAME = "systemlibsglue";
    public static final String MKL_LIBRARY_NAME = "mklglue";

    private static boolean debuggingFormat = false;
    private static ManageError errorManager = new ManageError(System.err);
    private static BuildExecutableTree executableTreeBuilder = new BuildExecutableTree();
    private static final int NCONNECTIONS = 128;
    private static final Connection[] connections = new Connection[NCONNECTIONS];

    static {
        Arrays.fill(connections, null);
    }

    public static boolean debuggingFormat() {
        return debuggingFormat;
    }

    public static boolean debuggingFormat(boolean useDebuggingFormat) {
        boolean previous = debuggingFormat;
        debuggingFormat = useDebuggingFormat;
        return previous;
    }

    public static RAny eval(ASTNode expr, boolean useDebuggingFormat) {
        debuggingFormat(useDebuggingFormat);
        return eval(expr);
        // NOTE: cannot reset to the original value of debuggingFormat here, because usually the pretty printer is
        // invoked on the results afterwards by the caller of eval; the pretty printer still depends on the correct
        // setting of debugging format
    }

    public static RAny eval(ASTNode expr) {
        try {
            return (RAny) executableTreeBuilder.createLazyRootTree(expr).execute(null); // null means top-level
        } catch (RError e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            error(e); // throws an error
        }
        throw new Error("Never reached");
    }

    public static RNode createNode(ASTNode expr) {
        return executableTreeBuilder.createTree(expr);
    }

    public static RNode createRootNode(ASTNode expr, final RFunction rootEnclosingFunction) {
        return new BaseR(expr) {
            @Child RNode node = adoptChild(executableTreeBuilder.createTree(ast, rootEnclosingFunction));

            @Override
            public Object execute(Frame frame) {
                return node.execute(frame);
            }

            @Override
            protected <N extends RNode> N replaceChild(RNode oldNode, N newNode) {
                assert oldNode != null;
                if (node == oldNode) {
                    node = newNode;
                    return adoptInternal(newNode);
                }
                return super.replaceChild(oldNode, newNode);
            }

        };
    }

    public static void warning(ASTNode expr, String msg, Object... args) {
        errorManager.warning(expr, String.format(msg, args));
    }

    public static void warning(ASTNode expr, String msg) {
        errorManager.warning(expr, msg);
    }

    public static void warning(RError err) {
        errorManager.warning(err);
    }

    public static void error(ASTNode expr, String msg) {
        errorManager.error(expr, msg);
    }

    public static void error(RError err) {
        errorManager.error(err);
    }

    public static int allocateConnection(Connection connection) {
        for (int i = 0; i < NCONNECTIONS; i++) {
            if (connections[i] == null) {
                connections[i] = connection;
                return i;
            }
        }
        return -1;
    }

    /** Release a connection currently in use. */
    public static void freeConnection(int i) {
        assert Utils.check(connections[i] != null);
        connections[i] = null;
    }

    /** Return a connection or null. */
    public static Connection getConnection(int i) {
        return i >= 0 && i < NCONNECTIONS ? connections[i] : null;
    }

    private static int hasGNUR = -1;
    public static boolean hasGNUR() {
        if (hasGNUR == -1) {
            try {
                System.loadLibrary(GNUR_LIBRARY_NAME);
                hasGNUR = 1;
            } catch (Throwable t) {
                hasGNUR = 0;
            }
        }
        return hasGNUR == 1;
    }

    private static int hasSystemLibs = -1;
    public static boolean hasSystemLibs() {
        if (hasSystemLibs == -1) {
            try {
                System.loadLibrary(SYSTEM_LIBS_LIBRARY_NAME);
                hasSystemLibs = 1;
            } catch (Throwable t) {
                hasSystemLibs = 0;
            }
        }
        return hasSystemLibs == 1;
    }

    private static int hasMKL = -1;
    public static boolean hasMKL() {
        if (hasMKL == -1) {
            try {
                System.loadLibrary(MKL_LIBRARY_NAME);
                hasMKL = 1;
            } catch (Throwable t) {
                hasMKL = 0;
            }
        }
        return hasMKL == 1;
    }

    public static ASTNode parseFile(ANTLRStringStream inputStream) {
        CommonTokenStream tokens = new CommonTokenStream();
        RLexer lexer = new RLexer(inputStream);
        tokens.setTokenSource(lexer);
        RParser parser = new RParser(tokens);

        try {
            return parser.script();
        } catch (RecognitionException e) {
            Console.parseError(parser, e);
            return null;
        }
    }

}
