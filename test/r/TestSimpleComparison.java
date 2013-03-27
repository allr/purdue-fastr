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
        assertEval("{FALSE<TRUE}", "TRUE");
        assertEval("{TRUE>FALSE}", "TRUE");
        assertEval("{TRUE>=FALSE}", "TRUE");
        assertEval("{TRUE!=FALSE}", "TRUE");

        assertEval("{ 1+1i == 1-1i }", "FALSE");
        assertEval("{ 1+1i == 1+1i }", "TRUE");
    }

    @Test
    public void testVectors() throws RecognitionException {
        assertEval("{x<-c(1,2,3,4);y<-c(10,2);x<=y}", "TRUE, TRUE, TRUE, FALSE");
        assertEval("{x<-c(1,2,3,4);y<-2.5;x<=y}", "TRUE, TRUE, FALSE, FALSE");
        assertEval("{x<-c(1,2,3,4);y<-c(2.5+NA,2.5);x<=y}", "NA, TRUE, NA, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-c(2.5+NA,2.5);x<=y}", "NA, TRUE, NA, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-c(TRUE,FALSE);x<=y}", "TRUE, FALSE, FALSE, FALSE");
        assertEval("{x<-c(1L,2L,3L,4L);y<-1.5;x<=y}", "TRUE, FALSE, FALSE, FALSE");
        assertEval("{c(1:3,4,5)==1:5}", "TRUE, TRUE, TRUE, TRUE, TRUE");
        assertEval("{0/0 == c(1,2,3,4)}", "NA, NA, NA, NA");
    }

    @Test
    public void testMatrices() throws RecognitionException {
        assertEval("{ matrix(1) > matrix(2) }", "      [,1]\n[1,] FALSE");
        assertEval("{ matrix(1) > NA }", "     [,1]\n[1,]   NA");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m > c(1,2,3) }", "      [,1]  [,2] [,3]\n[1,] FALSE FALSE TRUE\n[2,] FALSE  TRUE TRUE");
    }

    @Test
    public void testStrings() throws RecognitionException {
        assertEval("{ \"a\" <= \"b\" }", "TRUE");
        assertEval("{ \"a\" > \"b\" }", "FALSE");
        assertEval("{ \"2.0\" == 2 }", "TRUE"); // FIXME: incompatible with R because of fastr's character representation of numbers

    }

    @Test
    public void testRaw() throws RecognitionException {
        assertEval("{ a <- as.raw(1) ; b <- as.raw(2) ; a < b }", "TRUE");
        assertEval("{ a <- as.raw(1) ; b <- as.raw(2) ; a > b }", "FALSE");
        assertEval("{ a <- as.raw(1) ; b <- as.raw(2) ; a == b }", "FALSE");
        assertEval("{ a <- as.raw(1) ; b <- as.raw(200) ; a < b }", "TRUE");
        assertEval("{ a <- as.raw(200) ; b <- as.raw(255) ; a < b }", "TRUE");
    }
}
