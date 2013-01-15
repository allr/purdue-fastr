package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.errors.RError;

public class TestSimpleFailures extends TestBase {
    @Test
    public void testFailure() throws RecognitionException {
        assertEvalError("this.object.should.not.exist", "object 'this.object.should.not.exist' not found");
    }

    @Test
    public void testWarning() throws RecognitionException {
        assertEvalWarning("{ x = c(1,2,3,4); x[x %% 2 == 0] <- c(1,2,3,4); }", "1.0, 2.0, 3.0, 4.0", RError.NOT_MULTIPLE_REPLACEMENT);
    }

}
