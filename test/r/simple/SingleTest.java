package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class SingleTest extends SimpleTestBase {
    @Test
    public void testScalars() throws RecognitionException {
        assertEval("f = function(a,b) {\n" +
                "  for (i in 1:10) {\n" +
                "    a = a + b\n" +
                "  }\n" +
                "}\n" +
                "a = as.vector(array(0,c(10)))\n" +
                "b = c(1,2,3,4,5,6,7,8,9,10)\n" +
                "f(a,b)\n","NULL");
    }


}
