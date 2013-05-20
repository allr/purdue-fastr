package r.simple;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleArithmetic extends SimpleTestBase {

    @Test public void testScalars() throws RecognitionException {
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

        assertEval("{ (1+2i)*(3+4i) }", "-5.0+10.0i");
        assertEval("{ x <- 1+2i; y <- 3+4i; x*y }", "-5.0+10.0i");
        assertEval("{ x <- 1+2i; y <- 3+4i; x/y }", "0.44+0.08i");
        assertEval("{ x <- 1+2i; y <- 3+4i; x-y }", "-2.0-2.0i");
        assertEval("{ x <- 1+2i; y <- 3+4i; round(x*x*y/(x+y), digits=5) }", "-1.92308+2.88462i");
        assertEval("{ x <- c(-1.5-1i,-1.3-1i) ; y <- c(0+0i, 0+0i) ; y*y+x }", "-1.5-1.0i, -1.3-1.0i");
        assertEval("{ x <- c(-1.5-1i,-1.3-1i) ; y <- c(0+0i, 0+0i) ; y-x }", "1.5+1.0i, 1.3+1.0i");
        assertEval("{ x <- c(-1-2i,3+10i) ; y <- c(3+1i, -4+5i) ; y-x }", "4.0+3.0i, -7.0-5.0i");
        assertEval("{ x <- c(-1-2i,3+10i) ; y <- c(3+1i, -4+5i) ; y+x }", "2.0-1.0i, -1.0+15.0i");
        assertEval("{ x <- c(-1-2i,3+10i) ; y <- c(3+1i, -4+5i) ; y*x }", "-1.0-7.0i, -62.0-25.0i");
        assertEval("{ x <- c(-1-2i,3+10i) ; y <- c(3+1i, -4+5i) ; round(y/x, digits=5) }", "-1.0+1.0i, 0.34862+0.50459i");

        assertEval("{ round( (1+2i)^(3+4i), digits=5 ) }", "0.12901+0.03392i");
        assertEval("{ round( (1+2i)^2, digits=5 ) }", "-3.0+4.0i");
        assertEval("{ round( (1+2i)^(-2), digits=5 ) }", "-0.12-0.16i");
        assertEval("{ (1+2i)^0 }", "1.0+0.0i");
        assertEval("{ 0^(-1+1i) }", "NaN+NaNi");

        assertEval("{ (0+0i)/(0+0i) }", "NaN+NaNi");
        assertEval("{ (1+0i)/(0+0i) }", "Infinity+NaNi");
        assertEval("{ (0+1i)/(0+0i) }", "NaN+Infinityi");
        assertEval("{ (1+1i)/(0+0i) }", "Infinity+Infinityi");
        assertEval("{ (-1+0i)/(0+0i) }", "-Infinity+NaNi");
        assertEval("{ (-1-1i)/(0+0i) }", "-Infinity-Infinityi");

        assertEval("{ ((0+1i)/0) * ((0+1i)/0) }", "-Infinity+NaNi");
        assertEval("{ ((0-1i)/0) * ((0+1i)/0) }", "Infinity+NaNi");
        assertEval("{ ((0-1i)/0) * ((0-1i)/0) }", "-Infinity+NaNi");
        assertEval("{ ((0-1i)/0) * ((1-1i)/0) }", "-Infinity-Infinityi");
        assertEval("{ ((0-1i)/0) * ((-1-1i)/0) }", "-Infinity+Infinityi");

        assertEval("{ (1+2i) / ((0-1i)/(0+0i)) }", "-0.0+0.0i"); // NOTE: GNU-R prints negative zero as zero
        assertEval("{ 1/((1+0i)/(0+0i)) }", "0.0+0.0i");
        assertEval("{ (1+2i) / ((0-0i)/(0+0i)) }", "NaN+NaNi");

        //TODO:        assertEval("{ ((1+0i)/(0+0i)) ^ (-3) }", "0.0+0.0i");
        //TODO:        assertEval("{ round( ((1+1i)/(0+1i)) ^ (-3.54), digits=5) }", "-0.27428+0.10364i");

        assertEval("{ 1^(1/0) }", "1.0"); // FDLIBM (Math.pow) fails on this
        assertEval("{ (-2)^(1/0) }", "NaN");
        assertEval("{ (-2)^(-1/0) }", "NaN");
        assertEval("{ (1)^(-1/0) }", "1.0");
        assertEval("{ 0^(-1/0) }", "Infinity");
        assertEval("{ 0^(1/0) }", "0.0");
        assertEval("{ 0^(0/0) }", "NaN");
        assertEval("{ 1^(0/0) }", "1.0");
        assertEval("{ (-1)^(0/0) }", "NaN");
        assertEval("{ (-1/0)^(0/0) }", "NaN");
        assertEval("{ (1/0)^(0/0) }", "NaN");
        assertEval("{ (0/0)^(1/0) }", "NaN");
        assertEval("{ (-1/0)^3 }", "-Infinity");
        assertEval("{ (1/0)^(-4) }", "0.0");
        assertEval("{(-1/0)^(-4) }", "0.0");

    }

    @Test public void testVectors() throws RecognitionException {
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
        assertEval("{ z <- c(-1.5-1i,10) ; (z * z)[1] }", "1.25+3.0i");

        assertEval("{ round( c(1,2,3+1i)^3, digits=5 ) }", "1.0+0.0i, 8.0+0.0i, 18.0+26.0i");
        assertEval("{ round( 3^c(1,2,3+1i), digits=5 ) }", "3.0+0.0i, 9.0+0.0i, 12.28048+24.04558i");

        assertEval("{ 1L + 1:2 }", "2L, 3L");
        assertEval("{ 4:3 + 2L }", "6L, 5L");
        assertEval("{ 1:2 + 3:4 }", "4L, 6L");
        assertEval("{ 1:2 + c(1L, 2L) }", "2L, 4L");
        assertEval("{ c(1L, 2L) + 1:4 }", "2L, 4L, 4L, 6L");
        assertEval("{ 1:4 + c(1L, 2L) }", "2L, 4L, 4L, 6L");
        assertEval("{ 2L + 1:2 }", "3L, 4L");
        assertEval("{ 1:2 + 2L }", "3L, 4L");
        assertEval("{ c(1L, 2L) + 2L }", "3L, 4L");
        assertEval("{ 2L + c(1L, 2L) }", "3L, 4L");
        assertEval("{ 1 + 1:2 }", "2.0, 3.0");
        assertEval("{ c(1,2) + 1:2 }", "2.0, 4.0");
        assertEval("{ c(1,2,3,4) + 1:2 }", "2.0, 4.0, 4.0, 6.0");
        assertEval("{ c(1,2,3,4) + c(1L,2L) }", "2.0, 4.0, 4.0, 6.0");
        assertEval("{ 1:2 + 1 }", "2.0, 3.0");
        assertEval("{ 1:2 + c(1,2) }", "2.0, 4.0");
        assertEval("{ 1:2 + c(1,2,3,4) }", "2.0, 4.0, 4.0, 6.0");
        assertEval("{ c(1L,2L) + c(1,2,3,4) }", "2.0, 4.0, 4.0, 6.0");
        assertEval("{ 1L + c(1,2) }", "2.0, 3.0");

    }

    @Test public void testUnary() throws RecognitionException {
        assertEval("{ !TRUE }", "FALSE");
        assertEval("{ !FALSE }", "TRUE");
        assertEval("{ !c(TRUE,TRUE,FALSE,NA) }", "FALSE, FALSE, TRUE, NA");
        assertEval("{ !c(1,2,3,4,0,0,NA) }", "FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, NA");
        assertEval("{ !((0-3):3) }", "FALSE, FALSE, FALSE, TRUE, FALSE, FALSE, FALSE");

        assertEval("{ a <- as.raw(201) ; !a }", "36");
        assertEval("{ a <- as.raw(12) ; !a }", "f3");

        assertEval("{ -(0/0) }", "NaN");
        assertEval("{ -(1/0) }", "-Infinity");
        assertEval("{ -(1[2]) }", "NA");

    }

    @Test public void testMatrices() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+1L }", "     [,1] [,2] [,3]\n[1,]   2L   3L   4L\n[2,]   5L   6L   7L");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m-1 }", "     [,1] [,2] [,3]\n[1,]  0.0  1.0  2.0\n[2,]  3.0  4.0  5.0");
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m+m }", "     [,1] [,2] [,3]\n[1,]   2L   4L   6L\n[2,]   8L  10L  12L");
        assertEval("{ z<-matrix(12)+1 ; z }", "     [,1]\n[1,] 13.0");

        // matrix product
        assertEval("{ x <- 1:3 %*% 9:11 ; x[1] }", "62.0");
        assertEval("{ m<-matrix(1:3, nrow=1) ; 1:2 %*% m }", "     [,1] [,2] [,3]\n[1,]  1.0  2.0  3.0\n[2,]  2.0  4.0  6.0");
        assertEval("{ m<-matrix(1:6, nrow=2) ; 1:2 %*% m }", "     [,1] [,2] [,3]\n[1,]  5.0 11.0 17.0");
        assertEval("{ m<-matrix(1:6, nrow=2) ; m %*% 1:3 }", "     [,1]\n[1,] 22.0\n[2,] 28.0");
        assertEval("{ m<-matrix(1:3, ncol=1) ; m %*% 1:2 }", "     [,1] [,2]\n[1,]  1.0  2.0\n[2,]  2.0  4.0\n[3,]  3.0  6.0");
        assertEval("{ a<-matrix(1:6, ncol=2) ; b<-matrix(11:16, nrow=2) ; a %*% b }", "      [,1]  [,2]  [,3]\n[1,]  59.0  69.0  79.0\n[2,]  82.0  96.0 110.0\n[3,] 105.0 123.0 141.0");
        assertEval("{ a <- array(1:9, dim=c(3,1,3)) ;  a %*% 1:9 }", "      [,1]\n[1,] 285.0");

        // outer product
        assertEval("{ 1:3 %o% 1:2 }", "     [,1] [,2]\n[1,]  1.0  2.0\n[2,]  2.0  4.0\n[3,]  3.0  6.0");

        // precedence
        assertEval("{ 10 / 1:3 %*% 3:1 }", "     [,1]\n[1,]  1.0");
    }

    @Test public void testNonvectorizedLogical() throws RecognitionException {
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
        assertEval("{ NA && FALSE }", "FALSE");
        assertEval("{ NA && NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA && f(NA) ; x } ", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; FALSE && f(FALSE) ; x } ", "1.0");
    }

    @Test public void testVectorizedLogical() throws RecognitionException {
        assertEval("{ 1.1 | 3.15 }", "TRUE");
        assertEval("{ 0 | 0 }", "FALSE");
        assertEval("{ 1 | 0 }", "TRUE");
        assertEval("{ NA | 1 }", "TRUE");
        assertEval("{ NA | 0 }", "NA");
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
        assertEval("{ NA & FALSE }", "FALSE");
        assertEval("{ NA & NA }", "NA");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; NA & f(NA) ; x }", "2.0");
        assertEval("{ x <- 1 ; f <- function(r) { x <<- 2; r } ; FALSE & f(FALSE) ; x }", "2.0");

        assertEval("{ 1:4 & c(FALSE,TRUE) }", "FALSE, TRUE, FALSE, TRUE");

        assertEval("{ a <- as.raw(200) ; b <- as.raw(255) ; a | b }", "ff");
        assertEval("{ a <- as.raw(200) ; b <- as.raw(1) ; a | b }", "c9");
        assertEval("{ a <- as.raw(201) ; b <- as.raw(1) ; a & b }", "01");
    }

    @Test public void testIntegerOverflow() throws RecognitionException {
        assertEvalWarning("{ x <- 2147483647L ; x + 1L }", "NA", "NAs produced by integer overflow");
        assertEvalWarning("{ x <- 2147483647L ; x * x }", "NA", "NAs produced by integer overflow");
        assertEvalWarning("{ x <- -2147483647L ; x - 2L }", "NA", "NAs produced by integer overflow");
        assertEvalWarning("{ x <- -2147483647L ; x - 1L }", "NA", "NAs produced by integer overflow");
        assertEvalNoWarnings("{ 3L %/% 0L }", "NA");
        assertEvalNoWarnings("{ 3L %% 0L }", "NA");
        assertEvalNoWarnings("{ c(3L,3L) %/% 0L }", "NA, NA");
        assertEvalNoWarnings("{ c(3L,3L) %% 0L }", "NA, NA");

    }

    @Test public void testArithmeticUpdate() throws RecognitionException {
        assertEval("{ x <- 3 ; f <- function(z) { if (z) { x <- 1 } ; x <- x + 1L ; x } ; f(FALSE) }", "4.0");
        assertEval("{ x <- 3 ; f <- function(z) { if (z) { x <- 1 } ; x <- 1L + x ; x } ; f(FALSE) }", "4.0");
        assertEval("{ x <- 3 ; f <- function(z) { if (z) { x <- 1 } ; x <- x - 1L ; x } ; f(FALSE) }", "2.0");
    }
}
