package r;

import java.io.*;

import org.antlr.runtime.*;
import org.junit.*;

import r.data.*;
import r.nodes.*;
import r.parser.*;

public class FileTestBase extends TestBase {

    public static void captureOutputToFile(String runFile, String captureFile) {
        EvalResult result = run(runFile);
        try {
            PrintWriter out = new PrintWriter(captureFile);
            out.print(result.stdout);
            out.close();
        } catch (IOException e) {
            Assert.fail("I/O error while generating output using " + runFile + ": " + e.toString());
        }
    }

    protected static EvalResult run(String runFile) {

        System.err.println("Running file " + runFile + "...");
        try {
            ANTLRFileStream runStream = new ANTLRFileStream(runFile);

            CommonTokenStream tokens = new CommonTokenStream();
            RLexer lexer = new RLexer(runStream);
            tokens.setTokenSource(lexer);
            RParser parser = new RParser(tokens);
            ASTNode tree = parser.script();

            if (DEBUGGING_RUN) {
                RContext.eval(tree, false).pretty();
                RSymbol.resetTable();
                runStream = new ANTLRFileStream(runFile);
                tokens = new CommonTokenStream();
                lexer = new RLexer(runStream);
                tokens.setTokenSource(lexer);
                parser = new RParser(tokens);
                tree = parser.script();
            }

            PrintStream oldOut = System.out;
            ByteArrayOutputStream myOut = new ByteArrayOutputStream();
            PrintStream myOutPS = new PrintStream(myOut);
            System.setOut(myOutPS);

            PrintStream oldErr = System.err;
            ByteArrayOutputStream myErr = new ByteArrayOutputStream();
            PrintStream myErrPS = new PrintStream(myErr);
            System.setErr(myErrPS);

            String result;
            try {
                result = RContext.eval(tree, false).pretty();
            } finally {
                RSymbol.resetTable(); // some tests may have overwritten some builtins
            }

            myOutPS.flush();
            System.setOut(oldOut);
            myErrPS.flush();
            System.setErr(oldErr);

            String output = myOut.toString();
            String errorOutput = myErr.toString();

            if (VERBOSE) {
                System.out.println(output);
                System.err.println(errorOutput);
            }

            return new EvalResult(result, output, errorOutput, null); // null as we don't catch exceptions

        } catch (IOException e) {
            Assert.fail("I/O error while running file " + runFile + ":" + e.toString());
        } catch (RecognitionException e) {
            Assert.fail("Parse error while running file " + runFile + ":" + e.toString());
        }
        return null;
    }

    protected static void assertRun(String runFile, String[] args, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        Console.storeCommandLineArguments(args);
        EvalResult result = run(runFile);

        if (expectedOutput != null && !expectedOutput.equals(result.stdout)) {
            fail("Incorrect output while running " + runFile, result);
            System.err.println("Expected output was: ");
            System.err.println(expectedOutput);
        }
        if (expectedErrorOutput != null && !expectedErrorOutput.equals(result.stderr)) {
            fail("Incorrect error output while running " + runFile, result);
            System.err.println("Expected error output was: ");
            System.err.println(expectedErrorOutput);
        }
        if (expectedResult != null && !expectedResult.equals(result.result)) {
            fail("Incorrect result while running " + runFile, result);
            System.err.println("Expected result was: ");
            System.err.println(expectedResult);
        }
    }

}
