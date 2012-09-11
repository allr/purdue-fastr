package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleFunctions extends TestBase {

    @Test
    public void testDefinitions() throws RecognitionException {
        // FIXME: the formatting, white-spaces are not exactly like in GNU R
        assertEval("x<-function(){1}", "function () { 1.0 }");
        assertEval("{ x<-function(){1} ; x() }", "1.0");
        assertEval("{ x<-function(z){z} ; x(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(){x} ; x<-2 ; f() }", "2.0");
        assertEval("{ x<-1 ; f<-function(x){x} ; f(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(x){a<-1;b<-2;x} ; f(TRUE) }", "TRUE");
        assertEval("{ f<-function(x){g<-function(x) {x} ; g(x) } ; f(TRUE) }", "TRUE");
        assertEval("{ x<-1 ; f<-function(x){a<-1; b<-2; g<-function(x) {b<-3;x} ; g(b) } ; f(TRUE) }", "2.0");
        assertEval("{ x<-1 ; f<-function(z) { if (z) { x<-2 } ; x } ; x<-3 ; f(FALSE) }", "3.0");
        assertEval("{ f<-function() {z} ; z<-2 ; f() }", "2.0");
        assertEval("{ x<-1 ; g<-function() { x<-12 ; f<-function(z) { if (z) { x<-2 } ; x } ; x<-3 ; f(FALSE) } ; g() }", "3.0");
        assertEval("{ x<-function() { z<-211 ; function(a) { if (a) { z } else { 200 } } } ; f<-x() ; z<-1000 ; f(TRUE) }", "211.0");
// F// assertEval("{ f<-function(a=1,b=2,c=3) {TRUE} ; f(,,) }", "TRUE");

        assertEval("{ f<-function(x=2) {x} ; f() } ", "2.0");
        assertEval("{ f<-function(a,b,c=2,d) {c} ; f(1,2,c=4,d=4) }", "4.0");
        assertEval("{ f<-function(a,b,c=2,d) {c} ; f(1,2,d=8,c=1) }", "1.0");
        assertEval("{ f<-function(a,b,c=2,d) {c} ; f(1,d=8,2,c=1) }", "1.0");
        assertEval("{ f<-function(a,b,c=2,d) {c} ; f(d=8,1,2,c=1) }", "1.0");
        assertEval("{ f<-function(a,b,c=2,d) {c} ; f(d=8,c=1,2,3) }", "1.0");
        assertEval("{ f<-function(a=10,b,c=20,d=20) {c} ; f(4,3,5,1) }", "5.0");

        assertEval("{ x<-1 ; z<-TRUE ; f<-function(y=x,a=z,b) { if (z) {y} else {z}} ; f(b=2) }", "1.0");
        assertEval("{ x<-1 ; z<-TRUE ; f<-function(y=x,a=z,b) { if (z) {y} else {z}} ; f(2) }", "2.0");
        assertEval("{ x<-1 ; f<-function(x=x) { x } ; f(x=x) }", "1.0");
        assertEval("{ f<-function(z, x=if (z) 2 else 3) {x} ; f(FALSE) }", "3.0");
    }

}
