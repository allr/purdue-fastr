package r;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;

public class TestAperm extends TestBase {

    @Test
    public void testAperm() throws RecognitionException {
        assertTrue("# default argument for permutation is transpose\n" +
                "a = array(1:4,c(2,2))\n" +
                "b = aperm(a)\n" +
                "(a[1,1] == b[1,1]) && (a[1,2] == b[2,1]) && (a[2,1] == b[1,2]) && (a[2,2] == b[2,2])");

        assertTrue("# default for resize is true\n" +
                "a = array(1:24,c(2,3,4))\n" +
                "b = aperm(a)\n" +
                "dim(b)[1] == 4 && dim(b)[2] == 3 && dim(b)[3] == 2");

        assertTrue("# no resize does not change the dimensions\n" +
                "a = array(1:24,c(2,3,4))\n" +
                "b = aperm(a, resize=FALSE)\n" +
                "dim(b)[1] == 2 && dim(b)[2] == 3 && dim(b)[3] == 4");

        assertTrue("# correct structure with resize\n" +
                "a = array(1:24,c(2,3,4))\n" +
                "b = aperm(a, c(2,3,1))\n" +
                "a[1,2,3] == b[2,3,1]");

        assertTrue("# correct structure on cubic array\n" +
                "a = array(1:24,c(3,3,3))\n" +
                "b = aperm(a, c(2,3,1))\n" +
                "a[1,2,3] == b[2,3,1] && a[2,3,1] == b[3,1,2] && a[3,1,2] == b[1,2,3]");

        assertTrue("# correct structure on cubic array with no resize\n" +
                "a = array(1:24,c(3,3,3))\n" +
                "b = aperm(a, c(2,3,1), resize = FALSE)\n" +
                "a[1,2,3] == b[2,3,1] && a[2,3,1] == b[3,1,2] && a[3,1,2] == b[1,2,3]");

        assertTrue("# correct structure without resize\n" +
                "a = array(1:24,c(2,3,4))\n" +
                "b = aperm(a, c(2,3,1), resize = FALSE)\n" +
                "a[1,2,3] == b[2,1,2]");

        assertEvalError("# first argument not an array\n" +
                "aperm(c(1,2,3))\n","nvalid first argument, must be an array");

        assertEvalError("# invalid perm length\n" +
                "aperm(array(1,c(3,3,3)), c(1,2))","'perm' is of wrong length");

        assertEvalError("# perm is not a permutation vector\n" +
                "aperm(array(1,c(3,3,3)), c(1,2,1))","invalid 'perm' argument");

        assertEvalError("# perm value out of bounds\n" +
                "aperm(array(1,c(3,3,3)), c(1,2,0))","value out of range in 'perm'");

        assertEvalWarning("# perm specified in complex numbers produces warning\n" +
                "aperm(array(1:27,c(3,3,3)), c(1+1i,3+3i,2+2i))[1,2,3] == array(1:27,c(3,3,3))[1,3,2]","TRUE","imaginary parts discarded in coercion");

    }

}
