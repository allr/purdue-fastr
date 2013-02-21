package r;

import org.antlr.runtime.RecognitionException;
import org.junit.*;

public class TestSimpleAttributes extends TestBase {

    @Test
    public void testDefinition() throws RecognitionException {
        assertEval("{ x <- 1 ; attr(x, \"hi\") <- 2 ;  x }", "1.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1+1i ; attr(x, \"hi\") <- 2 ;  x }", "1.0+1.0i\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- \"s\" ; attr(x, \"hi\") <- 2 ;  x }", "\"s\"\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1L, 2L) ; attr(x, \"hi\") <- 2; x }", "1L, 2L\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1, 2) ; attr(x, \"hi\") <- 2; x }", "1.0, 2.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1L, 2L) ; attr(x, \"hi\") <- 2; attr(x, \"hello\") <- 1:2 ;  x }", "1L, 2L\nattr(,\"hi\")\n2.0\nattr(,\"hello\")\n1L, 2L");

    }
}
