package r;

import org.antlr.runtime.*;
import org.junit.*;


public class TestSimpleMatrices extends TestBase {
    @Test
    public void testDefinitions() throws RecognitionException {
        assertEval("{ m <- matrix(1:6, nrow=2, ncol=3, byrow=TRUE) ; m }", "     [,1] [,2] [,3]\n[1,]   1L   2L   3L\n[2,]   4L   5L   6L");
    }
}
