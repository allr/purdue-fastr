package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class TestSimpleGraalModifications extends SimpleTestBase {
    @Test
    public void testFunctionWithScalarDoubleArg() throws RecognitionException {
        assertEval("f <- function() { a = 1; aa = 2; aaa = 3; aaaa = 4; aaaa; }; f();","4.0");

    }
}
