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
}

