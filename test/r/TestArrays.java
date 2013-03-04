package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.errors.RError;


public class TestArrays extends TestBase {

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

        // dimensions of 1 are not dropped when requested
        assertTrue("{ a = array(1,c(3,3,3)); a = dim(a[1,1,1, drop = FALSE]); length(a) == 3 && a[1] == 1 && a[2] == 1 && a[3] == 1; }");

        // fallback to one dimensional read
        assertTrue("{ a = array(1:27, c(3,3,3)); a[1] == 1 && a[27] == 27 && a[22] == 22 && a[6] == 6; }");

        // error when different dimensions given
        assertEvalError("{ a = array(1,c(3,3,3)); a[2,2]; }", RError.INCORRECT_DIMENSIONS);

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
    public void testBugIfiniteLoopInGeneralizedReqriting() {
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
    }

    @Test
    public void testSelection() throws RecognitionException {
        assertEval("{ m <- matrix(c(1,2,3,4,5,6), nrow=3) \nm[0] }", "numeric(0)");
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) \nm[0] }", "list()");
        assertEval("{ m <- matrix(1:6, nrow=2) \nm[upper.tri(m)] }", "3L, 5L, 6L");
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

    }
}
