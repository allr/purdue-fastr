package r;

import java.io.*;

import org.antlr.runtime.*;
import org.junit.*;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.tools.*;

public class TestBase {

    static String evalString(String input) throws RecognitionException {
        return eval(input).pretty();
    }

    static void assertEval(String input, String expectedOutput, String expectedResult) throws RecognitionException {
        // FIXME: can this be made work also with Truffle?

        final PrintStream oldOut = System.out;
        final ByteArrayOutputStream myOut = new ByteArrayOutputStream();
        final PrintStream myOutPS = new PrintStream(myOut);
        System.setOut(myOutPS);
        String result = evalString(input);
        myOutPS.flush();
        System.setOut(oldOut);
        String output = myOut.toString();

        if (!result.equals(expectedResult)) {
            Assert.fail("incorrect result, got " + result + " for " + input + " expecting " + expectedResult);
        }
        if (!output.equals(expectedOutput)) {
            Assert.fail("incorrect output, got " + output + " for " + input + " expecting " + expectedOutput);
        }

    }

    /** A simple class that holds the resulr of a test evaluation.
     *
     * Contains the actual result reported by eval, the captured std err and std out of the execution and the message
     * of a Java exception if any was thrown during the execution, or null.
     */
    public static class EvalResult {

        public final String result; // result of the evaulation (last expression)
        public final String stdout; // standard out as reported during the evaluation
        public final String stderr; // stderr as reported during the evaluation
        public final String exception; // exception, or null if no exception was thrown

        public EvalResult(String res, String out, String err, Exception e) {
            result = res;
            stdout = out;
            stderr = err;
            if (e == null) {
                exception = null;
            } else {
                exception = e.getClass().getName() + e.getMessage();
            }
        }
    }

    /** Evaluates the given string and returns the output. Fails if there are problems. Also captures the stderr
     * and stdout streams and then returns an EvalResult object containing the captured evaluation.
     */
    static EvalResult testEval(String input) throws RecognitionException {
        final PrintStream oldOut = System.out;
        final PrintStream oldErr = System.err;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setOut(new PrintStream(out));
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            System.setErr(new PrintStream(err));
            String result = "";
            Exception e = null;
            try {
              result = evalString(input);
            } catch (RError ex) {
                e = ex;
            }
            String output = out.toString();
            String error = err.toString();
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
            return new EvalResult(result, output, error, e);
        } finally {
            System.setOut(oldOut);
            System.setErr(oldErr);
        }
    }

    /** Asserts that given source evaluates to the expected result and that no errors were reported and no exceptions
     * raised.
     */
    static void assertEval(String input, String expected) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expected, result.result);
        Assert.assertFalse("Error marker was found", result.stderr.contains(ManageError.ERROR));
        Assert.assertTrue("Exception was thrown!", result.exception == null);
    }

    /** Asserts that given source evaluates to given result and no errors, warnings or exceptions are reported or
     * thrown.
     */
    static void assertEvalNoWarnings(String input, String expected) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expected, result.result);
        Assert.assertFalse("Error marker was found", result.stderr.contains(ManageError.ERROR));
        Assert.assertFalse("Warning marker was found", result.stderr.contains(ManageError.WARNING));
        Assert.assertTrue("Exception was thrown!", result.exception == null);
    }

    // FIXME: this can be done without text capture, using Junit's rules and expectedException
    // FIXME: note, ExpectedException cannot be overriden, but one can implement a MethodRule

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
        EvalResult result = testEval(input);
        Assert.assertTrue("Output expected to contain error: " + expectedError, result.stderr.contains(expectedError));
        Assert.assertTrue("Error marker not found.", result.stderr.contains(ManageError.ERROR));
        Assert.assertTrue("Exception was not thrown", result.exception != null);
    }

    /** Asserts that given source evaluates to an expected result and that a warning is produced in the stderr that
     * contains the specified text.
     */
    static void assertEvalWarning(String input, String expected, String expectedWarning) throws RecognitionException {
        EvalResult result = testEval(input);
        Assert.assertEquals("Evaluation result mismatch", expected, result.result);
        Assert.assertTrue("Output expected to contain warning: " + expectedWarning, result.stderr.contains(expectedWarning));
        Assert.assertTrue("Warning marker not found.", result.stderr.contains(ManageError.WARNING));
        Assert.assertTrue("Exception was thrown!", result.exception == null);
    }

    /** Evaluates the given R expression and returns the returned value.
     */
    static RAny eval(String input) throws RecognitionException {
        ASTNode astNode = TestPP.parse(input);
        try {
            return RContext.eval(astNode, true);
        } finally {
            RSymbol.resetTable(); // some tests may have overwritten some builtins
        }
    }
}
