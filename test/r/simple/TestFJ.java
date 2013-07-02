package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.FJ;


public class TestFJ extends SimpleTestBase {


    @Test
    public void testFJInit() throws RecognitionException {
        assertEval("a = rep(1,1000); b = rep(2,1000); sum(a+b);","3000.0");

/*        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        int size = 10;
        for (int j = 0; j < 8; ++j) {
            long tt = 0;
            for (int i = 0; i < 10; ++i) {
                long t = System.currentTimeMillis();
                assertEval("f <- function(a,b) { a + b + a + b + a + a }; a = rep(0,"+size+"); b = rep(0,"+size+"); sum(f(a,b)); ","0.0");
                //assertEval("sum(rep(1.0,100000000))", "1.0E8");
                //FJ.pool.invoke(new SimpleTask(new double[100000000], 78));
                t = System.currentTimeMillis() - t;
                tt += t;
            }
            System.out.println((tt / 10.0));
            size = size * 10;
        } */
    }

}
