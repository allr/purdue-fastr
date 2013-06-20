package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class TestSimpleGraalModifications extends SimpleTestBase {
    @Test
    public void testFunctionWithScalarDoubleArg() throws RecognitionException {
        assertEval("f <- function(a) { a = a + 1; a; }; f(1);","2.0");

    }
}
