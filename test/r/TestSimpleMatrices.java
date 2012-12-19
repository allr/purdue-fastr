package r;

import org.antlr.runtime.*;
import org.junit.*;


public class TestSimpleMatrices extends TestBase {
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
    }
}
