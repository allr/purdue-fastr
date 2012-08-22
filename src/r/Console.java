package r;

import java.io.*;

import org.antlr.runtime.*;

import r.nodes.*;
import r.nodes.tools.*;
import r.parser.*;

/*
 * FIXME Is it the right package for Console ? Should maybe be in 'r' or 'r.tools'
 */
public class Console {

    public static final boolean DEBUG = Utils.getProperty("RConsole.debug", false);
    public static final boolean DEBUG_GUI = Utils.getProperty("RConsole.debug.gui", false);

    public static String prompt = Utils.getProperty("RConsole.prompt", "> ");
    public static String promptMore = Utils.getProperty("RConsole.promptmore", "+ ");

    static RContext context;
    static PrintStream out;

    static RLexer lexer;
    static RParser parser;
    static ASTNode tree;
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static StringBuilder incomplete = new StringBuilder();

    public static void main(String[] args) {
        boolean errorStmt = false;
        try {
            if (args.length > 0) {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            }

            context = new RContext();
            out = System.out;
            lexer = new RLexer();
            parser = new RParser(null);

            do {
                out.print(incomplete.length() == 0 ? prompt : promptMore);
                out.flush();
                errorStmt = !parse_statement();
                if (!errorStmt) {
                    parser.reset();
                    if (DEBUG) {
                        if (DEBUG_GUI) {
                            TreeViewer.showTree(tree);
                        } else {
                            new PrettyPrinter(System.err).print(tree);
                        }
                    }
                    out.println(context.eval(tree).pretty());
                }
            } while (true);
        } catch (IOException e) {
        }
    }

    static boolean parse_statement() throws IOException {
        String line = in.readLine();
        if (line == null) {
            throw new EOFException();
        }
        incomplete.append(line);
        lexer.setCharStream(new ANTLRStringStream(incomplete.toString()));
        parser.setTokenStream(new CommonTokenStream(lexer));
        ASTNode result;
        try {
            result = parser.interactive();
        } catch (RecognitionException e) {
            if (e.getUnexpectedType() != -1) {
                Token tok = e.token;
                String[] tokNames = parser.getTokenNames();
                System.err.print("Parse error on '" + tok.getText() + "' at " + tok.getLine() + ":" + tok.getCharPositionInLine() + " (" + tokNames[tok.getType()] + ") expected:");
                parser.display_next_tokens();
                System.err.println("");
                incomplete.setLength(0); // Kind of reset
            } else {
                incomplete.append('\n');
            }
            return false;
        }
        incomplete.setLength(0); // Kind of reset
        tree = result;
        return true;
    }
}
