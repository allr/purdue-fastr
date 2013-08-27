package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import r.errors.RError;


public class TestSimpleArrays extends SimpleTestBase {

    @Test
    public void testArrayBuiltin() throws RecognitionException {
        // array with no arguments produces array of length 1
        assertTrue("{ a = array(); length(a) == 1; }");

        // empty arg has first element NA
        assertTrue("{ a = array(); is.na(a[1]); }");

        // dimnames not implemented yet
        // dimension names of empty array are null
        // assertTrue("{ a = array(); is.null(dimnames(a)); }");

        // empty array has single dimension that is 1
        assertTrue("{ a <- array(); dim(a) == 1; }");

        // wrapping in arrays work even when prohibited by help
        assertTrue("{ a = array(1:10, dim = c(2,6)); length(a) == 12; }");

        // negative length vectors are not allowed is the error reported by gnu-r
        // negative dims not allowed by R, special GNU message
        assertEvalError("{ array(dim=c(-2,2)); }", RError.DIMS_CONTAIN_NEGATIVE_VALUES);

        // negative dims not allowed
        assertEvalError("{ array(dim=c(-2,-2)); }", RError.DIMS_CONTAIN_NEGATIVE_VALUES);

        // zero dimension array has length 0
        assertTrue("{ length(array(dim=c(1,0,2,3))) == 0; }");

        // double dimensions work and are rounded down always
        assertTrue("{ a = dim(array(dim=c(2.1,2.9,3.1,4.7))); a[1] == 2 && a[2] == 2 && a[3] == 3 && a[4] == 4; }");
    }

    @Test
    public void testMatrixBuiltin() {
        // empty matrix length is 1
        assertTrue("{ length(matrix()) == 1; }");
    }


    @Test
    public void testArraySimpleRead() throws RecognitionException {
        // simple read
        assertTrue("{ a = array(1:27,c(3,3,3)); a[1,1,1] == 1 && a[3,3,3] == 27 && a[1,2,3] == 22 && a[3,2,1] == 6; }");

        // empty selectors reads the whole array
        assertTrue("{ a = array(1:27, c(3,3,3)); b = a[,,]; d = dim(b); d[1] == 3 && d[2] == 3 && d[3] == 3; }");

        // dimensions of 1 are dropped
        assertTrue("{ a = array(1,c(3,3,3)); a = dim(a[,1,]); length(a) == 2 && a[1] == 3 && a[2] == 3; }");

        // when all dimensions are dropped, dim is null
        assertTrue("{ a = array(1,c(3,3,3)); is.null(dim(a[1,1,1])); }");

        // last dimension is dropped
        assertTrue("{ a = array(1,c(3,3,3)); is.null(dim(a[1,1,])); } ");

        // dimensions of 1 are not dropped when requested (with subset)
        assertTrue("{ a = array(1,c(3,3,3)); a = dim(a[1,1,1, drop = FALSE]); length(a) == 3 && a[1] == 1 && a[2] == 1 && a[3] == 1; }");

        // with subscript, dimensions are always dropped
        assertTrue("{ m <- array(1:4, dim=c(4,1,1)) ; x <- m[[2,1,1,drop=FALSE]] ; is.null(dim(x)) }");

        // fallback to one dimensional read
        assertTrue("{ a = array(1:27, c(3,3,3)); a[1] == 1 && a[27] == 27 && a[22] == 22 && a[6] == 6; }");

        // error when different dimensions given
        assertEvalError("{ a = array(1,c(3,3,3)); a[2,2]; }", RError.INCORRECT_DIMENSIONS);

        // calculating result dimensions
        assertTrue("{ m <- array(c(1,2,3), dim=c(3,1,1)) ; x <- m[1:2,1,1] ; x[1] == 1 && x[2] == 2 }");
        assertTrue("{ m <- array(c(1,2,3), dim=c(3,1,1)) ; x <- dim(m[1:2,1,1]) ; is.null(x) }");
        assertTrue("{ m <- array(c(1,2,3), dim=c(3,1,1)) ; x <- dim(m[1:2,1,1,drop=FALSE]) ; x[1] == 2 && x[2] == 1 && x[3] == 1 }");
        assertTrue("{ m <- array(c(1,2,3), dim=c(3,1,1)) ; x <- m[1:2,1,integer()] ; d <- dim(x) ; length(x) == 0 }");
        assertTrue("{ m <- array(c(1,2,3), dim=c(3,1,1)) ; x <- m[1:2,1,integer()] ; d <- dim(x) ; d[1] == 2 && d[2] == 0 }");
    }

    @Test
    public void testArraySubsetAndSelection() throws RecognitionException {
        // subset operator works for arrays
        assertTrue("{ array(1,c(3,3,3))[1,1,1] == 1; }");

        // selection operator works for arrays
        assertTrue("{ array(1,c(3,3,3))[[1,1,1]] == 1; }");

        // selection on multiple elements fails in arrays
        assertEvalError("{ array(1,c(3,3,3))[[,,]]; }", String.format(RError.INVALID_SUBSCRIPT_TYPE, "symbol"));

        // selection on multiple elements fails in arrays
        assertEvalError("{ array(1,c(3,3,3))[[c(1,2),1,1]]; }", RError.SELECT_MORE_1);

        // last column
        assertEval("{ m <- array(1:24, dim=c(2,3,4)) ; m[,,2] }", "     [,1] [,2] [,3]\n[1,]   7L   9L  11L\n[2,]   8L  10L  12L");
        assertEval("{ m <- array(1:24, dim=c(2,3,4)) ; m[,,2,drop=FALSE] }", ", , 1\n\n     [,1] [,2] [,3]\n[1,]   7L   9L  11L\n[2,]   8L  10L  12L");
        assertEval("{ m <- array(1:24, dim=c(2,3,4)) ; f <- function(i) { m[,,i] } ; f(1) ; f(2) ; dim(f(1:2)) }", "2L, 3L, 2L");
        assertEval("{ m <- array(1:24, dim=c(2,3,4)) ; f <- function(i) { m[,,i] } ; f(1[2]) ; f(3) }", "     [,1] [,2] [,3]\n[1,]  13L  15L  17L\n[2,]  14L  16L  18L");
    }

