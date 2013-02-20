package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.errors.RError;


public class TestArrays extends TestBase {

    @Test
    public void testArrayBuiltin() throws RecognitionException {
        assertTrue("# array with no arguments produces array of length 1\n" +
                "a = array()\n" +
                "length(a) == 1\n");

        assertTrue("# empty arg has first element NA\n" +
                "a = array()\n" +
                "is.na(a[1])\n");

// dimnames not implemented yet
//        assertTrue("# dimension names of empty array are null\n" +
//                "a = array()\n" +
//                "is.null(dimnames(a))\n");

        assertTrue("# empty array has single dimension that is 1\n" +
                "a <- array()\n" +
                "dim(a) == 1\n");

        assertTrue("# wrapping in arrays work even when prohibited by help\n" +
                "a = array(1:10, dim = c(2,6))\n" +
                "length(a) == 12\n");

        // negative length vectors are not allowed is the error reported by gnu-r
        assertEvalError("# negative dims not allowed by R, special GNU message\n" +
                "array(dim=c(-2,2))\n",RError.DIMS_CONTAIN_NEGATIVE_VALUES);

        assertEvalError("# negative dims not allowed\n" +
                "array(dim=c(-2,-2))\n",RError.DIMS_CONTAIN_NEGATIVE_VALUES);

        assertTrue("# zero dimension array has length 0\n" +
                "length(array(dim=c(1,0,2,3))) == 0\n");

        assertTrue("# double dimensions work and are rounded down always\n" +
                "a = dim(array(dim=c(2.1,2.9,3.1,4.7)))\n" +
                "a[1] == 2 && a[2] == 2 && a[3] == 3 && a[4] == 4\n");
    }

    @Test
    public void testMatrixBuiltin() throws RecognitionException {
        assertTrue("# empty matrix length is 1\n" +
                "length(matrix()) == 1\n");
    }


    @Test
    public void testArraySimpleRead() throws RecognitionException {
        assertTrue("# simple read;\n" +
                "a = array(1:27,c(3,3,3));\n" +
                "a[1,1,1] == 1 && a[3,3,3] == 27 && a[1,2,3] == 22 && a[3,2,1] == 6");

        assertTrue("# empty selectors reads the whole array\n" +
                "a = array(1:27, c(3,3,3))\n" +
                "b = a[,,]\n" +
                "d = dim(b)\n" +
                "d[1] == 3 && d[2] == 3 && d[3] == 3\n");

        assertTrue("# dimensions of 1 are dropped\n" +
                "a = array(1,c(3,3,3))\n" +
                "a = dim(a[,1,])\n" +
                "length(a) == 2 && a[1] == 3 && a[2] == 3\n");

        assertTrue("# when all dimensions are dropped, dim is null\n" +
                "a = array(1,c(3,3,3))\n" +
                "is.null(dim(a[1,1,1]))");

        assertTrue("# last dimension is dropped\n" +
                "a = array(1,c(3,3,3))\n" +
                "is.null(dim(a[1,1,]))\n");

        assertTrue("# dimensions of 1 are not dropped when requested\n" +
                "a = array(1,c(3,3,3))\n" +
                "a = dim(a[1,1,1, drop = FALSE])\n" +
                "length(a) == 3 && a[1] == 1 && a[2] == 1 && a[3] == 1\n");



        assertTrue("# fallback to one dimensional read\n" +
                "a = array(1:27, c(3,3,3))\n" +
                "a[1] == 1 && a[27] == 27 && a[22] == 22 && a[6] == 6");

        assertEvalError("# error when different dimensions given\n" +
                "a = array(1,c(3,3,3))\n" +
                "a[2,2]\n",RError.INCORRECT_DIMENSIONS);

    }

    @Test
    public void testArraySubsetAndSelection() throws RecognitionException {
        assertTrue("#subset operator works for arrays\n" +
                "array(1,c(3,3,3))[1,1,1] == 1");

        assertTrue("#selection operator works for arrays\n" +
                "array(1,c(3,3,3))[[1,1,1]] == 1");

        assertEvalError("#selection on multiple elements fails in arrays\n" +
                "array(1,c(3,3,3))[[,,]]",String.format(RError.INVALID_SUBSCRIPT_TYPE,"symbol"));

        assertEvalError("#selection on multiple elements fails in arrays\n" +
                "array(1,c(3,3,3))[[c(1,2),1,1]]", RError.SELECT_MORE_1);
    }

