package r.simple;

import java.io.*;

import org.antlr.runtime.*;
import org.junit.*;

import r.*;
import r.builtins.internal.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;
import r.nodes.tools.*;

// simple tests run string snippets of R code (usually one line) using debugging output format
public class SimpleTestBase extends TestBase {

    /** Asserts that given source evaluates to the expected result and that no errors were reported and no exceptions
     * raised.
     */
    static void assertEval(String input, String expectedResult) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expectedResult, result.result);
        Assert.assertFalse("Error marker was found", result.stderr.contains(ManageError.ERROR));
        Assert.assertTrue("Exception was thrown", result.exception == null);
    }

    static void assertEval(String input, String expectedOutput, String expectedResult) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expectedResult, result.result);
        Assert.assertEquals("Evaliation output mismatch", expectedOutput, result.stdout);
        Assert.assertFalse("Error marker was found", result.stderr.contains(ManageError.ERROR));
        Assert.assertTrue("Exception was thrown", result.exception == null);
    }

    /** Asserts that given source evaluates to given result and no errors, warnings or exceptions are reported or
     * thrown.
     */
    static void assertEvalNoWarnings(String input, String expectedResult) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expectedResult, result.result);
        Assert.assertFalse("Error marker was found", result.stderr.contains(ManageError.ERROR));
        Assert.assertFalse("Warning marker was found", result.stderr.contains(ManageError.WARNING));
        Assert.assertTrue("Exception was thrown", result.exception == null);
    }

    // FIXME: this can be done without text capture, using Junit's rules and expectedException
    // FIXME: note, ExpectedException cannot be overridden, but one can implement a MethodRule

//  @Rule
//  public ExpectedException thrown = ExpectedException.none();
//
//  @Test(expected = RError.class)
//  public void testUnused1() throws RecognitionException {
//      evalString("{ x<-function(){1} ; x(y=1) }");
//      Assert.fail("Should not be reached");
//  }
//
//  @Test(expected = RError.class)
//  public void testUnused2() throws RecognitionException {
//      evalString("{ x<-function(){1} ; x(1) }");
//      Assert.fail("Should not be reached");
//  }


    /** Asserts that given source evaluation results in an error being reported and the exception thrown.
     */
    static void assertEvalError(String input, String expectedError) throws RecognitionException {
        EvalResult result = testEvalError(input);
        Assert.assertTrue("Output expected to contain error: " + expectedError, result.stderr.contains(expectedError));
        Assert.assertTrue("Error marker not found.", result.stderr.contains(ManageError.ERROR));
        Assert.assertTrue("Exception was not thrown", result.exception != null);
    }

    /** Asserts that given source evaluates to an expected result and that a warning is produced in the stderr that
     * contains the specified text.
     */
    static void assertEvalWarning(String input, String expectedResult, String expectedWarning) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expectedResult, result.result);
        Assert.assertTrue("Output expected to contain warning: " + expectedWarning, result.stderr.contains(expectedWarning));
        Assert.assertTrue("Warning marker not found.", result.stderr.contains(ManageError.WARNING));
        Assert.assertTrue("Exception was thrown", result.exception == null);
    }

    static void assertTrue(String input) {
        try {
            assertEval(input, "TRUE");
        } catch (Throwable t) {
            Assert.assertTrue(false);
        }
    }

    /** Evaluates the given R expression and returns the returned value.
     */
    // USES DEBUGGING OUTPUT
    private static RAny eval(String input) throws RecognitionException {
        ASTNode astNode = TestPP.parse(input);
        try {
            Random.resetSeed(); // RESETS RANDOM SEED
            return RContext.eval(astNode, true);
        } finally {
            RSymbol.resetTable(); // some tests may have overwritten some builtins
        }
    }

    static EvalResult testEval(String input) throws RecognitionException {
        return testEval(input, false);
    }

    static EvalResult testEvalError(String input) throws RecognitionException {
        return testEval(input, true);
    }

    /** Evaluates the given string and returns the output. Fails if there are problems. Also captures the stderr
     * and stdout streams and then returns an EvalResult object containing the captured evaluation.
     */
    static EvalResult testEval(String input, boolean expectError) throws RecognitionException {
        final PrintStream oldOut = System.out;
        final PrintStream oldErr = System.err;

        if (DEBUGGING_RUN  && !expectError) {
            eval(input).pretty();
            RSymbol.resetTable();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err));
        String result = "";
        Exception e = null;

        try {
            result = eval(input).pretty();
        } catch (RError ex) {
            e = ex;
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
        String output = out.toString();
        String errorOutput = err.toString();

        if (VERBOSE) {
            System.out.println("---------------------\n");
            System.out.println("TEST INPUT:\n"+ input);
            System.out.println("TEST RESULT:\n" + result);
            if (output.length() != 0) {
                System.out.println("TEST OUTPUT:\n" + output);
            }
            if (errorOutput.length() != 0) {
                System.err.println("TEST ERROR OUTPUT:\n" + errorOutput);
            }
        }

        if (RContext.usesTruffleOptimizer()) {
            String verboseOutput = "Captured output of " + input + " is below:\n" + output + "\n" +
                    "Captured output of " + input + " is above.\n";
            if (output.contains("createOptimizedGraph:") && !output.contains("new specialization]#")) {
                System.err.println("Truffle compilation failed for " + input);
                System.err.println(verboseOutput);
                Assert.fail("Truffle compilation failed");
            }
            if (!input.contains("junitWrapper")) {
                // the test did not trigger compilation
                String newInput = "{ junitWrapper <- function() { " + input + " }; junitWrapper(); junitWrapper() }";
                System.out.println("Converted input " + input + " to " + newInput);
                return testEval(newInput); // run us again
            } else {
                System.out.println(verboseOutput);
            }
        }
        return new EvalResult(result, output, errorOutput, e);
    }

}
