package r;

import org.antlr.runtime.*;
import org.junit.*;


public class TestSimpleLoop extends TestBase {

    @Test
    public void testLoops() throws RecognitionException {
        assertEval("{ x<-210 ; repeat { x <- x + 1 ; break } ; x }", "211.0");
        assertEval("{ x<-1 ; repeat { x <- x + 1 ; if (x > 11) { break } } ; x }", "12.0");
        assertEval("{ x<-1 ; repeat { x <- x + 1 ; if (x <= 11) { next } else { break } ; x <- 1024 } ; x }", "12.0");
        assertEval("{ x<-1 ; while(TRUE) { x <- x + 1 ; if (x > 11) { break } } ; x }", "12.0");
        assertEval("{ x<-1 ; while(x <= 10) { x<-x+1 } ; x }", "11.0");
    }
}