    @Test
    public void testMatrixSubsetAndSelection() throws RecognitionException {
        // subset operator works for matrices
        assertTrue("{ matrix(1,3,3)[1,1] == 1; }");

        // selection operator works for arrays
        assertTrue("{ matrix(1,3,3)[[1,1]] == 1; }");

        // selection on multiple elements fails in matrices with empty selector
        assertEvalError("{ matrix(1,3,3)[[,]]; }", String.format(RError.INVALID_SUBSCRIPT_TYPE, "symbol"));

        // selection on multiple elements fails in matrices
        assertEvalError("{ matrix(1,3,3)[[c(1,2),1]]; }", RError.SELECT_MORE_1);

        assertEval("{  m <- matrix(1:6, nrow=2) ;  m[1,NULL] }", "integer(0)");
    }

    @Test
    public void testArrayUpdate() {
        // update to matrix works
        assertTrue("{ a = matrix(1,2,2); a[1,2] = 3; a[1,2] == 3; }");

        // update to an array works
        assertTrue("{ a = array(1,c(3,3,3)); a[1,2,3] = 3; a[1,2,3] == 3; }");

        // update returns the rhs
        assertTrue("{ a = array(1,c(3,3,3)); (a[1,2,3] = 3) == 3; }");

        // update of shared object does the copy
        assertTrue("{ a = array(1,c(3,3,3)); b = a; b[1,2,3] = 3; a[1,2,3] == 1 && b[1,2,3] == 3; }");

        // update where rhs depends on the lhs
        assertTrue("{ x <- array(c(1,2,3), dim=c(3,1,1)) ; x[1:2,1,1] <- sqrt(x[2:1]) ; x[1] == sqrt(2) && x[2] == 1 && x[3] == 3 }");

    }

    @Test
    public void testLhsCopy() {
        // lhs gets upgraded to int
        assertTrue("{ a = array(TRUE,c(3,3,3)); a[1,2,3] = 8L; a[1,2,3] == 8L; }");

        // lhs logical gets upgraded to double
        assertTrue("{ a = array(TRUE,c(3,3,3)); a[1,2,3] = 8.1; a[1,2,3] == 8.1; }");

        // lhs integer gets upgraded to double
        assertTrue("{ a = array(1L,c(3,3,3)); a[1,2,3] = 8.1; a[1,2,3] == 8.1; }");

        // lhs logical gets upgraded to complex
        assertTrue("{ a = array(TRUE,c(3,3,3)); a[1,2,3] = 2+3i; a[1,2,3] == 2+3i; }");

        // lhs integer gets upgraded to complex
        assertTrue("{ a = array(1L,c(3,3,3)); a[1,2,3] = 2+3i; a[1,2,3] == 2+3i; }");

        // lhs double gets upgraded to complex
        assertTrue("{ a = array(1.3,c(3,3,3)); a[1,2,3] = 2+3i; a[1,2,3] == 2+3i; }");

        // lhs logical gets upgraded to string
        assertTrue("{ a = array(TRUE,c(3,3,3)); a[1,2,3] = \"2+3i\"; a[1,2,3] == \"2+3i\" && a[1,1,1] == \"TRUE\"; }");

        // lhs integer gets upgraded to string
        assertTrue("{ a = array(1L,c(3,3,3)); a[1,2,3] = \"2+3i\"; a[1,2,3] == \"2+3i\" && a[1,1,1] == \"1L\"; }");

        // lhs double gets upgraded to string
        assertTrue("{ a = array(1.5,c(3,3,3)); a[1,2,3] = \"2+3i\"; a[1,2,3] == \"2+3i\" && a[1,1,1] == \"1.5\"; }");
    }

    @Test
    public void testRhsCopy() {
        // rhs logical gets upgraded to int
        assertTrue("{ a = array(7L,c(3,3,3)); b = TRUE; a[1,2,3] = b; a[1,2,3] == 1L && a[1,1,1] == 7L; }");

        // rhs logical gets upgraded to double
        assertTrue("{ a = array(1.7,c(3,3,3)); b = TRUE; a[1,2,3] = b; a[1,2,3] == 1 && a[1,1,1] == 1.7; }");

        // rhs logical gets upgraded to complex
        assertTrue("{ a = array(3+2i,c(3,3,3)); b = TRUE; a[1,2,3] = b; a[1,2,3] == 1 && a[1,1,1] == 3+2i; } ");

        // rhs logical gets upgraded to string
        assertTrue("{ a = array(\"3+2i\",c(3,3,3)); b = TRUE; a[1,2,3] = b; a[1,2,3] == \"TRUE\" && a[1,1,1] == \"3+2i\"; }");

        // rhs int gets upgraded to double
        assertTrue("{ a = array(1.7,c(3,3,3)); b = 3L; a[1,2,3] = b; a[1,2,3] == 3 && a[1,1,1] == 1.7; }");

        // rhs int gets upgraded to complex
        assertTrue("{ a = array(3+2i,c(3,3,3)); b = 4L; a[1,2,3] = b; a[1,2,3] == 4 && a[1,1,1] == 3+2i; }");
        assertTrue("{ m <- array(c(1+1i,2+2i,3+3i), dim=c(3,1,1)) ; m[1:2,1,1] <- c(100L,101L) ; m ; m[1,1,1] == 100 && m[2,1,1] == 101 }");

        // rhs logical gets upgraded to string
        assertTrue("{ a = array(\"3+2i\",c(3,3,3)); b = 7L; a[1,2,3] = b; a[1,2,3] == \"7L\" && a[1,1,1] == \"3+2i\"; }");

        // rhs double gets upgraded to complex
        assertTrue("{ a = array(3+2i,c(3,3,3)); b = 4.2; a[1,2,3] = b; a[1,2,3] == 4.2 && a[1,1,1] == 3+2i; }");

        // rhs complex gets upgraded to string
        assertTrue("{ a = array(\"3+2i\",c(3,3,3)); b = 2+3i; a[1,2,3] = b; a[1,2,3] == \"2.0+3.0i\" && a[1,1,1] == \"3+2i\"; }");
    }

