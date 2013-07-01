package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.FJ;


public class TestFJ extends SimpleTestBase {

    @Test
    public void testFJInit() throws RecognitionException {
        assertEval("a = rep(1,1000); b = rep(2,1000); sum(a+b);","3000.0");

/*        FJ.initialize();
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        long tt = 0;
        for (int i = 0; i < 40; ++i) {
            long t = System.currentTimeMillis();
            assertEval("f <- function(a,b) { a + b + a + b + a + a }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","8.0E8");
            //assertEval("sum(rep(1.0,100000000))", "1.0E8");
            //FJ.pool.invoke(new SimpleTask(new double[100000000], 78));
            t = System.currentTimeMillis() - t;
            tt += t;
        } */
       // System.out.println("time: "+ (tt / 40));
    }

}