    @Test
    public void testMatrixSubsetAndSelection() throws RecognitionException {
        assertTrue("#subset operator works for matrices\n" +
                "matrix(1,3,3)[1,1] == 1");

        assertTrue("#selection operator works for arrays\n" +
                "matrix(1,3,3)[[1,1]] == 1");

        assertEvalError("#selection on multiple elements fails in matrices with empty selector\n" +
                "matrix(1,3,3)[[,]]",String.format(RError.INVALID_SUBSCRIPT_TYPE,"symbol"));

        assertEvalError("#selection on multiple elements fails in matrices\n" +
                "matrix(1,3,3)[[c(1,2),1]]", RError.SELECT_MORE_1);
    }

    @Test
    public void testArrayComplexSelectors() {

    }

    @Test
    public void testMatrixSimpleRead() {
        assertTrue("# last dimension is dropped;\n" +
                "a = matrix(1,3,3);\n" +
                "is.null(dim(a[1,]));\n");

    }


    @Test
    public void testDefinitions() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix(1:6, ncol=3, byrow=TRUE) ; m }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix(1:6, nrow=2, byrow=TRUE) ; m }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
        assertEval("{ m <- matrix() ; m }", "     [,1]\n[1,]   NA");
        assertEval("{ matrix( (1:6) * (1+3i), nrow=2 ) }", "         [,1]      [,2]      [,3]\n[1,] 1.0+3.0i  3.0+9.0i 5.0+15.0i\n[2,] 2.0+6.0i 4.0+12.0i 6.0+18.0i");
        assertEval("{ matrix( as.raw(101:106), nrow=2 ) }", "     [,1] [,2] [,3]\n[1,]   65   67   69\n[2,]   66   68   6a");
    }

    @Test
    public void testSelection() throws RecognitionException {
        assertEval("{ m <- matrix(c(1,2,3,4,5,6), nrow=3) ; m[0] }", "numeric(0)");
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) ; m[0] }", "list()");
        assertEval("{ m <- matrix(1:6, nrow=2) ; m[upper.tri(m)] }", "3L, 5L, 6L");
    }

    @Test
    public void testUpdate() throws RecognitionException {
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) ; m[[2]] <- list(100) ; m }", "       [,1] [,2]\n[1,]    1.0  4.0\n[2,] List,1  5.0\n[3,]    3.0  6.0");
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) ; m[2] <- list(100) ; m }", "      [,1] [,2]\n[1,]   1.0  4.0\n[2,] 100.0  5.0\n[3,]   3.0  6.0");
        assertEval("{ m <- matrix(1:6, nrow=3) ; m[2] <- list(100) ; m }", "[[1]]\n1L\n\n[[2]]\n100.0\n\n[[3]]\n3L\n\n[[4]]\n4L\n\n[[5]]\n5L\n\n[[6]]\n6L");

        // element deletion
        assertEval("{ m <- matrix(list(1,2,3,4,5,6), nrow=3) ; m[c(2,3,4,6)] <- NULL ; m }", "[[1]]\n1.0\n\n[[2]]\n5.0");

        // proper update in place
        assertEval("{ m <- matrix(1,2,2); m[1,1] = 6; m }", "     [,1] [,2]\n[1,]  6.0  1.0\n[2,]  1.0  1.0");
        assertEval("{ m <- matrix(1,2,2); m[,1] = 7; m }", "     [,1] [,2]\n[1,]  7.0  1.0\n[2,]  7.0  1.0");
        assertEval("{ m <- matrix(1,2,2); m[1,] = 7; m }", "     [,1] [,2]\n[1,]  7.0  7.0\n[2,]  1.0  1.0");
        assertEval("{ m <- matrix(1,2,2); m[,1] = c(10,11); m }", "     [,1] [,2]\n[1,] 10.0  1.0\n[2,] 11.0  1.0");
        // error in lengths
        assertEvalError("{ m <- matrix(1,2,2); m[,1] = c(1,2,3,4); m }", RError.NOT_MULTIPLE_REPLACEMENT);

    }
}