    @Test
    public void testMultiDimensionalUpdate() {
        // update matrix by vector, rows
        assertTrue("{ a = matrix(1,3,3); a[1,] = c(3,4,5); a[1,1] == 3 && a[1,2] == 4 && a[1,3] == 5; }");

        // update matrix by vector, cols
        assertTrue("{ a = matrix(1,3,3); a[,1] = c(3,4,5); a[1,1] == 3 && a[2,1] == 4 && a[3,1] == 5; }");

        // update array by vector, dim 3
        assertTrue("{ a = array(1,c(3,3,3)); a[1,1,] = c(3,4,5); a[1,1,1] == 3 && a[1,1,2] == 4 && a[1,1,3] == 5; }");

        // update array by vector, dim 2
        assertTrue("{ a = array(1,c(3,3,3)); a[1,,1] = c(3,4,5); a[1,1,1] == 3 && a[1,2,1] == 4 && a[1,3,1] == 5; }");

        // update array by vector, dim 1
        assertTrue("{ a = array(1,c(3,3,3)); a[,1,1] = c(3,4,5); a[1,1,1] == 3 && a[2,1,1] == 4 && a[3,1,1] == 5; }");

        // update array by matrix
        assertTrue("{ a = array(1,c(3,3,3)); a[1,,] = matrix(1:9,3,3); a[1,1,1] == 1 && a[1,3,1] == 3 && a[1,3,3] == 9; }");

    }

    @Test
    public void testBugIfiniteLoopInGeneralizedRewriting() {
        assertTrue("{ m <- array(1:3, dim=c(3,1,1)) ; f <- function(x,v) { x[1:2,1,1] <- v ; x } ; f(m,10L) ; f(m,10) ; f(m,c(11L,12L)); m[1,1,1] == 1L && m[2,1,1] == 2L && m[3,1,1] == 3L }");
    }


    @Test
    public void testMatrixSimpleRead() {
        // last dimension is dropped
        assertTrue("{ a = matrix(1,3,3); is.null(dim(a[1,])); }");
    }


