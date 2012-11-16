package r;

import org.antlr.runtime.*;
import org.junit.*;
import org.junit.rules.*;

import r.data.*;
import r.errors.*;

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
        assertEval("{ f<-function(a=1,b=2,c=3) {TRUE} ; f(,,) }", "TRUE");

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

        assertEval("{f<-function(a,b,c=2,d) {c} ; g <- function() f(d=8,c=1,2,3) ; g() ; g() }", "1.0");

        assertEval("{ f<-function() { return() } ; f() }", "NULL");
        assertEval("{ f<-function() { return(2) ; 3 } ; f() }", "2.0");

        // function matching
        assertEval("{ x <- function(y) { sum(y) } ; f <- function() { x <- 1 ; x(1:10) } ; f() }", "55L");
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = RError.class)
    public void testUnused1() throws RecognitionException {
        evalString("{ x<-function(){1} ; x(y=1) }");
        Assert.fail("Should not be reached");
    }

    @Test(expected = RError.class)
    public void testUnused2() throws RecognitionException {
        evalString("{ x<-function(){1} ; x(1) }");
        Assert.fail("Should not be reached");
    }

    @Test
    public void testRecursion() throws RecognitionException {
        assertEval("{ f<-function(i) { if(i==1) { 1 } else { j<-i-1 ; f(j) } } ; f(10) }", "1.0");
        assertEval("{ f<-function(i) { if(i==1) { 1 } else { f(i-1) } } ; f(10) }", "1.0");
        assertEval("{ f<-function(i) { if(i<=1) 1 else i*f(i-1) } ; f(10) }", "3628800.0"); // factorial
        assertEval("{ f<-function(i) { if(i<=1L) 1L else i*f(i-1L) } ; f(10L) }", "3628800L"); // factorial
        // 100 times calculate factorial of 120
        // the GNU R outputs 6.689503e+198
        assertEval("{ f<-function(i) { if(i<=1) 1 else i*f(i-1) } ; g<-function(n, f, a) { if (n==1) { f(a) } else { f(a) ; g(n-1, f, a) } } ; g(100,f,120) }", "6.689502913449124E198");
        assertEval("{ f<-function(i) { if (i==1) { 1 } else if (i==2) { 1 } else { f(i-1) + f(i-2) } } ; f(10) }", "55.0"); // Fibonacci
// numbers
        assertEval("{ f<-function(i) { if (i==1L) { 1L } else if (i==2L) { 1L } else { f(i-1L) + f(i-2L) } } ; f(10L) }", "55L");
    }
}
