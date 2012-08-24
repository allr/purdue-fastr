package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleAssignment extends TestBase {
    @Test
    public void testAssign() throws RecognitionException {
        assertEval("a<-1", "1.0");
        assertEval("a<-FALSE ; b<-a", "FALSE");
        assertEval("x = if (FALSE) 1", "NULL");
    }
}

