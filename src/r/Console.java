package r;

import java.io.*;
import java.util.Scanner;

import org.antlr.runtime.*;
import org.netlib.blas.*;
import org.netlib.lapack.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;
import r.parser.*;

public class Console {

    public static boolean DEBUG = Utils.getProperty("RConsole.debug", false);
    public static boolean DEBUG_GUI = Utils.getProperty("RConsole.debug.gui", false);

    public static String prompt = Utils.getProperty("RConsole.prompt", "> ");
    public static String promptMore = Utils.getProperty("RConsole.promptmore", "+ ");

    public static String[] trailingArgs; // For commandArgs(trailing=T)
    public static String[] commandArgs; // For commandArgs(trailing=F)

    static String inputFile;
    static boolean interactive;
    static boolean forceVisible;

    static Option[] options = new Option[]{
            //
            new Option.Text("FastR -- Another Fast R Implementation"), //
            new Option("-f", "Script input file", 1) {

                @Override protected void processOption(String name, String[] opts) {
                    inputFile = opts[0];
                }
            }, //
            new Option("--interactive", "Force interactive even if -f is provided") {

                @Override protected void processOption(String name, String[] opts) throws IOException {
                    interactive = true;
                }
            }, //
            new Option("--visible", "Skip invisibility checks") {

                @Override protected void processOption(String name, String[] opts) throws IOException {
                    forceVisible = true;
                }
            }, //
            new Option("--waitForKey", "Wait for 'ENTER' before starting execution") {

                @Override protected void processOption(String name, String[] opts) {
                    System.out.println("Press ENTER to start...");
                    new Scanner(System.in).nextLine();
                }
            }, //
            new Option("--debug", "debug in 'text' or 'gui' mode", 1) {

                @Override protected void processOption(String name, String[] opts) {
                    DEBUG = true;
                    if (opts[0].equalsIgnoreCase("gui")) {
                        DEBUG_GUI = true;
                    }
                }
            }, //
            new Option.Help() {

                @Override protected void processOption(String name, String[] opts) {
                    Option.Help.displayHelp(System.out, options, 0);
                }
            }
    //
    };

    public static void storeCommandLineArguments(String[] args) {
        int alen = args.length;
        commandArgs = new String[alen + 1];
        commandArgs[0] = "FAST-R";
        System.arraycopy(args, 0, commandArgs, 1, alen);

        int argsIndex = -1;
        for (int i = 0; i < (alen - 1); i++) {
            if ("--args".equals(args[i])) {
                argsIndex = i;
                break;
            }
        }
        if (argsIndex == -1) {
            trailingArgs = new String[0];
        } else {
            int tlen = alen - argsIndex - 1;
            trailingArgs = new String[tlen];
            System.arraycopy(args, argsIndex + 1, trailingArgs, 0, tlen);
        }
    }

    public static void main(String[] args) {
        storeCommandLineArguments(args);

        try {
            Option.processCommandLine(args, options); // TODO store this in a more appropriate place
            // (needed for commandArgs())
        } catch (Exception e1) {
            return;
        }
        long before = System.nanoTime();
        try {
            if (interactive || inputFile == null) {
                RContext.debuggingFormat(true);
                System.err.println("Using LAPACK: " + LAPACK.getInstance().getClass().getName());
                System.err.println("Using BLAS: " + BLAS.getInstance().getClass().getName());
                interactive((inputFile == null) ? new BufferedReader(new InputStreamReader(System.in)) : new BufferedReader(new FileReader(inputFile)));
            } else {
                RContext.debuggingFormat(false);
                processFile(openANTLRStream(inputFile));
            }
        } catch (IOException e) {}
        long after = System.nanoTime();
        long elapsed = after - before;
        System.err.println("\n" + (inputFile == null ? "(stdin)" : inputFile) + ": Elapsed " + (elapsed / 1000000L) + " microseconds");
    }

    static void interactive(BufferedReader in) throws IOException {
        RLexer lexer = new RLexer();
        RParser parser = new RParser(null);
        ASTNode tree;
        StringBuilder incomplete = new StringBuilder();

        do {
            try {
                System.out.print(incomplete.length() == 0 ? prompt : promptMore);
                System.out.flush();
                tree = parseStatement(in, lexer, parser, incomplete);
                parser.reset();
                if (tree != null) {
                    if (DEBUG) {
                        debug(tree);
                    }
                    printResult(tree, RContext.eval(tree));
                }
            } catch (RecognitionException e) {
                if (e.getUnexpectedType() != -1) {
                    // if we reached EOF, the sentence is obviously finished ...
                    // and contains a parse error.
                    parseError(parser, e);
                    incomplete.setLength(0); // thus we reset the buffer
                } else { // otherwise there is no parser error and we continue to parse
                    incomplete.append('\n'); // (I'm not really sure we need this ... maybe just for pretty print)
                }
            } catch (IOException e) {
                throw e;
            } catch (RError e) {
                // This error should have been printed by the error manager ingore it here.
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
            if (tree != null) {
                printResult(tree, RContext.eval(tree)); // use non-debugging format
            }
        } catch (RecognitionException e) {
            parseError(parser, e);
        }
    }

    static ASTNode parseStatement(BufferedReader in, RLexer lexer, RParser parser, StringBuilder incomplete) throws IOException, RecognitionException {
        String line = in.readLine();
        if (line == null) { throw new EOFException(); }
        incomplete.append(line);
        lexer.resetIncomplete(); // Since it's a brand new parsing, reset the lexer
        lexer.setCharStream(new ANTLRStringStream(incomplete.toString()));
        parser.setTokenStream(new CommonTokenStream(lexer));
        ASTNode result = parser.interactive();
        incomplete.setLength(0); // Kind of reset
        return result;
    }

    static ANTLRStringStream openANTLRStream(String fName) throws IOException {
        try {
            return fName.equals("-") ? new ANTLRInputStream(System.in) : new ANTLRFileStream(fName);
        } catch (IOException e) {
            // System.err.println(e.getLocalizedMessage());
            throw e;
        }
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
        System.err
                .print("Parse error on '" + tok.getText() + "' at " + tok.getLine() + ":" + (tok.getCharPositionInLine() + 1) + ((tok.getType() > 0) ? " (" + tokNames[tok.getType()] + "). " : ". "));
        System.err.println(parser.getErrorMessage(e, null));
    }

    static void printResult(ASTNode expr, RAny result) {
        // TODO to be a bit more compatible, we need to keep '()' as an ASTNode, but Truffelize must SKIP it.
        if (forceVisible || !(expr instanceof AssignVariable || expr instanceof Loop)) {
            println(result.pretty());
        }
    }

    public static void println(String str) {
        System.out.println(str);
    }
}
