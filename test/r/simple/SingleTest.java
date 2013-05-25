package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class SingleTest extends SimpleTestBase {
    @Test
    public void testScalars() throws RecognitionException {
        testEval("s = as.vector(array(1,c(10)))\n" +
                "inc = as.vector(array(1,c(10)))\n" +
                "\n" +
                "\n" +
                "f2 = function(b) {\n" +
                "  b = b + s\n" +
                "  s <<- s + inc\n" +
                "  b\n" +
                "}\n" +
                "\n" +
                "f = function(a) {\n" +
                "  x = _timerStart();\n" +
                "  for (i in 1:10) {\n" +
                "    a = a + f2(a)\n" +
                "  }\n" +
                "  s <<- a\n" +
                "  x\n" +
                "}\n" +
                "a = as.vector(array(0,c(10)))\n" +
                "f(a)\n" +
                "a = as.vector(array(0,c(10)))\n" +
                "s = as.vector(array(1,c(10)))\n" +
                "_timerEnd(f(a),\"tmr\")\n");
    }


}
