package r.simple;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import r.FJ;
import r.simple.SimpleTestBase;

import java.util.concurrent.RecursiveAction;

/**
 * Created with IntelliJ IDEA. User: Peta Date: 6/25/13 Time: 7:49 PM To change this template use File | Settings | File
 * Templates.
 */
public class TestFJ extends SimpleTestBase {

    static class SimpleTask extends RecursiveAction {

        final double[] array;
        final int inc;
        final int start;
        final int end;

        public SimpleTask(double[] array, int inc) {
            this.array = array;
            this.inc = inc;
            this.start = 0;
            this.end = array.length;
        }

        protected SimpleTask(SimpleTask other, int start, int end) {
            this.array = other.array;
            this.inc = other.inc;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int size = end - start;
            if (size < 100000) {
                computeDirect(start, end);
            } else {
                //computeDirect();
                size = size / 2;
                //invokeAll(new SimpleTask(this, start+100000, end));
                //computeDirect(start, start+100000);
                invokeAll(new SimpleTask(this,start, start+size), new SimpleTask(this, start+size, end));
            }
        }

        void computeDirect(int s, int e) {
            for (int i = s; i < e; ++i) {
                if (array[i] == Double.NaN)
                    continue;
                array[i] += inc;
                array[i] += s*i + inc;
                array[i] += e / (i+1) - inc;
            }
        }
    }

    @Test
    public void testFJInit() throws RecognitionException {
        FJ.initialize();
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
        long tt = 0;
        for (int i = 0; i < 40; ++i) {
            long t = System.currentTimeMillis();
            assertEval("f <- function(a,b) { a + b }; a = rep(1,100000000); b = rep(2,100000000); sum(f(a,b)); ","3.0E8");
            //assertEval("sum(rep(1.0,100000000))", "1.0E8");
            //FJ.pool.invoke(new SimpleTask(new double[100000000], 78));
            t = System.currentTimeMillis() - t;
            tt += t;
        }
        System.out.println("time: "+ (tt / 40));
    }

}
