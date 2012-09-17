package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleVectors extends TestBase {

    @Test
    public void testSubscript() throws RecognitionException {
        assertEval("{ x<-1:10; x[3] }", "3L");
        assertEval("{ x<-1:10; x[3L] }", "3L");
        assertEval("{ x<-c(1,2,3); x[3] }", "3.0");
        assertEval("{ x<-c(1,2,3); x[3L] }", "3.0");
    }
}
