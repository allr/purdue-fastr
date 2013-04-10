package r.simple;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleLoop extends SimpleTestBase {

    @Test
    public void testLoops() throws RecognitionException {
        assertEval("{ x<-210 ; repeat { x <- x + 1 ; break } ; x }", "211.0");
        assertEval("{ x<-1 ; repeat { x <- x + 1 ; if (x > 11) { break } } ; x }", "12.0");
        assertEval("{ x<-1 ; repeat { x <- x + 1 ; if (x <= 11) { next } else { break } ; x <- 1024 } ; x }", "12.0");
        assertEval("{ x<-1 ; while(TRUE) { x <- x + 1 ; if (x > 11) { break } } ; x }", "12.0");
        assertEval("{ x<-1 ; while(x <= 10) { x<-x+1 } ; x }", "11.0");
        assertEval("{ x<-1 ; for(i in 1:10) { x<-x+1 } ; x }", "11.0");
            // factorial
        assertEval("{ f<-function(i) { if (i<=1) {1} else {r<-i; for(j in 2:(i-1)) {r=r*j}; r} }; f(10) }", "3628800.0");
            // Fibonacci
        assertEval("{ f<-function(i) { x<-integer(i); x[1]<-1; x[2]<-1; if (i>2) { for(j in 3:i) { x[j]<-x[j-1]+x[j-2] } }; x[i] } ; f(32) }", "2178309.0");

        assertEval("{ f<-function(r) { x<-0 ; for(i in r) { x<-x+i } ; x } ; f(1:10) ; f(c(1,2,3,4,5)) }", "15.0");
        assertEval("{ f<-function(r) { x<-0 ; for(i in r) { x<-x+i } ; x } ; f(c(1,2,3,4,5)) ; f(1:10) }", "55.0");
    }
}
