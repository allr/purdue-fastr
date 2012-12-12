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

        assertEval("{ 3 %/% 2 }", "1.0");
        assertEval("{ 3L %/% 2L }", "1L");
        assertEval("{ 3L %/% -2L }", "-2L");
        assertEval("{ 3 %/% -2 }", "-2.0");
        assertEval("{ 3 %/% 0 }", "Infinity");
        assertEval("{ 3L %/% 0L }", "NA"); // note this would return 0L in earlier versions of R

        assertEval("{ 3 %% 2 }", "1.0");
        assertEval("{ 3L %% 2L }", "1L");
        assertEval("{ 3L %% -2L }", "-1L");
        assertEval("{ 3 %% -2 }", "-1.0");
        assertEval("{ 3 %% 0 }", "NaN");
        assertEval("{ 3L %% 0L }", "NA");

        assertEval("{ 0x10 + 0x10L + 1.28 }", "33.28");
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

        assertEval("{ a <- as.raw(201) ; !a }", "36");
        assertEval("{ a <- as.raw(12) ; !a }", "f3");
    }

    @Test
    public void testMatrices() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+1L }", "     [,1] [,2] [,3]\n[1,]   2L   3L   4L\n[2,]   5L   6L   7L");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m-1 }",  "     [,1] [,2] [,3]\n[1,]  0.0  1.0  2.0\n[2,]  3.0  4.0  5.0");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+m }", "     [,1] [,2] [,3]\n[1,]   2L   4L   6L\n[2,]   8L  10L  12L");
        assertEval("{ z<-matrix(12)+1 ; z }", "     [,1]\n[1,] 13.0");

        // matrix product
        assertEval("{ x <- 1:3 %*% 9:11 ; x[1] }", "62.0");
        assertEval("{ m<-matrix(1:3, nrow=1) ; 1:2 %*% m }", "     [,1] [,2] [,3]\n[1,]  1.0  2.0  3.0\n[2,]  2.0  4.0  6.0");
        assertEval("{ m<-matrix(1:6, nrow=2) ; 1:2 %*% m }", "     [,1] [,2] [,3]\n[1,]  5.0 11.0 17.0");
        assertEval("{ m<-matrix(1:6, nrow=2) ; m %*% 1:3 }", "     [,1]\n[1,] 22.0\n[2,] 28.0");
        assertEval("{ m<-matrix(1:3, ncol=1) ; m %*% 1:2 }", "     [,1] [,2]\n[1,]  1.0  2.0\n[2,]  2.0  4.0\n[3,]  3.0  6.0");
        assertEval("{ a<-matrix(1:6, ncol=2) ; b<-matrix(11:16, nrow=2) ; a %*% b }", "      [,1]  [,2]  [,3]\n[1,]  59.0  69.0  79.0\n[2,]  82.0  96.0 110.0\n[3,] 105.0 123.0 141.0");

        // outer product
        assertEval("{ 1:3 %o% 1:2 }", "     [,1] [,2]\n[1,]  1.0  2.0\n[2,]  2.0  4.0\n[3,]  3.0  6.0");

        // precedence
        assertEval("{ 10 / 1:3 %*% 3:1 }", "     [,1]\n[1,]  1.0");
    }

    public void testNonvectorizedLogical() throws RecognitionException {
        assertEval("{ 1.1 || 3.15 }", "TRUE");
        assertEval("{ 0 || 0 }", "FALSE");
        assertEval("{ 1 || 0 }", "TRUE");
        assertEval("{ NA || 1 }", "TRUE");
        assertEval("{ NA || 0 }", "FALSE");
        assertEval("{ 0 || NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA || f(NA) ; x }", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; TRUE || f(FALSE) ; x } ", "1.0");

        assertEval("{ TRUE && FALSE }", "FALSE");
        assertEval("{ FALSE && FALSE }", "FALSE");
        assertEval("{ FALSE && TRUE }", "FALSE");
        assertEval("{ TRUE && TRUE }", "TRUE");
        assertEval("{ TRUE && NA }", "NA");
        assertEval("{ FALSE && NA }", "FALSE");
        assertEval("{ NA && TRUE }", "NA");
        assertEval("{ NA && FALSE }", "NA");
        assertEval("{ NA && NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA && f(NA) ; x } ", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; FALSE && f(FALSE) ; x } ", "1.0");
    }

    public void testVectorizedLogical() throws RecognitionException {
        assertEval("{ 1.1 | 3.15 }", "TRUE");
        assertEval("{ 0 | 0 }", "FALSE");
        assertEval("{ 1 | 0 }", "TRUE");
        assertEval("{ NA | 1 }", "TRUE");
        assertEval("{ NA | 0 }", "FALSE");
        assertEval("{ 0 | NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA | f(NA) ; x }", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; TRUE | f(FALSE) ; x }", "2.0");

        assertEval("{ TRUE & FALSE }", "FALSE");
        assertEval("{ FALSE & FALSE }", "FALSE");
        assertEval("{ FALSE & TRUE }", "FALSE");
        assertEval("{ TRUE & TRUE }", "TRUE");
        assertEval("{ TRUE & NA }", "NA");
        assertEval("{ FALSE & NA }", "FALSE");
        assertEval("{ NA & TRUE }", "NA");
        assertEval("{ NA & FALSE }", "NA");
        assertEval("{ NA & NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA & f(NA) ; x }", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; FALSE & f(FALSE) ; x }", "2.0");

        assertEval("{ 1:4 & c(FALSE,TRUE) }", "FALSE, TRUE, FALSE, TRUE");

        assertEval("{ a <- as.raw(200) ; b <- as.raw(255) ; a | b }", "ff");
        assertEval("{ a <- as.raw(200) ; b <- as.raw(1) ; a | b }", "c9");
        assertEval("{ a <- as.raw(201) ; b <- as.raw(1) ; a & b }", "01");
    }
}
