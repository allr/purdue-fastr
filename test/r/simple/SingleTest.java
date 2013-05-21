package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class SingleTest extends SimpleTestBase {
    @Test
    public void testScalars() throws RecognitionException {
        testEval("f = function(a,b) {\n" +
                "  x = _timerStart()\n" +
                "  for (i in 1:100000000) {\n" +
                "    a = a + b\n" +
                "  }\n" +
                "  x\n" +
                "}\n" +
                "a = as.vector(array(0,c(10)))\n" +
                "b = as.vector(array(2,c(10)))\n" +
                "f(a,b)\n" +
                "_timerEnd(f(a,b),\"tmr\")\n" +
                " ");
    }


}
