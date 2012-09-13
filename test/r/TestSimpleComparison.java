package r;

import org.antlr.runtime.*;
import org.junit.*;
public class TestSimpleComparison extends TestBase {

    @Test
    public void testScalars() throws RecognitionException {
        assertEval("{1==1}", "TRUE");
        assertEval("{2==1}", "FALSE");
        assertEval("{1L<=1}", "TRUE");
        assertEval("{1<=0L}", "FALSE");
        assertEval("{x<-2; f<-function(z=x) { if (z<=x) {z} else {x} } ; f(1.4)}", "1.4");
        assertEval("{1==NULL}", "logical(0)");
        assertEval("{1L==1}", "TRUE");
        assertEval("{TRUE==1}", "TRUE");
        assertEval("{TRUE==FALSE}", "FALSE");
        assertEval("{FALSE<=TRUE}", "TRUE");
    }

    @Test
    public void testVectors() throws RecognitionException {
        assertEval("{x<-c(1,2,3,4);y<-c(10,2);x<=y}", "TRUE, TRUE, TRUE, FALSE");
        assertEval("{x<-c(1,2,3,4);y<-2.5;x<=y}", "TRUE, TRUE, FALSE, FALSE");
        assertEval("{x<-c(1,2,3,4);y<-c(2.5+NA,2.5);x<=y}", "NA, TRUE, NA, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-c(2.5+NA,2.5);x<=y}", "NA, TRUE, NA, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-c(TRUE,FALSE);x<=y}", "TRUE, FALSE, FALSE, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-1.5;x<=y}", "TRUE, FALSE, FALSE, FALSE");


    }
}
