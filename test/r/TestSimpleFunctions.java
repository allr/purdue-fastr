package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleFunctions extends TestBase {

    @Test
    public void testDefinitions() throws RecognitionException {
        // FIXME: the formatting, white-spaces are not exactly like in GNU R
        assertEval("x<-function(){1}", "function () {1.0}");
        assertEval("{ x<-function(){1} ; x() }", "1.0");
        assertEval("{ x<-function(z){z} ; x(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(){x} ; x<-2 ; f() }", "2.0");
        assertEval("{ x<-1 ; f<-function(x){x} ; f(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(x){a<-1;b<-2;x} ; f(TRUE) }", "TRUE");
        assertEval("{ f<-function(x){g<-function(x) {x} ; g(x) } ; f(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(x){a<-1; b<-2; g<-function(x) {b<-3;x} ; g(b) } ; f(TRUE) }", "2.0");
        assertEval("{ x<-1 ; f<-function(z) { if (z) { x<-2 } ; x } ; x<-3 ; f(FALSE) }", "3.0");
    }

}
