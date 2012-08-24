package r;

import org.antlr.runtime.*;
import org.junit.*;

public class TestSimpleIfEvaluator extends TestBase {

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

  /*
   * @Test
    public void testIfNot1() throws RecognitionException {
        assertEval("if(!FALSE) 1 else 2", "1.0");
        assertEval("if(!TRUE) 1 else 2", "2.0");
    }
   */
}
