package r;

import org.junit.*;

public class TestBase {

    public static final boolean VERBOSE = false; // show the outputs of the running benchmarks
    public static final boolean DEBUGGING_RUN = true; // run each benchmark first without capturing its output

    /**
     * A simple class that holds the result of a test evaluation. Contains the actual result reported by eval, the
     * captured std err and std out of the execution and the message of a Java exception if any was thrown during the
     * execution, or null.
     */
    public static class EvalResult {

        public final String result; // result of the evaluation (last expression)
        public final String stdout; // standard out as reported during the evaluation
        public final String stderr; // stderr as reported during the evaluation
        public final String exception; // exception, or null if no exception was thrown

        public EvalResult(String result, String stdout, String stderr, Exception e) {
            this.result = result;
            this.stdout = stdout;
            this.stderr = stderr;
            if (e == null) {
                this.exception = null;
            } else {
                this.exception = e.getClass().getName() + e.getMessage();
            }
        }
    }

    protected static void fail(String msg, EvalResult result) {
        System.err.println(" --- " + msg + ":");
        System.err.println(" --- actual output: ");
        System.err.println(result.stdout);
        System.err.println(" --- actual error output: ");
        System.err.println(result.stderr);
        System.err.println(" --- actual result: ");
        System.err.println(result.result);
        System.err.println(" --- end of report ---");
        Assert.fail();
    }
}
