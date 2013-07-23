package r.simple;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleIfEvaluator extends SimpleTestBase {

    @Test
    public void testIf2() throws RecognitionException {
        assertEval("if(TRUE) 1 else 2", "1.0");
        assertEval("if(FALSE) 1 else 2", "2.0");
    }

    @Test
    public void testIfNot1() throws RecognitionException {
        assertEval("if(!FALSE) 1 else 2", "1.0");
        assertEval("if(!TRUE) 1 else 2", "2.0");
    }

    @Test
    public void testIf() throws RecognitionException {
        assertEval("{ x <- 2 ; if (1==x) TRUE else 2 }", "2.0");
        assertEvalError("{ x <- 2 ; if (NA) x <- 3 ; x }", "missing value where TRUE/FALSE needed");
        assertEvalError("{ f <- function(x) { if (x) 1 else 2 } ; f(NA)  }", "missing value where TRUE/FALSE needed");
        assertEvalError("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(NA) }", "missing value where TRUE/FALSE needed");
        assertEval("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(TRUE) }", "1.0");
        assertEval("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(FALSE) }", "2.0");

        assertEvalError("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(\"hello\") }", "argument is not interpretable as logical");
        assertEvalError("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(logical()) }", "argument is of length zero");
        assertEvalWarning("{ f <- function(x) { if (x) 1 else 2 } ; f(1) ; f(1:3) }", "1.0", "the condition has length > 1 and only the first element will be used");
        assertEvalError("{ f <- function(x) { if (x==2) 1 else 2 } ; f(1) ; f(NA) }", "missing value where TRUE/FALSE needed");

        assertEval("{ if (TRUE==FALSE) TRUE else FALSE }", "FALSE");
        assertEvalError("{ if (NA==TRUE) TRUE else FALSE }", "missing value where TRUE/FALSE needed");
        assertEvalError("{ if (TRUE==NA) TRUE else FALSE }", "missing value where TRUE/FALSE needed");
        assertEval("{ if (FALSE==TRUE) TRUE else FALSE }", "FALSE");
        assertEval("{ if (FALSE==1) TRUE else FALSE }", "FALSE");
        assertEval("{ f <- function(v) { if (FALSE==v) TRUE else FALSE } ; f(TRUE) ; f(1) }", "FALSE");



    }
}
