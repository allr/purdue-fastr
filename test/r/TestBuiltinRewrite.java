package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class TestBuiltinRewrite extends TestBase {

    /** Makes sure that builtin rewrite in one test example does not affect other test instances. */
    @Test
    public void testBuiltinRewrite() throws RecognitionException {
        assertEval("sub <- function(x,y) x-y; sub(10,5)", "5.0");
        // to make sure that the objects are deleted and the symbol table is reinitialized
        assertEval("{ sub(\"a\",\"aa\", \"prague alley\", fixed=TRUE) }", "\"praague alley\"");
    }
}
