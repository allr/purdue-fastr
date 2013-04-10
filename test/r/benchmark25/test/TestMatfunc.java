package r.benchmark25.test;

import org.junit.*;

//NOTE: the benchmarks are slightly modified to run for a reasonable amount of time and to produce
//a result that can be checked
//(the modified versions that are executed from here are in the "benchmark25/test" directory)
public class TestMatfunc extends B25TestBase {

    @Test
    public void test1() {
        assertB25("b25-matfunc-1", "-3047.36999+0i");
    }

    @Test
    public void test2() {
        assertB25("b25-matfunc-2", "-0.00868+0i");
    }

    @Test
    public void test3() {
        assertB25("b25-matfunc-3", "27.6254");
    }

    @Test
    public void test4() {
        assertB25("b25-matfunc-4", "90.89338");
    }

    @Test
    public void test5() {
        assertB25("b25-matfunc-5", "3.33147");
    }

}
