package r;

import org.antlr.runtime.*;
import org.junit.*;

import r.data.*;
import r.nodes.*;
import r.nodes.tools.*;

public class TestSimpleIfEvaluator {

    static Truffleize truffleize = new Truffleize();
    static RContext global = new RContext();

    static String evalString(String input) throws RecognitionException {
        return eval(input).pretty();
    }

    static void assertEval(String input, String expected) throws RecognitionException {
        Assert.assertEquals(evalString(input), expected);
    }

    static RAny eval(String input) throws RecognitionException {
        ASTNode astNode = TestPP.parse(input);
        return global.eval(astNode);
    }

//    @Test
//    public void testIf1() throws RecognitionException {
//        RDouble res1 = RDoubleFactory.getArray(1);
//        RDouble res2 = RDoubleFactory.getArray(2);
//
//        Assert.assertEquals(eval("if(TRUE) 1 else 2"), res1);
//        Assert.assertEquals(eval("if(!TRUE) 1 else 2"), res2);
//        Assert.assertEquals(eval("if(FALSE) 1 else 2"), res2);
//        Assert.assertEquals(eval("if(!FALSE) 1 else 2"), res1);
//    }

    @Test
    public void testIf2() throws RecognitionException {
        assertEval("if(TRUE) 1 else 2", "1.0");
        assertEval("if(FALSE) 1 else 2", "2.0");
    }

    @Test
    public void testIfNot1() throws RecognitionException {
        assertEval("if(!FALSE) 1 else 2", "1.0");
        assertEval("if(!TRUE) 1 else 2", "2.0");
    }

}
