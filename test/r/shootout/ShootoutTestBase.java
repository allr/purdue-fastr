package r.shootout;

import java.io.*;

import org.antlr.runtime.*;
import org.junit.*;

import r.*;
import r.nodes.*;
import r.parser.*;


public class ShootoutTestBase {

    static RContext global = new RContext(1, false); // use non-debugging format, some shootouts depend on it

    public static String sourceFilePath(String benchDir, String benchFileBase) {
        return "test" + File.separator + "r" + File.separator + "shootout" + File.separator + benchDir + File.separator + benchFileBase + ".r";
    }

    // shootouts that take integer size as input
    public static void assertShootout(String benchDir, String benchFileBase, int size, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        String sourceFile = sourceFilePath(benchDir, benchFileBase);
        String fileToRun = prepareSource(sourceFile, size, benchFileBase);
        assertRun(fileToRun, expectedOutput, expectedErrorOutput, expectedResult);
    }

    // shootouts that take input file name as input
    public static void assertShootout(String benchDir, String benchFileBase, String functionName, String inputFileName, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        String sourceFile = sourceFilePath(benchDir, benchFileBase);
        String fileToRun = prepareSource(sourceFile, functionName, inputFileName, benchFileBase);
        assertRun(fileToRun, expectedOutput, expectedErrorOutput, expectedResult);
    }

    protected static String prepareSource(String code, int size, String benchName) {
        String lastLine = "run(" + size + "L)";
        return prepareSource(code, lastLine, benchName);
    }

    protected static String prepareSource(String code, String functionName, String inputFileName, String benchName) {
        String lastLine = functionName + "(\"" + inputFileName + "\")";
        return prepareSource(code, lastLine, benchName);
    }


    static String prepareSource(String code, String lastLine, String benchName) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(code));
            String tmp = ".tmp.unit." + benchName + ".torun.r";
            PrintWriter out = new PrintWriter(new FileWriter(tmp));

            String line;
            while ((line = in.readLine()) != null) {
                out.println(line);
            }
            out.println(lastLine);
            out.close();
            in.close();
            return tmp;
        } catch (FileNotFoundException e) {
            Assert.fail("Cannot open shootout source file " + code + ":" + e.toString());
        } catch (IOException e) {
            Assert.fail("Error creating run file for " + code + "(" + benchName + "):" + e.toString());
        }
        return null;
    }

    public static class RunResult {
        public final String output;
        public final String errorOutput;
        public final String result;

        public RunResult(String output, String errorOutput, String result) {
            this.output = output;
            this.errorOutput = errorOutput;
            this.result = result;
        }
    }

    protected static RunResult run(String inputFile) {
        System.err.println("Running file " + inputFile + "...");
        try {
            ANTLRFileStream runStream = new ANTLRFileStream(inputFile);

            CommonTokenStream tokens = new CommonTokenStream();
            RLexer lexer = new RLexer(runStream);
            tokens.setTokenSource(lexer);
            RParser parser = new RParser(tokens);
            ASTNode tree = parser.script();

            PrintStream oldOut = System.out;
            ByteArrayOutputStream myOut = new ByteArrayOutputStream();
            PrintStream myOutPS = new PrintStream(myOut);
            System.setOut(myOutPS);

            PrintStream oldErr = System.err;
            ByteArrayOutputStream myErr = new ByteArrayOutputStream();
            PrintStream myErrPS = new PrintStream(myErr);
            System.setErr(myErrPS);

            String result = global.eval(tree).pretty();

            myOutPS.flush();
            System.setOut(oldOut);
            myErrPS.flush();
            System.setErr(oldErr);

            String output = myOut.toString();
            String errorOutput = myErr.toString();

            return new RunResult(output, errorOutput, result);

        } catch (IOException e) {
            Assert.fail("I/O error while running file " + inputFile + ":" + e.toString());
        } catch (RecognitionException e) {
            Assert.fail("Parse error while running file " + inputFile + ":" + e.toString());
        }
        return null;
    }

    static void assertRun(String runFile, String expectedOutput, String expectedErrorOutput, String expectedResult) {
        RunResult result = run(runFile);

        if (expectedOutput != null && !expectedOutput.equals(result.output)) {
            fail("Incorrect output while running " + runFile, result);
        }
        if (expectedErrorOutput != null && !expectedErrorOutput.equals(result.errorOutput)) {
            fail("Incorrect error output while running " + runFile, result);
        }
        if (expectedResult != null && !expectedResult.equals(result.result)) {
            fail("Incorrect result while running " + runFile, result);
        }
    }

    static void fail(String msg, RunResult result) {
        System.err.println(" --- " + msg + ":");
        System.err.println(" --- actual output: ");
        System.err.println(result.output);
        System.err.println(" --- actual error output: ");
        System.err.println(result.errorOutput);
        System.err.println(" --- actual result: ");
        System.err.println(result.result);
        System.err.println(" --- end of report ---");
        Assert.fail();
    }

}
