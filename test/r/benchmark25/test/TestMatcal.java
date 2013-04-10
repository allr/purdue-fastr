package r.benchmark25.test;

import org.junit.*;

// NOTE: the benchmarks are slightly modified to run for a reasonable amount of time and to produce
// a result that can be checked
// (the modified versions that are executed from here are in the "benchmark25/test" directory)
public class TestMatcal extends B25TestBase {

    @Test
    public void test1() {
        assertB25("b25-matcal-1", "2.0019");
    }

    @Test
    public void test2() {
        assertB25("b25-matcal-2", "593.9124");
    }

    @Test
    public void test3() {
        assertB25("b25-matcal-3", "152375.9597");
    }

    @Test
    public void test4() {
        assertB25("b25-matcal-4", "787.72202");
    }

    @Test
    public void test5() {
        assertB25("b25-matcal-5", "-161.02601");
    }

}
