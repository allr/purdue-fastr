package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleBuiltins extends TestBase {

    @Test
    public void testSequence() throws RecognitionException {
        assertEval("{5L:10L}", "5L, 6L, 7L, 8L, 9L, 10L");
        assertEval("{5L:(0L-5L)}", "5L, 4L, 3L, 2L, 1L, 0L, -1L, -2L, -3L, -4L, -5L");
        assertEval("{1:10}", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L"); // note: yes, GNU R will convert to integers
        assertEval("{1:(0-10)}", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{1L:(0-10)}", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{1:(0L-10L)}", "1L, 0L, -1L, -2L, -3L, -4L, -5L, -6L, -7L, -8L, -9L, -10L");
        assertEval("{(0-12):1.5}", "-12L, -11L, -10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L, 0L, 1L");
        assertEval("{1.5:(0-12)}", "1.5, 0.5, -0.5, -1.5, -2.5, -3.5, -4.5, -5.5, -6.5, -7.5, -8.5, -9.5, -10.5, -11.5");
        assertEval("{(0-1.5):(0-12)}", "-1.5, -2.5, -3.5, -4.5, -5.5, -6.5, -7.5, -8.5, -9.5, -10.5, -11.5");
        assertEval("{10:1}", "10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L");
        assertEval("{(0-5):(0-9)}", "-5L, -6L, -7L, -8L, -9L");

        assertEval("{seq(1,10)}", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L");
        assertEval("{seq(10,1)}", "10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L");
        assertEval("{seq(from=1,to=3)}", "1L, 2L, 3L");
        assertEval("{seq(to=-1,from=-10)}", "-10L, -9L, -8L, -7L, -6L, -5L, -4L, -3L, -2L, -1L");
        assertEval("{seq(length.out=13.4)}", "1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L");
        assertEval("{seq(length.out=0)}", "integer(0)");
        assertEval("{seq(length.out=1)}", "1L");
    }

    @Test
    public void testArrayConstructors() throws RecognitionException {
        assertEval("{ integer() }", "integer(0)");
        assertEval("{ double() }", "numeric(0)");
        assertEval("{ logical() }", "logical(0)");
        assertEval("{ double(3) }", "0.0, 0.0, 0.0");
        assertEval("{ logical(3L) }", "FALSE, FALSE, FALSE");
    }

    @Test
    public void testMaximum() throws RecognitionException {
        assertEval("{ max((-1):100) }", "100L");
        assertEval("{ max(1:10, 100:200, c(4.0, 5.0)) }", "200.0");
        assertEval("{ max(1:10, 100:200, c(4.0, 5.0), c(TRUE,FALSE,NA)) }", "NA");
    }

    @Test
    public void testOther() throws RecognitionException {
        assertEval("{ rev <- function(x) { if (length(x)) x[length(x):1L] else x } ; rev(1:3) }", "3L, 2L, 1L");
    }
}
