package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleArithmetic extends TestBase {

    @Test
    public void testScalars() throws RecognitionException {
        assertEval("{ 1L+1 }", "2.0");
        assertEval("{ 1L+1L }", "2L");
        assertEval("{ ( 1+1)*(3+2) }", "10.0");
        assertEval("{ 1000000000*100000000000 }", "1.0E20");
        assertEval("{ 1000000000L*1000000000L }", "NA");
        assertEval("{ 1000000000L*1000000000 }", "1.0E18");
        assertEval("{ 1+TRUE }", "2.0");
        assertEval("{ 1L+TRUE }", "2L");
        assertEval("{ 1+FALSE<=0 }", "FALSE");
        assertEval("{ 1L+FALSE<=0 }", "FALSE");
        assertEval("{ TRUE+TRUE+TRUE*TRUE+FALSE+4 }", "7.0");
        assertEval("{ 1L*NA }", "NA");
        assertEval("{ 1+NA }", "NA");
        assertEval("{ 2L^10L }", "1024.0");
    }

    @Test
    public void testVectors() throws RecognitionException {
        assertEval("{ x<-c(1,2,3);x }", "1.0, 2.0, 3.0");
        assertEval("{ x<-c(1,2,3);x*2 }", "2.0, 4.0, 6.0");
        assertEval("{ x<-c(1,2,3);x+2 }", "3.0, 4.0, 5.0");
        assertEval("{ x<-c(1,2,3);x+FALSE }", "1.0, 2.0, 3.0");
        assertEval("{ x<-c(1,2,3);x+TRUE }", "2.0, 3.0, 4.0");
        assertEval("{ x<-c(1,2,3);x*x+x }", "2.0, 6.0, 12.0");
        assertEval("{ x<-c(1,2);y<-c(3,4,5,6);x+y }", "4.0, 6.0, 6.0, 8.0");
        assertEval("{ x<-c(1,2);y<-c(3,4,5,6);x*y }", "3.0, 8.0, 5.0, 12.0");
        assertEval("{ x<-c(1,2);z<-c();x==z }", "logical(0)");
        assertEval("{ x<-1+NA; c(1,2,3,4)+c(x,10) }", "NA, 12.0, NA, 14.0");
        assertEval("{ c(1L,2L,3L)+TRUE }", "2L, 3L, 4L");
        assertEval("{ c(1L,2L,3L)*c(10L) }", "10L, 20L, 30L");
        assertEval("{ c(1L,2L,3L)*c(10,11,12) }", "10.0, 22.0, 36.0");
        assertEval("{ c(1L,2L,3L,4L)-c(TRUE,FALSE) }", "0L, 2L, 2L, 4L");
        assertEval("{ ia<-c(1L,2L);ib<-c(3L,4L);d<-c(5,6);ia+ib+d }", "9.0, 12.0");
    }

    @Test
    public void testUnary() throws RecognitionException {
        assertEval("{ !TRUE }", "FALSE");
        assertEval("{ !FALSE }", "TRUE");
        assertEval("{ !c(TRUE,TRUE,FALSE,NA) }", "FALSE, FALSE, TRUE, NA");
        assertEval("{ !c(1,2,3,4,0,0,NA) }", "FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, NA");
        assertEval("{ !((0-3):3) }", "FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE");
    }

    @Test
    public void testMatrix() throws RecognitionException {
        assertEval("{ x <- 1:3 %*% 9:11 ; x[1] }", "62.0");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+1L }", "     [,1] [,2] [,3]\n[1,]   2L   3L   4L\n[2,]   5L   6L   7L");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m-1 }",  "     [,1] [,2] [,3]\n[1,]  0.0  1.0  2.0\n[2,]  3.0  4.0  5.0");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+m }", "     [,1] [,2] [,3]\n[1,]   2L   4L   6L\n[2,]   8L  10L  12L");
    }
}
