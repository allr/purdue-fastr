package r;

import java.io.*;
import java.util.Scanner;

import org.antlr.runtime.*;

import r.nodes.*;
import r.nodes.tools.*;
import r.parser.*;

/*
 * FIXME Is it the right package for Console ? Should maybe be in 'r' or 'r.tools'
 */
public class Console {

    public static boolean DEBUG = Utils.getProperty("RConsole.debug", false);
    public static boolean DEBUG_GUI = Utils.getProperty("RConsole.debug.gui", false);

    public static String prompt = Utils.getProperty("RConsole.prompt", "> ");
    public static String promptMore = Utils.getProperty("RConsole.promptmore", "+ ");

    static String[] moreArgs; // For commandArgs()

    static int compilerThreshold = 1; // For --threshold

    // When we aren't in interactive mode
    static ANTLRStringStream stream;

    // When we are
    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    static Option[] options = new Option[]{
                    //
                    new Option.Text("FastR -- Another Fast R Implementation"), //
                    new Option("-f", "Script input file", 1) {

                        @Override
                        protected void processOption(String name, String[] opts) throws IOException {
                            try {
                                if (opts[0].equals("-")) {
                                    stream = new ANTLRInputStream(System.in);
                                } else {
                                    stream = new ANTLRFileStream(opts[0]);
                                }
                            } catch (IOException e) {
                                System.err.println(e.getLocalizedMessage());
                                throw e;
                            }
                        }
                    }, //
                    new Option("--waitForKey", "Wait for presing 'return' before starting execution") {

                        @Override
                        protected void processOption(String name, String[] opts) {
                            System.out.println("Press ENTER to start...");
                            new Scanner(System.in).nextLine();
                        }
                    }, //
                    new Option("--threshold", "Set compiler threshold (default: " + compilerThreshold + ")", 1) {

                        @Override
                        protected void processOption(String name, String[] opts) {
                            try {
                                compilerThreshold = Integer.parseInt(opts[0]);
                            } catch (NumberFormatException e) {
                            }
                        }
                    }, //
                    new Option("--debug", "debug in 'text' or 'gui' mode", 1) {

                        @Override
                        protected void processOption(String name, String[] opts) {
                            DEBUG = true;
                            if (opts[0].equalsIgnoreCase("gui")) {
                                DEBUG_GUI = true;
                            }
                        }
                    }, //
                    new Option.Help() {

                        @Override
                        protected void processOption(String name, String[] opts) {
                            Option.Help.displayHelp(System.out, options, 0);
                        }
                    }
    //
    };

    public static void main(String[] args) {
        try {
            moreArgs = Option.processCommandLine(args, options);
            // TODO store this in a more appropriate place (needed for commandArgs())
        } catch (Exception e1) {
            return;
        }

        long before = System.nanoTime();
        try {
            if (stream != null) {
                processFile(stream);
            } else {
                interactive();
            }
        } catch (IOException e) {
        }
        long after = System.nanoTime();
        long elapsed = after - before;
        System.out.println("\nElapsed " + (elapsed / 1000000L) + " microseconds");
    }

    static void interactive() throws IOException {
        RLexer lexer = new RLexer();
        RParser parser = new RParser(null);
        ASTNode tree;
        RContext context = new RContext(compilerThreshold);
        PrintStream out = System.out;
        StringBuilder incomplete = new StringBuilder();

        do {
            try {
                out.print(incomplete.length() == 0 ? prompt : promptMore);
                out.flush();
                tree = parseStatement(lexer, parser, incomplete);
                parser.reset();
                if (DEBUG) {
                    debug(tree);
                }
                out.println(context.eval(tree).pretty());
            } catch (RecognitionException e) {
                if (e.getUnexpectedType() != -1) { // if we reached EOF, the sentence is obviously finished and contains
// a
                    // parse error
                    parseError(parser, e);
                    incomplete.setLength(0); // thus we reset the buffer
                } else { // otherwise there is no parser error and we continue to parse
                    incomplete.append('\n'); // (I'm not really sure we need this ... maybe just for pretty print)
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    static void processFile(ANTLRStringStream inputStream) {
        CommonTokenStream tokens = new CommonTokenStream();
        RLexer lexer = new RLexer(inputStream);
        tokens.setTokenSource(lexer);
        RParser parser = new RParser(tokens);

        try {
            ASTNode tree = parser.script();
            System.out.println(new RContext(compilerThreshold).eval(tree).pretty());
        } catch (RecognitionException e) {
            parseError(parser, e);
        }
    }

    static ASTNode parseStatement(RLexer lexer, RParser parser, StringBuilder incomplete) throws IOException, RecognitionException {
        String line = in.readLine();
        if (line == null) {
            throw new EOFException();
        }
        incomplete.append(line);
        lexer.setCharStream(new ANTLRStringStream(incomplete.toString()));
        parser.setTokenStream(new CommonTokenStream(lexer));
        ASTNode result;
        result = parser.interactive();
        incomplete.setLength(0); // Kind of reset
        return result;
    }

    static void debug(ASTNode tree) {
        if (DEBUG_GUI) {
            TreeViewer.showTree(tree);
        } else {
            new PrettyPrinter(System.err).print(tree);
        }
    }

    static void parseError(RParser parser, RecognitionException e) {
        Token tok = e.token;
        String[] tokNames = parser.getTokenNames();
        System.err.print("Parse error on '" + tok.getText() + "' at " + tok.getLine() + ":" + tok.getCharPositionInLine() + " (" + tokNames[tok.getType()] + ") expected:");
        parser.display_next_tokens();
        System.err.println("");
    }
}
