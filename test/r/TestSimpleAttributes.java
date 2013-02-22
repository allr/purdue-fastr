package r;

import org.antlr.runtime.RecognitionException;
import org.junit.*;

// NOTE: some tests relating to attributes are also in TestSimpleBuiltins.testAttributes
public class TestSimpleAttributes extends TestBase {

    @Test
    public void testDefinition() throws RecognitionException {
        assertEval("{ x <- as.raw(10) ; attr(x, \"hi\") <- 2 ;  x }", "0a\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- TRUE ; attr(x, \"hi\") <- 2 ;  x }", "TRUE\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1L ; attr(x, \"hi\") <- 2 ;  x }", "1L\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1 ; attr(x, \"hi\") <- 2 ;  x }", "1.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1+1i ; attr(x, \"hi\") <- 2 ;  x }", "1.0+1.0i\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- \"s\" ; attr(x, \"hi\") <- 2 ;  x }", "\"s\"\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1L, 2L) ; attr(x, \"hi\") <- 2; x }", "1L, 2L\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1, 2) ; attr(x, \"hi\") <- 2; x }", "1.0, 2.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- c(1L, 2L) ; attr(x, \"hi\") <- 2; attr(x, \"hello\") <- 1:2 ;  x }", "1L, 2L\nattr(,\"hi\")\n2.0\nattr(,\"hello\")\n1L, 2L");

        assertEval("{ x <- c(hello=9) ; attr(x, \"hi\") <- 2 ;  y <- x ; y }", "hello\n  9.0\nattr(,\"hi\")\n2.0");

        assertEval("{ x <- c(hello=1) ; attr(x, \"hi\") <- 2 ;  attr(x,\"names\") <- \"HELLO\" ; x }", "HELLO\n  1.0\nattr(,\"hi\")\n2.0");
    }

    @Test
    public void testArithmeticPropagation() throws RecognitionException {
        assertEval("{ x <- 1:2;  attr(x, \"hi\") <- 2 ;  x+1:4 }", "2L, 4L, 4L, 6L");
        assertEval("{ x <- c(1+1i,2+2i);  attr(x, \"hi\") <- 3 ; y <- 2:3 ; attr(y,\"zz\") <- 2; x+y }", "3.0+1.0i, 5.0+2.0i\nattr(,\"zz\")\n2.0\nattr(,\"hi\")\n3.0");
        assertEval("{ x <- 1+1i;  attr(x, \"hi\") <- 1+2 ; y <- 2:3 ; attr(y,\"zz\") <- 2; x+y }", "3.0+1.0i, 4.0+1.0i\nattr(,\"zz\")\n2.0");
        assertEval("{ x <- c(1+1i, 2+2i) ;  attr(x, \"hi\") <- 3 ; attr(x, \"hihi\") <- 10 ; y <- c(2+2i, 3+3i) ; attr(y,\"zz\") <- 2; attr(y,\"hi\") <-3; attr(y,\"bye\") <- 4 ; x+y }", "3.0+3.0i, 5.0+5.0i\nattr(,\"zz\")\n2.0\nattr(,\"hi\")\n3.0\nattr(,\"bye\")\n4.0\nattr(,\"hihi\")\n10.0");
        assertEval("{ x <- 1+1i;  attr(x, \"hi\") <- 1+2 ; y <- 2:3 ;  x+y }", "3.0+1.0i, 4.0+1.0i");

        assertEval("{ x <- 1:2;  attr(x, \"hi\") <- 2 ;  x+1 }", "2.0, 3.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1:2;  attr(x, \"hi\") <- 2 ; y <- 2:3 ; attr(y,\"hello\") <- 3; x+y }", "3L, 5L\nattr(,\"hello\")\n3.0\nattr(,\"hi\")\n2.0");
        assertEval("{ x <- 1;  attr(x, \"hi\") <- 1+2 ; y <- 2:3 ; attr(y, \"zz\") <- 2; x+y }", "3.0, 4.0\nattr(,\"zz\")\n2.0");
        assertEval("{ x <- 1:2 ;  attr(x, \"hi\") <- 3 ; attr(x, \"hihi\") <- 10 ; y <- 2:3 ; attr(y,\"zz\") <- 2; attr(y,\"hi\") <-3; attr(y,\"bye\") <- 4 ; x+y }", "3L, 5L\nattr(,\"zz\")\n2.0\nattr(,\"hi\")\n3.0\nattr(,\"bye\")\n4.0\nattr(,\"hihi\")\n10.0");
    }

    @Test
    public void testBuiltinPropagation() throws RecognitionException {
        assertEval("{ x <- c(hello=1, hi=9) ; attr(x, \"hi\") <- 2 ;  sqrt(x) }", "hello  hi\n  1.0 3.0\nattr(,\"hi\")\n2.0");
    }

}
