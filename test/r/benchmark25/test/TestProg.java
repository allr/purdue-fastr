package r.benchmark25.test;

import org.junit.*;

//NOTE: the benchmarks are slightly modified to run for a reasonable amount of time and to produce
//a result that can be checked
//(the modified versions that are executed from here are in the "benchmark25/test" directory)
public class TestProg extends B25TestBase {

    @Test
    public void test1() {
        assertB25("b25-prog-1", "482.41909");
    }

    @Test
    public void test2() {
        assertB25("b25-prog-2", "4158.38313");
    }

    @Test
    public void test3() {
        assertB25("b25-prog-3", "2684");
    }

    @Test
    public void test4() {
        assertB25("b25-prog-4", "343300");
    }

    @Test
    public void test5() {
        assertB25("b25-prog-5", "1535.34607");
    }

}