    @Test
    public void testDefinitions() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) \nm }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix(1:6, ncol=3, byrow=TRUE) \nm }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2, byrow=TRUE) \nm }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix() \nm }", "     [,1]\n[1,]   NA");
        assertEval("{ matrix( (1:6) * (1+3i), nrow=2 ) }", "         [,1]      [,2]      [,3]\n[1,] 1.0+3.0i  3.0+9.0i 5.0+15.0i\n[2,] 2.0+6.0i 4.0+12.0i 6.0+18.0i");
        assertEval("{ matrix( as.raw(101:106), nrow=2 ) }", "     [,1] [,2] [,3]\n[1,]   65   67   69\n[2,]   66   68   6a");
        assertEval("{ x <- list(\"a\",1,TRUE,NULL) ; dim(x) <- c(2,1,2) ; x }", ", , 1\n\n     [,1]\n[1,]  \"a\"\n[2,]  1.0\n\n, , 2\n\n     [,1]\n[1,] TRUE\n[2,] NULL");
    }

    @Test
    public void testSelection() throws RecognitionException {
        assertEval("{ m <- matrix(c(1,2,3,4,5,6), nrow=3) \nm[0] }", "numeric(0)");
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) \nm[0] }", "list()");
        assertEval("{ m <- matrix(1:6, nrow=2) \nm[upper.tri(m)] }", "3L, 5L, 6L");
        assertEvalError("{ a <- 1:3; a[1,2,3] <- 10 }", "incorrect number of subscripts");
        assertEvalError("{ f <- function() {3} ; f[1,1,2] }", "object of type 'closure' is not subsettable");
        assertEvalError("{ x <- list(a=1,b=2,c=3) ; x[1,1,2] }", "incorrect number of dimensions");
        assertEvalError("{ x <- 1:3 ; x[1,1,2] }", "incorrect number of dimensions");
        assertEvalError("{ x <- 1:3 ; dim(x) <- c(3,1) ; x[1,1,2] }", "incorrect number of dimensions");

        // matrix
        assertEvalError("{ f <- function(b) { b[,2] } ; f(matrix(1:4,nrow=2)) ; f(f) }", "object of type 'closure' is not subsettable");
        assertEvalError("{ f <- function(b) { b[,2] } ; f(matrix(1:4,nrow=2)) ; f(1:3) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b) { b[,2] } ; f(matrix(1:4,nrow=2)) ; z <- 1:3; dim(z) <- c(3,1,1,1) ; f(z) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b) { b[,4] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEval("{ f <- function(b) { b[,0] } ; f(matrix(1:4,nrow=2)) }", "    \n[1,]\n[2,]");
        assertEval("{ m <- matrix(1:4,nrow=2) ; m[2,2,drop=TRUE] }", "4L");
        assertEval("{ m <- matrix(1:4,nrow=2) ; m[2,2,drop=FALSE] }", "     [,1]\n[1,]   4L");
        assertEval("{ m <- matrix(1:4,nrow=2) ; m[,2,drop=FALSE] }", "     [,1]\n[1,]   3L\n[2,]   4L");
        assertEval("{ m <- matrix(1:4,nrow=2) ; m[,2,drop=TRUE] }", "3L, 4L");
        assertEvalError("{ f <- function(b) { b[2,] } ; f(matrix(1:4,nrow=2)) ; f(f) }", "object of type 'closure' is not subsettable");
        assertEvalError("{ f <- function(b) { b[2,] } ; f(matrix(1:4,nrow=2)) ; f(1:3) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b) { b[2,] } ; f(matrix(1:4,nrow=2)) ; z <- 1:3; dim(z) <- c(3,1,1,1) ; f(z) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b) { b[4,] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEval("{ f <- function(b) { b[0,] } ; f(matrix(1:4,nrow=2)) }", "     [,1] [,2]");
        assertEvalError("{ f <- function(b) { b[1+0i,] } ; f(matrix(1:4,nrow=2)) }", "invalid subscript type 'complex'");
        assertEvalError("{ f <- function(b,x,y) { b[1:2,2:1] } ; f(matrix(1:4,nrow=2)) ; f(f) }", "object of type 'closure' is not subsettable");
        assertEvalError("{ f <- function(b,x,y) { b[1:2,2:1] } ; f(1:4) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b,x,y) { b[1:2,2:1] } ; m <- matrix(1:4,nrow=2) ; dim(m) <- c(2,2,1,1) ; f(m) }", "incorrect number of dimensions");
        assertEval("{ f <- function(b,x,y) { b[2:1,2:1] } ; f(matrix(1:4,nrow=2)) }", "     [,1] [,2]\n[1,]   4L   2L\n[2,]   3L   1L");
        assertEval("{ f <- function(b,x,y) { b[2:1,1:2] } ; f(matrix(1:4,nrow=2)) }", "     [,1] [,2]\n[1,]   2L   4L\n[2,]   1L   3L");
        assertEval("{ f <- function(b,x,y) { b[1:2,2:1] } ; f(matrix(1:4,nrow=2)) }", "     [,1] [,2]\n[1,]   3L   1L\n[2,]   4L   2L");
        assertEvalError("{ f <- function(b,x,y) { b[-1:1,2:1] } ; f(matrix(1:4,nrow=2)) }", "only 0's may be mixed with negative subscripts");
        assertEval("{ f <- function(b,x,y) { b[0:2,2:1] } ; f(matrix(1:4,nrow=2)) }", "     [,1] [,2]\n[1,]   3L   1L\n[2,]   4L   2L");
        assertEval("{ f <- function(b,x,y) { b[1:2,2:2,drop=TRUE] } ; f(matrix(1:4,nrow=2)) }", "3L, 4L");
        assertEval("{ f <- function(b,x,y) { b[1:1,2:1,drop=TRUE] } ; f(matrix(1:4,nrow=2)) }", "3L, 1L");
        assertEval("{ f <- function(b,x,y) { b[1:1,2:2,drop=FALSE] } ; f(matrix(1:4,nrow=2)) }", "     [,1]\n[1,]   3L");
        assertEval("{ f <- function(b,x,y) { b[-1:-2,2:2,drop=FALSE] } ; f(matrix(1:4,nrow=2)) }", "     [,1]");
        assertEvalWarning("{ f <- function(b,x,y) { b[1e100:1e100,2:2] } ; f(matrix(1:4,nrow=2)) }", "NA", "NAs introduced by coercion");
        assertEval("{ f <- function(b,x,y) { b[-2L:-2L,2:2] } ; f(matrix(1:4,nrow=2)) }", "3L");
        assertEval("{ f <- function(b,x,y) { b[TRUE:FALSE,2:2] } ; f(matrix(1:4,nrow=2)) }", "3L");
        assertEvalError("{ f <- function(b,x,y) { b[1:2,2:4] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEvalError("{ f <- function(b,x,y) { b[1:4,2:1] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEvalError("{ f <- function(b,x,y) { b[4:1,2:1] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEvalError("{ f <- function(b,x,y) { b[1:2,4:2] } ; f(matrix(1:4,nrow=2)) }", "subscript out of bounds");
        assertEval("{ f <- function(b,x,y) { b[[2,1]] } ; f(matrix(1:4,nrow=2)) }", "2L");
        assertEval("{ f <- function(b,x,y) { b[[2,1]] } ; f(matrix(as.list(1:4),nrow=2)) }", "2L");
        assertEvalError("{ f <- function(b,x,y) { b[c(2,3),1] } ; f(f) }", "object of type 'closure' is not subsettable");
        assertEvalError("{ f <- function(b,x,y) { b[c(2,3),1] } ; f(1:4) }", "incorrect number of dimensions");
        assertEvalError("{ f <- function(b,x,y) { b[c(2,3),1] } ; b <- 1:4 ; dim(b) <- c(4,1,1); f(b) }", "incorrect number of dimensions");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,4); x <- b[drop=FALSE,,1:1] ; x }", "     [,1]\n[1,]   1L");
        assertEval("{ f <- function(d) { b <- matrix(1:4,nrow=2,ncol=2) ; b[,drop=d,2] } ; f(0) ; f(1L) }", "3L, 4L");
        assertEval("{ z <- 1 ; f <- function(d) { b <- matrix(1:4,nrow=2,ncol=2) ; b[{z<<-z+1;1},drop=z<<-z*10,{z<<-z*2;2}] } ; f(0) ; f(1L) ; z }", "820.0");

        // arrays
        assertEvalError("{ b <- 1:4 ; dim(b) <- c(1,0,4,1); x <- b[,,,1] ; dim(x) }", "dims [product 0] do not match the length of object [4]");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,4,1); x <- b[,,,1] ; x }", "1L, 2L, 3L, 4L");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[,,,1,drop=FALSE] ; x }", ", , 1, 1\n\n     [,1]\n[1,]   1L");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[,,,1,drop=FALSE] ; x }", ", , 1, 1\n\n     [,1]\n[1,]   1L");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[,,drop=FALSE,,1] ; x }", ", , 1, 1\n\n     [,1]\n[1,]   1L");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[drop=FALSE,,,,1] ; x }", ", , 1, 1\n\n     [,1]\n[1,]   1L");
        assertEval("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[drop=FALSE,,,,-1] ; x }", ", , 1, 1\n\n     [,1]\n[1,]   2L\n\n, , 1, 2\n\n     [,1]\n[1,]   3L\n\n, , 1, 3\n\n     [,1]\n[1,]   4L");
        assertEvalError("{ b <- 1:4 ; dim(b) <- c(1,1,1,4); x <- b[drop=TRUE,,,,10] ; x }", "subscript out of bounds");
        assertEvalError("{ b <- 1:4 ; x <- b[drop=TRUE,,,,10] ; x }", "incorrect number of dimensions");
        assertEvalError("{ b <- 1:4 ; dim(b) <- c(4,1) ; b[drop=TRUE,,,,10]  }", "incorrect number of dimensions");
        assertEvalError("{ b <- function(){3}  ; b[drop=TRUE,,,,10]  }", "object of type 'closure' is not subsettable");
        assertEvalError("{ x <- 1:2 ; dim(x) <- c(2,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,1) ; f(x,f) }", "invalid subscript type 'closure'");
        assertEvalError("{ x <- list(1,2) ; dim(x) <- c(2,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,-2) }", "attempt to select more than one element");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,integer()) }", "attempt to select less than one element");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,4) }", "subscript out of bounds");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,FALSE) }", "attempt to select less than one element");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,NA) }", "subscript out of bounds");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,-4) }", "attempt to select more than one element");
        assertEvalError("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,NULL) }", "attempt to select less than one element");
        assertEval("{ x <- list(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,1L) ; f(x,2) } ", "[[1]]\n2.0");
        assertEval("{ x <- c(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,TRUE) }", "1.0");
        assertEvalError("{ x <- c(1,2,3) ; dim(x) <- c(3,1,1) ; f <- function(b,i) { b[[i,1,1]] } ; f(x,TRUE) ; f(x,4) }", "subscript out of bounds");
        assertEvalError("{ x <- c(1,2,3) ; dim(x) <- c(1,3,1) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,1:3,1:5) }", "subscript out of bounds");
        assertEval("{ x <- c(1,2,3) ; dim(x) <- c(1,3,1) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(-1,-4,-4,-6), 1) }", "2.0, 3.0");
        assertEval("{ x <- c(1,2,3) ; dim(x) <- c(1,3,1) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(-1,-4,-4,-6), 1) ; f(x,c(-2,-4,-4,-6), 1) }", "1.0, 3.0");
        assertEval("{ x <- c(1,2,3) ; dim(x) <- c(1,3,1) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(-1,-4,-4,-6), 1) ; y <- 1:8 ; dim(y) <- c(1,8,1) ; f(y,c(-2,-4,-4,-6,-8), 1) }", "1L, 3L, 5L, 7L");
        assertEvalError("{ x <- c(1,2,3) ; dim(x) <- c(1,3,1) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(-1,-4,-4, NA), 1) }", "only 0's may be mixed with negative subscripts");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(1,1),c(2,1)) }", "     [,1] [,2]\n[1,]  3.0  1.0\n[2,]  3.0  1.0");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(-1,-1),c(-3,-3,-3,-4)) }", "2.0, 4.0");
        assertEvalError("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(TRUE,TRUE,FALSE,NA),c(TRUE,TRUE)) }", "(subscript) logical subscript too long");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,TRUE,1) ; f(x,c(TRUE,FALSE),c(NA)) }", "NA, NA");
        assertEvalError("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[[1,i,j]] } ; f(x,TRUE,1) ; f(x,2,TRUE) ; f(x,TRUE,NULL) }", "attempt to select less than one element");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,c(1,1,1,1),c(2,1)) }", "     [,1] [,2]\n[1,]  3.0  1.0\n[2,]  3.0  1.0\n[3,]  3.0  1.0\n[4,]  3.0  1.0");
        assertEvalWarning("{ x <- c(1,2,3,4) ; dim(x) <- c(1,2,2) ; f <- function(b,i,j) { b[1,i,j] } ; f(x,c(1,1e100,2,2e100),c(2,1)) }", "     [,1] [,2]\n[1,]  3.0  1.0\n[2,]   NA   NA\n[3,]  4.0  2.0\n[4,]   NA   NA", "NAs introduced by coercion");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(2,1,2) ; f <- function(b,i,j) { b[j,1,i] } ; f(x,c(2,1,2,2),c(NA,2)) }", "     [,1] [,2] [,3] [,4]\n[1,]   NA   NA   NA   NA\n[2,]  4.0  2.0  4.0  4.0");
    }

    @Test
    public void testUpdate() throws RecognitionException {
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) \nm[[2]] <- list(100) \nm }", "       [,1] [,2]\n[1,]    1.0  4.0\n[2,] List,1  5.0\n[3,]    3.0  6.0");
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) \nm[2] <- list(100) \nm }", "      [,1] [,2]\n[1,]   1.0  4.0\n[2,] 100.0  5.0\n[3,]   3.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=3) \nm[2] <- list(100) \nm }", "[[1]]\n1L\n\n[[2]]\n100.0\n\n[[3]]\n3L\n\n[[4]]\n4L\n\n[[5]]\n5L\n\n[[6]]\n6L");

        // element deletion
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) \nm[c(2,3,4,6)] <- NULL \nm }", "[[1]]\n1.0\n\n[[2]]\n5.0");

        // proper update in place
        assertEval("{ m <- matrix(1,2,2)\nm[1,1] = 6\nm }", "     [,1] [,2]\n[1,]  6.0  1.0\n[2,]  1.0  1.0");
        assertEval("{ m <- matrix(1,2,2)\nm[,1] = 7\nm }", "     [,1] [,2]\n[1,]  7.0  1.0\n[2,]  7.0  1.0");
        assertEval("{ m <- matrix(1,2,2)\nm[1,] = 7\nm }", "     [,1] [,2]\n[1,]  7.0  7.0\n[2,]  1.0  1.0");
        assertEval("{ m <- matrix(1,2,2)\nm[,1] = c(10,11)\nm }", "     [,1] [,2]\n[1,] 10.0  1.0\n[2,] 11.0  1.0");

        // error in lengths
        assertEvalError("{ m <- matrix(1,2,2)\nm[,1] = c(1,2,3,4)\nm }", RError.NOT_MULTIPLE_REPLACEMENT);

        // column update
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,2] <- 10:11 ; m }", "     [,1] [,2] [,3]\n[1,]   1L  10L   5L\n[2,]   2L  11L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,2:3] <- 10:11 ; m }", "     [,1] [,2] [,3]\n[1,]   1L  10L  10L\n[2,]   2L  11L  11L");
        assertEval("{ m <- array(1:24, dim=c(2,3,4)) ; m[,,4] <- 10:15 ; m[,,4] }", "     [,1] [,2] [,3]\n[1,]  10L  12L  14L\n[2,]  11L  13L  15L");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[,integer()] <- integer() ; m }", "     [,1] [,2] [,3]\n[1,]   1L   3L   5L\n[2,]   2L   4L   6L");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[,2] <- integer() }", "replacement has length zero");

        // subscript with rewriting
        assertTrue("{  m <- array(1:3, dim=c(3,1,1)) ; f <- function(x,v) { x[[2,1,1]] <- v ; x } ; f(m,10L) ; f(m,10) ; x <- f(m,11L) ; x[1] == 1 && x[2] == 11 && x[3] == 3 }");

        // error reporting
        assertEvalError("{ a <- 1:9 ; a[,,1] <- 10L }", "incorrect number of subscripts");
        assertEvalError("{ a <- 1:9 ; a[,1] <- 10L }", "incorrect number of subscripts on a matrix");
        assertEvalError("{ a <- 1:9 ; a[1,1] <- 10L }", "incorrect number of subscripts on a matrix");
        assertEvalError("{ a <- 1:9 ; a[1,1,1] <- 10L }", "incorrect number of subscripts");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[[1:2,1]] <- 1 }", "attempt to select more than one element");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[[integer(),1]] <- 1 }", "attempt to select less than one element");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[[1,1]] <- integer() }", "replacement has length zero");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[[1:2,1]] <- integer() }", "replacement has length zero");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[1,2] <- integer() }", "replacement has length zero");
        assertEvalError("{ m <- matrix(1:6, nrow=2) ; m[1,2] <- 1:3 }", "number of items to replace is not a multiple of replacement length");

        // pushback child of a selector node
        assertEval("{ m <- matrix(1:100, nrow=10) ; z <- 1; s <- 0 ; for(i in 1:3) { m[z <- z + 1,z <- z + 1] <- z * z * 1000 } ; sum(m) }", "39918.0");

        // recovery from scalar selection update
        assertEval("{ m <- matrix(as.double(1:6), nrow=2) ; mi <- matrix(1:6, nrow=2) ; f <- function(v,i,j) { v[i,j] <- 100 ; v[i,j] * i * j } ; f(m, 1L, 2L) ; f(m,1L,TRUE)  }", "100.0, 100.0, 100.0");
        assertEval("{ m <- matrix(as.double(1:6), nrow=2) ; mi <- matrix(1:6, nrow=2) ; f <- function(v,i,j) { v[i,j] <- 100 ; v[i,j] * i * j } ; f(m, 1L, 2L) ; f(m,1L,-1)  }", "-100.0, -100.0");

        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] <- 10 ; m } ; m <- f(1,-1) ; m }", "     [,1] [,2] [,3]\n[1,]  1.0 10.0 10.0\n[2,]  2.0  4.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] <- 10 ; m } ; m <- f(1, c(-1,-10)) ; m }", "     [,1] [,2] [,3]\n[1,]  1.0 10.0 10.0\n[2,]  2.0  4.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] <- 10 ; m } ; m <- f(1,c(-1,-10)) ; m <- f(1,-1) ; m }", "     [,1] [,2] [,3]\n[1,]  1.0 10.0 10.0\n[2,]  2.0  4.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] <- 10 ; m } ; m <- f(1,c(-1,-10)) ; m <- f(-1,2) ; m }", "     [,1] [,2] [,3]\n[1,]  1.0 10.0 10.0\n[2,]  2.0 10.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=2) ; f <- function(i,j) { m[i,j] <- 10 ; m } ; m <- f(2,1:3) ; m <- f(1,-2) ; m }", "     [,1] [,2] [,3]\n[1,] 10.0  3.0 10.0\n[2,] 10.0 10.0 10.0");
        assertEval("{ x <- array(c(1,2,3), dim=c(3,1)) ; x[1:2,1] <- 2:1 ; x }", "     [,1]\n[1,]  2.0\n[2,]  1.0\n[3,]  3.0");

        // more tests
        assertEval("{ x <- c(1+2i,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,2] <- TRUE; x }", ", , 1\n\n         [,1]\n[1,] 1.0+2.0i\n[2,] 2.0+0.0i\n\n, , 2\n\n         [,1]\n[1,] 3.0+0.0i\n[2,] 1.0+0.0i");
        assertEval("{ x <- c(1+2i,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- 1:2; x }", ", , 1\n\n         [,1]\n[1,] 1.0+2.0i\n[2,] 1.0+0.0i\n\n, , 2\n\n         [,1]\n[1,] 3.0+0.0i\n[2,] 2.0+0.0i");
        assertEval("{ x <- c(1+2i,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- 10+2i; x }", ", , 1\n\n          [,1]\n[1,]  1.0+2.0i\n[2,] 10.0+2.0i\n\n, , 2\n\n          [,1]\n[1,]  3.0+0.0i\n[2,] 10.0+2.0i");
        assertEval("{ x <- c(1+2i,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(12,10); x }", ", , 1\n\n          [,1]\n[1,]  1.0+2.0i\n[2,] 12.0+0.0i\n\n, , 2\n\n          [,1]\n[1,]  3.0+0.0i\n[2,] 10.0+0.0i");
        assertEval("{ x <- c(1+2i,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- \"hello\"; x }", ", , 1\n\n           [,1]\n[1,] \"1.0+2.0i\"\n[2,]    \"hello\"\n\n, , 2\n\n           [,1]\n[1,] \"3.0+0.0i\"\n[2,]    \"hello\"");
        assertEval("{ x <- c(TRUE,FALSE,NA,FALSE) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- FALSE; x }", ", , 1\n\n      [,1]\n[1,]  TRUE\n[2,] FALSE\n\n, , 2\n\n      [,1]\n[1,]    NA\n[2,] FALSE");
        assertEval("{ x <- c(1L,3L,5L,4L) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(10L,11L) ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]  10L\n\n, , 2\n\n     [,1]\n[1,]   5L\n[2,]  11L");
        assertEval("{ x <- c(1+2i,3+4i,5+6i,4+5i) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(10+11i,12+13i) ; x }", ", , 1\n\n           [,1]\n[1,]   1.0+2.0i\n[2,] 10.0+11.0i\n\n, , 2\n\n           [,1]\n[1,]   5.0+6.0i\n[2,] 12.0+13.0i");
        assertEvalError("{ x <- c(1+2i,3+4i,5+6i,4+5i) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- as.raw(1:2) ; x }", "incompatible types (from raw to complex) in subassignment type fix");
        assertEvalError("{ x <- function(){3} ; x[2,1,2] <- x }", "object of type 'closure' is not subsettable");
        assertEvalError("{ x <- c(1+2i,3+4i,5+6i,4+5i) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- sum ; x }", "number of items to replace is not a multiple of replacement length");
        assertEvalError("{ x <- as.raw(1:4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- 3:4 ; x }", "incompatible types (from integer to raw) in subassignment type fix");
        assertEval("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- as.raw(13:14) ; x }", ", , 1\n\n     [,1]\n[1,]   0b\n[2,]   0d\n\n, , 2\n\n     [,1]\n[1,]   0d\n[2,]   0e");
        assertEval("{ x <- list(1+2i,3+4i,5+6i,4+5i) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- as.raw(c(1,2)) ; unlist(x) }", "1.0+2.0i, 1.0+0.0i, 5.0+6.0i, 2.0+0.0i");
        assertEval("{ for (i in 1:3) { x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; if (i==2) { z <- x } ; x[2,1,1:2] <- as.raw(13) ; r <- x } ; z }", ", , 1\n\n     [,1]\n[1,]   0b\n[2,]   0c\n\n, , 2\n\n     [,1]\n[1,]   0d\n[2,]   0e");
        assertEvalError("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; x[2,1,c(NA,2)] <- as.raw(13:14) ; x }", "NAs are not allowed in subscripted assignments");
        assertEvalError("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; x[2,1,c(1,NA)] <- as.raw(13:14) ; x }", "NAs are not allowed in subscripted assignments");
        assertEvalError("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; ina <- c(1,2) ; inb <- c(1,NA) ; x[ina, 1, inb] <- as.raw(13:14) ; x }", "NAs are not allowed in subscripted assignments");
        assertEval("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; ina <- c(TRUE,FALSE) ; inb <- c(FALSE,TRUE) ; x[ina, 1, inb] <- as.raw(13) ; x }", ", , 1\n\n     [,1]\n[1,]   0b\n[2,]   0c\n\n, , 2\n\n     [,1]\n[1,]   0d\n[2,]   0e");
        assertEval("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; ina <- c(TRUE,FALSE) ; inb <- logical() ; x[ina, 1, inb] <- as.raw(13) ; x }", ", , 1\n\n     [,1]\n[1,]   0b\n[2,]   0c\n\n, , 2\n\n     [,1]\n[1,]   0d\n[2,]   0e");
        assertEvalError("{ f <- function(b, v) { b[2,1,1:2] <- v ; b } ; x <- 1:4 ; dim(x) <- c(2,1,2) ; f(x, c(10L,11L)) ; f(x, c(TRUE,FALSE)) ; f(f,f) }", "object of type 'closure' is not subsettable");
        assertEvalError("{ f <- function(b, v) { b[2,1,1:2] <- v ; b } ; x <- 1:4 ; dim(x) <- c(2,1,2) ; f(x, c(10L,11L)) ; f(x, c(TRUE,FALSE)) ; f(x,f) }", "number of items to replace is not a multiple of replacement length");
        assertEvalError("{ f <- function(b, v) { b[2,1,1:2] <- v ; b } ; x <- 1:4 ; dim(x) <- c(2,1,2) ; f(x, c(10L,11L)) ; f(x, c(TRUE,FALSE)) ; f(1:10,f) }", "incorrect number of subscripts");
        assertEval("{ x <- NULL ; x[2,1] <- NULL ; x }", "NULL");
        assertEvalError("{ x <- matrix(2,nrow=2,ncol=2) ; x[2,1] <- NULL ; x }", "number of items to replace is not a multiple of replacement length");
        assertEvalError("{ x <- matrix(2,nrow=2,ncol=2) ; x[[2,1]] <- NULL ; x }", "more elements supplied than there are to replace");

        assertEval("{ x <- 1:4 ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- 11:12 ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]  11L\n\n, , 2\n\n     [,1]\n[1,]   3L\n[2,]  12L");
        assertEval("{ x <- c(1,2,3,4) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(11,12) ; x }", ", , 1\n\n     [,1]\n[1,]  1.0\n[2,] 11.0\n\n, , 2\n\n     [,1]\n[1,]  3.0\n[2,] 12.0");
        assertEval("{ x <- c(1+1i,2+2i,3+3i,4+4i) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(11+1i,12+2i) ; x }", ", , 1\n\n          [,1]\n[1,]  1.0+1.0i\n[2,] 11.0+1.0i\n\n, , 2\n\n          [,1]\n[1,]  3.0+3.0i\n[2,] 12.0+2.0i");
        assertEval("{ x <- c(TRUE,FALSE,NA,TRUE) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(FALSE,NA) ; x }", ", , 1\n\n      [,1]\n[1,]  TRUE\n[2,] FALSE\n\n, , 2\n\n     [,1]\n[1,]   NA\n[2,]   NA");
        assertEval("{ x <- c(\"a\",\"A\",\"XX\",\"Y\") ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(\"b\",\"BB\") ; x }", ", , 1\n\n     [,1]\n[1,]  \"a\"\n[2,]  \"b\"\n\n, , 2\n\n     [,1]\n[1,] \"XX\"\n[2,] \"BB\"");
        assertEval("{ x <- list(\"a\",1,TRUE,NULL) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- list(FALSE,NULL) ; x }", ", , 1\n\n      [,1]\n[1,]   \"a\"\n[2,] FALSE\n\n, , 2\n\n     [,1]\n[1,] TRUE\n[2,] NULL");
        assertEval("{ x <- list(\"a\",1,TRUE,NULL) ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(FALSE,TRUE) ; x }", ", , 1\n\n      [,1]\n[1,]   \"a\"\n[2,] FALSE\n\n, , 2\n\n     [,1]\n[1,] TRUE\n[2,] TRUE");
        assertEval("{ x <- as.raw(11:14) ; dim(x) <- c(2,1,2) ; x[2,1,2] <- as.raw(21)[1:1] ; x }", ", , 1\n\n     [,1]\n[1,]   0b\n[2,]   0c\n\n, , 2\n\n     [,1]\n[1,]   0d\n[2,]   15");
        assertEval("{ x <- 1:4 ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(FALSE,TRUE) ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]   0L\n\n, , 2\n\n     [,1]\n[1,]   3L\n[2,]   1L");

        assertEval("{ for (i in 1:3 ) { x <- 1:4 ; if (i>=2) { x <- c(10,1,3,4) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(14*i,12+i)  } ; x }", ", , 1\n\n     [,1]\n[1,] 10.0\n[2,] 42.0\n\n, , 2\n\n     [,1]\n[1,]  3.0\n[2,] 15.0");
        assertEval("{ for (i in 1:3 ) { x <- c(10,11,13,14) ; if (i>=2) { x <- c(1L,2L,10L,100L) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(14L*i,12L+i)  } ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]  42L\n\n, , 2\n\n     [,1]\n[1,]  10L\n[2,]  15L");
        assertEval("{ for (i in 1:3 ) { x <- c(11:14) ; if (i>=2) { x <- c(1,2,10,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(14L*i,12L+i)  } ; x }", ", , 1\n\n     [,1]\n[1,]  1.0\n[2,] 42.0\n\n, , 2\n\n     [,1]\n[1,] 10.0\n[2,] 15.0");
        assertEval("{ for (i in 1:3 ) { x <- c(11:14) ; if (i>=2) { x <- c(1,2,10+1i,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(14L*i,12L+i)  } ; x }", ", , 1\n\n          [,1]\n[1,]  1.0+0.0i\n[2,] 42.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 10.0+1.0i\n[2,] 15.0+0.0i");
        assertEval("{ for (i in 1:3 ) { x <- c(11:14) ; if (i>=2) { x <- c(1,2,10+1i,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(15*i,-12+i)  } ; x }", ", , 1\n\n          [,1]\n[1,]  1.0+0.0i\n[2,] 45.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 10.0+1.0i\n[2,] -9.0+0.0i");
        assertEval("{ for (i in 1:3 ) { x <- c(11:14) ; if (i>=2) { x <- c(1,2,10+1i,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(TRUE,NA)  } ; x }", ", , 1\n\n         [,1]\n[1,] 1.0+0.0i\n[2,] 1.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 10.0+1.0i\n[2,]        NA");
        assertEval("{ r <- 0 ; for (i in 1:5 ) { x <- c(11:14) ; if (i==2 || i==3) { x <- c(1,2,10+1i,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(15L*i,-12L+i) ; r <- r + sum(x) } ; r }", "274.0+2.0i");
        assertEval("{ r <- 0 ; for (i in 1:5 ) { x <- c(11:14) ; if (i==2 || i==3) { x <- c(1,2,10+1i,100) } ; dim(x) <- c(2,1,2) ; x[2,1,1:2] <- c(15L*i,NA) } ; x }", ", , 1\n\n     [,1]\n[1,]  11L\n[2,]  75L\n\n, , 2\n\n     [,1]\n[1,]  13L\n[2,]   NA");
        assertEval("{ x <- 1:4 ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- x ; x }", ", , 1\n\n     [,1]\n[1,]   4L\n[2,]   3L\n\n, , 2\n\n     [,1]\n[1,]   2L\n[2,]   1L");
        assertEval("{ y <- as.double(1:4) ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- c(1,2,3,4) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n     [,1]\n[1,]  4.0\n[2,]  3.0\n\n, , 2\n\n     [,1]\n[1,]  2.0\n[2,]  1.0");
        assertEval("{ y <- as.double(1:4) ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- c(1+2i,2,3,4) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n         [,1]\n[1,] 4.0+0.0i\n[2,] 3.0+0.0i\n\n, , 2\n\n         [,1]\n[1,] 2.0+0.0i\n[2,] 1.0+2.0i");
        assertEval("{ y <- 1:4 ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- c(1+2i,2,3,4) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n         [,1]\n[1,] 4.0+0.0i\n[2,] 3.0+0.0i\n\n, , 2\n\n         [,1]\n[1,] 2.0+0.0i\n[2,] 1.0+2.0i");
        assertEval("{ y <- 1:4 ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- c(1,2,3,4) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n     [,1]\n[1,]  4.0\n[2,]  3.0\n\n, , 2\n\n     [,1]\n[1,]  2.0\n[2,]  1.0");
        assertEval("{ y <- 1:4 ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- as.double(1:4) ; if (i>=2) { x <- 11:14 } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n     [,1]\n[1,]  14L\n[2,]  13L\n\n, , 2\n\n     [,1]\n[1,]  12L\n[2,]  11L");
        assertEval("{ y <- 1:4+1+2i ; dim(y) <- c(2,1,2) ; for(i in 1:6) { x <- as.double(1:4) ; if (i>=2) { x <- c(1+2i,3+4i,5,NA) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { x } else { y }  } ; x }", ", , 1\n\n         [,1]\n[1,]       NA\n[2,] 5.0+0.0i\n\n, , 2\n\n         [,1]\n[1,] 3.0+4.0i\n[2,] 1.0+2.0i");
        assertEval("{ for(i in 1:6) { x <- as.double(1:4) ; if (i>=2) { x <- 1:4 } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { 11:14+1+2i } else { c(1L,10L,NA,3L) }  } ; x }", ", , 1\n\n          [,1]\n[1,] 15.0+2.0i\n[2,] 14.0+2.0i\n\n, , 2\n\n          [,1]\n[1,] 13.0+2.0i\n[2,] 12.0+2.0i");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- as.double(1:4) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { 11:14+1+2i } else { c(1L,10L,NA,3L) }  } ; x }", ", , 1\n\n          [,1]\n[1,] 15.0+2.0i\n[2,] 14.0+2.0i\n\n, , 2\n\n          [,1]\n[1,] 13.0+2.0i\n[2,] 12.0+2.0i");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- (1:4)+1+2i } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { 11:14 } else { c(1L,10L,NA,3L)+2+3i }  } ; x }", ", , 1\n\n          [,1]\n[1,] 14.0+0.0i\n[2,] 13.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 12.0+0.0i\n[2,] 11.0+0.0i");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- (1:4)+1+2i } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { 11:14 } else { c(1,10,NA,3) }  } ; x }", ", , 1\n\n          [,1]\n[1,] 14.0+0.0i\n[2,] 13.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 12.0+0.0i\n[2,] 11.0+0.0i");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- (1:4)+1 } ; dim(x) <- c(2,1,2) ; x[2:1,1,2:1] <- if (i >= 4) { 11:14 } else { c(1,10,NA,3) }  } ; x }", ", , 1\n\n     [,1]\n[1,] 14.0\n[2,] 13.0\n\n, , 2\n\n     [,1]\n[1,] 12.0\n[2,] 11.0");
        assertEval("{ for(i in 1:6) { x <- 1:4+1 ; if (i>=2) { x <- 1:4 } ; if (i>=4) { x <- c(TRUE,FALSE,FALSE,NA) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2] <- c(11L,12L)  } ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]   0L\n\n, , 2\n\n     [,1]\n[1,]  12L\n[2,]  11L");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- 1:4+1 } ; if (i>=4) { x <- c(TRUE,FALSE,FALSE,NA) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2] <- c(11L,12L)  } ; x }", ", , 1\n\n     [,1]\n[1,]   1L\n[2,]   0L\n\n, , 2\n\n     [,1]\n[1,]  12L\n[2,]  11L");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- 1:4+1+1i } ; if (i>=4) { x <- c(TRUE,FALSE,FALSE,NA) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2] <- c(11,12)  } ; x }", ", , 1\n\n     [,1]\n[1,]  1.0\n[2,]  0.0\n\n, , 2\n\n     [,1]\n[1,] 12.0\n[2,] 11.0");
        assertEval("{ for(i in 1:6) { x <- 1:4 ; if (i>=2) { x <- 1:4+1+1i } ; if (i>=4) { x <- c(TRUE,FALSE,FALSE,NA) } ; dim(x) <- c(2,1,2) ; x[2:1,1,2] <- c(11+1i,12+2i)  } ; x }", ", , 1\n\n         [,1]\n[1,] 1.0+0.0i\n[2,] 0.0+0.0i\n\n, , 2\n\n          [,1]\n[1,] 12.0+2.0i\n[2,] 11.0+1.0i");
    }

    @Test
    public void testDynamic() throws RecognitionException {
        assertEval("{ l <- quote(x[1,1] <- 10) ; f <- function() { eval(l) } ; x <- matrix(1:4,nrow=2) ; f() ; x }", "     [,1] [,2]\n[1,]   1L   3L\n[2,]   2L   4L");
    }
}
