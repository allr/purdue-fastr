package r;

import org.antlr.runtime.*;
import org.junit.*;

import com.oracle.truffle.compiler.*;

import r.data.*;
import r.nodes.*;
import r.nodes.tools.*;

public class TestBase {

    static Truffleize truffleize = new Truffleize();
    static RContext global = new RContext(1);

    static String evalString(String input) throws RecognitionException {
        return eval(input).pretty();
    }

    static void assertEval(String input, String expected) throws RecognitionException {
        Assert.assertEquals(expected, evalString(input));
    }

    static RAny eval(String input) throws RecognitionException {
        ASTNode astNode = TestPP.parse(input);
        return global.eval(astNode);
    }
}
