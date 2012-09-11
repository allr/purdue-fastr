package r;

import org.antlr.runtime.*;
import org.junit.*;
public class TestSimpleComparison extends TestBase {

    @Test
    public void testScalars() throws RecognitionException {
        assertEval("{1==1}", "TRUE");
        assertEval("{2==1}", "FALSE");
        assertEval("{1L<=1}", "TRUE");
        assertEval("{1<=0L}", "FALSE");
        assertEval("{x<-2; f<-function(z=x) { if (z<=x) {z} else {x} } ; f(1.4)}", "1.4");
    }
}
