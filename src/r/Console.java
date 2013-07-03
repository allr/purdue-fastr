package r;

import java.io.*;
import java.util.Scanner;

import jline.*;
import jline.console.*;

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
    static boolean debuggingFormat;

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
            new Option("--debugging-format", "Use debugging output format") {

                @Override protected void processOption(String name, String[] opts) throws IOException {
                    debuggingFormat = true;
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
            RContext.debuggingFormat(debuggingFormat);
            if (interactive || inputFile == null) {
                System.err.println("Using LAPACK: " + LAPACK.getInstance().getClass().getName());
                System.err.println("Using BLAS: " + BLAS.getInstance().getClass().getName());
                System.err.println("Using GNUR: " + (RContext.hasGNUR() ? "yes" : "not available"));
                if (inputFile == null) {
                    // NOTE the JLine2 console does not work from within Eclipse
                    // Add -Djline.terminal=jline.UnsupportedTerminal to your eclipse run configuration
                    try {
                        ConsoleReader console = new ConsoleReader();
                        console.setPrompt("> "); // FIXME: it seems that JLine2 does not support a continuation prompt
                        interactive(createReader(console));
                    } finally {
                        try {
                            TerminalFactory.get().restore();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    interactive(createReader(new BufferedReader(new FileReader(inputFile))));
                }
//                interactive((inputFile == null) ? new BufferedReader(new InputStreamReader(System.in)) : new BufferedReader(new FileReader(inputFile)));
            } else {
                processFile(openANTLRStream(inputFile));
            }
        } catch (IOException e) {}
        long after = System.nanoTime();
        long elapsed = after - before;
        System.err.println("\n" + (inputFile == null ? "(stdin)" : inputFile) + ": Elapsed " + (elapsed / 1000000L) + " microseconds");
    }

    static interface RLineReader {
        public String readLine() throws IOException;
        public void prompt(String s);
    }

    private static RLineReader createReader(final BufferedReader buf) {
        return new RLineReader() {

            @Override
            public String readLine() throws IOException {
                return buf.readLine();
            }

            @Override
            public void prompt(String s) {
                System.out.print(s);
            }
        };
    }

    private static RLineReader createReader(final ConsoleReader console) {
        return new RLineReader() {

            @Override
            public String readLine() throws IOException {
                return console.readLine();
            }

            @Override
            public void prompt(String s) {
                console.setPrompt(s);
            }

        };
    }

    static void interactive(RLineReader in) throws IOException {
        RLexer lexer = new RLexer();
        RParser parser = new RParser(null);
        ASTNode tree;
        StringBuilder incomplete = new StringBuilder();

        do {
            try {
                in.prompt(incomplete.length() == 0 ? prompt : promptMore);
                System.out.flush();
                tree = parseStatement(in.readLine(), lexer, parser, incomplete);
                parser.reset();
                if (tree != null) {
                    if (DEBUG) {
                        debug(tree);
                    }
                    printResult(tree, RContext.eval(tree));
                }
            } catch (RecognitionException e) {
                if (e.getUnexpectedType() != -1) {
                    // if we reached EOF, the sentence is obviously finished ... and contains a parse error.
                    parseError(parser, e);
                    incomplete.setLength(0); // thus we reset the buffer
                } else { // otherwise there is no parser error and we continue to parse
                    incomplete.append('\n'); // (I'm not really sure we need this ... maybe just for pretty print)
                }
            } catch (IllegalArgumentException e) {
                // this is in fact a RecognitionException from the lexer
                // indeed, this is a hack, but there does not seem to be a cleaner way to stop the lexer on the first error and get here
                RecognitionException re = (RecognitionException) e.getCause();
                if (re.getUnexpectedType() != -1) {
                    // if we reached EOF, the sentence is obviously finished ... and contains a parse error.
                    lexerError(lexer, re);
                    incomplete.setLength(0); // thus we reset the buffer
                } else { // otherwise there is no parser error and we continue to parse
                    incomplete.append('\n'); // (I'm not really sure we need this ... maybe just for pretty print)
                }

            } catch (IOException e) {
                throw e;
            } catch (RError e) {
                // This error should have been printed by the error manager, ignore it here.
                if (debuggingFormat) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);
    }

    static void processFile(ANTLRStringStream inputStream) {
        ASTNode tree = RContext.parseFile(inputStream);
        if (tree != null) {
            printResult(tree, RContext.eval(tree)); // use non-debugging format
        }
    }

    static ASTNode parseStatement(String line, RLexer lexer, RParser parser, StringBuilder incomplete) throws IOException, RecognitionException {
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
        Token token = e.token;
        String[] tokenNames = parser.getTokenNames();
        System.err
                .print("Parse error on '" + token.getText() + "' at " + token.getLine() + ":" + (token.getCharPositionInLine() + 1) + ((token.getType() > 0) ? " (" + tokenNames[token.getType()] + "): " : ": "));
        System.err.println(parser.getErrorMessage(e, tokenNames) + ".");
    }

    static void lexerError(RLexer lexer, RecognitionException e) {
        String[] tokenNames = lexer.getTokenNames();
        System.err.println("Parse error (lexer): " + lexer.getErrorMessage(e, tokenNames) + ".");
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
