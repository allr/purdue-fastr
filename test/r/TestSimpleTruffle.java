package r;

import org.antlr.runtime.*;
import org.junit.*;

// FIXME: I've seen tests crash when run by JUnit, but pass when run manually through console...
public class TestSimpleTruffle extends TestBase {

    @Test
    public void test1() throws RecognitionException {
        assertEval("{ f<-function(i) { if(i==1) { 1 } else { j<-i-1 ; f(j) } } ; f(10) }", "1.0");
        assertEval("{ f<-function(i) { if(i==1) { i } } ; f(1) ; f(2) }", "NULL");
        assertEval("{ f<-function(i) {i} ; f(1) ; f(2) }", "2.0");
        assertEval("{ f<-function() { 1:5 } ; f(); f() }", "1L, 2L, 3L, 4L, 5L");
        assertEval("{ f<-function() { length(c(1,2)) } ; f(); f() }", "2L");
        assertEval("{ f<-function() { if (!1) TRUE } ; f(); f() }", "NULL");
        assertEval("{ f<-function() { if (1) TRUE } ; f(); f() }", "TRUE");
        assertEval("{ f<-function() { if (!TRUE) 1 } ; f(); f() }", "NULL");
        assertEval("{ f<-function() { if (if (1) {TRUE} else {FALSE} ) 1 } ; f(); f() }", "1.0");
        assertEval("{ f<-function() { logical(0) } ; f(); f() }", "logical(0)");

        assertEval("{ f<-function(i) { if (FALSE) { i } } ; f(2) ; f(1) }", "NULL");
        assertEval("{ f<-function(i) { if (TRUE) { i } } ; f(2) ; f(1) }", "1.0");
        assertEval("{ f<-function(i) { i ; if (FALSE) { 1 } else { i } } ; f(2) ; f(1) }", "1.0");
        assertEval("{ f<-function(i) { i ; if (TRUE) { 1 } else { i } } ; f(2) ; f(1) }", "1.0");
        assertEval("{ f<-function(i) { if(i==1) { 1 } else { i } } ; f(2) ; f(2) }", "2.0");
        assertEval("{ f<-function(i) { if(i==1) { 1 } else { i } } ; f(2) ; f(1) }", "1.0");
    }

    @Test
    public void testLoop() throws RecognitionException {
        assertEval("{ f<-function() { x<-210 ; repeat { x <- x + 1 ; break } ; x } ; f() ; f() }", "211.0");
        assertEval("{ f<-function() { x<-1 ; repeat { x <- x + 1 ; if (x > 11) { break } } ; x } ; f(); f() }", "12.0");
        assertEval("{ f<-function() { x<-1 ; repeat { x <- x + 1 ; if (x <= 11) { next } else { break } ; x <- 1024 } ; x } ; f() ; f() }", "12.0");
        assertEval("{ f<-function() { x<-1 ; while(TRUE) { x <- x + 1 ; if (x > 11) { break } } ; x } ; f(); f() }", "12.0");
        assertEval("{ f<-function() { x<-1 ; while(TRUE) { x <- x + 1 ; if (x > 11) { break } } ; x } ; f(); f() }", "12.0");
        assertEval("{ f<-function() { x<-1 ; while(x <= 10) { x<-x+1 } ; x } ; f(); f() }", "11.0");
        assertEval("{ f<-function() { x<-1 ; for(i in 1:10) { x<-x+1 } ; x } ; f(); f() }", "11.0");
    }
}
