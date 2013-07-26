package r.simple;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleAssignment extends SimpleTestBase {
    @Test
    public void testAssign() throws RecognitionException {
        assertEval("{ a<-1 }", "1.0");
        assertEval("{ a<-FALSE ; b<-a }", "FALSE");
        assertEval("{ x = if (FALSE) 1 }", "NULL");
    }

    @Test
    public void testSuperAssign() throws RecognitionException {
        assertEval("{ x <<- 1 }", "1.0");
        assertEval("{ x <<- 1 ; x }", "1.0");
        assertEval("{ f <- function() { x <<- 2 } ; f() ; x }", "2.0");
        assertEval("{ x <- 10 ; f <- function() { x <<- 2 } ; f() ; x }", "2.0");
        assertEval("{ x <- 10 ; f <- function() { x <<- 2 ; x } ; c(f(), f()) }", "2.0, 2.0");
        assertEval("{ x <- 10 ; f <- function() { x <- x ; x <<- 2 ; x } ; c(f(), f()) }", "10.0, 2.0");
        assertEval("{ x <- 10 ; g <- function() { f <- function() { x <- x ; x <<- 2 ; x } ; c(f(), f()) } ; g() }", "10.0, 2.0");
        assertEval("{ x <- 10 ; g <- function() { x ; f <- function() { x <- x ; x <<- 2 ; x } ; c(f(), f()) } ; g() }", "10.0, 2.0");
        assertEval("{ x <- 10 ; g <- function() { x <- 100 ; f <- function() { x <- x ; x <<- 2 ; x } ; c(f(), f()) } ; g() }", "100.0, 2.0");
    }

    @Test
    public void testDynamic() throws RecognitionException {
        assertEval("{ l <- quote(x <- 1) ; f <- function() { eval(l) } ; x <- 10 ; f() ; x }", "10.0");
        assertEval("{ l <- quote(x <- 1) ; f <- function() { eval(l) ; x <<- 10 ; get(\"x\") } ; f() }", "1.0");
    }

    @Test
    public void testMisc() throws RecognitionException {
        // some tests are just for corner cases of lookup, not necessarily with assignment
        assertEvalError("{ nonexistent }", " object 'nonexistent' not found");
        assertEvalError("{ f <- function(i) { if (i==1) { x <- 1 } ; x } ; f(1) ; f(2) }", "object 'x' not found");
        assertEval("{ f <- function(i) { if (i==1) { c <- 1 } ; c } ; f(1) ; typeof(f(2)) }", "\"builtin\"");
        assertEvalError("{ f <- function(i) { if (i==1) { x <- 1 } ; x } ; f(1) ; f(1) ; f(2) }", "object 'x' not found");
        assertEval("{ f <- function(i) { if (i==1) { c <- 1 ; x <- 1 } ; if (i!=2) { x } else { c }} ; f(1) ; f(1) ; typeof(f(2)) }", "\"builtin\"");
        assertEval("{ x <- 3 ; f <- function() { assign(\"x\", 4) ; g <- function() { assign(\"y\", 3) ; hh <- function() { assign(\"z\", 6) ; h <- function(s=1) { if (s==2) { x <- 5 } ; x } ; h() } ; hh() } ; g()  } ; f() }", "4.0");
        assertEvalError("{ f <- function() { if (FALSE) { x <- 1 } ; g <- function() { x } ; g() } ; f() }", "object 'x' not found");
        assertEval("{ f <- function() { if (FALSE) { c <- 1 } ; g <- function() { c } ; g() } ; typeof(f()) }", "\"builtin\"");
    }
}

