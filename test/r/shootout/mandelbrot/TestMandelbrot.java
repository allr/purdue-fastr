package r.shootout.mandelbrot;

import org.junit.*;

import r.shootout.*;

// TODO: this test does not check its output, because the output is not correctly captured
// the benchmark runs "cat" in a pipe and via it prints binary data, this combination is not captured by ShootoutTestBase
public class TestMandelbrot extends ShootoutTestBase {
    @Test
    public void testMandelbrot() {

        // TODO: this test spawns a child process, which then dumps binary data to standard output
        //       figure out how (if?) the output could be captured by the harness
        // assertShootout("mandelbrot", "mandelbrot", 100, null, null, "NULL");

        // a modification of the benchmark that does produces an ascii output (and does not create a pipe)
        assertShootout("mandelbrot", "mandelbrot-ascii", 10,
                        "P4\n" +
                        "10 10 \n" +
                        "00 00 \n" +
                        "01 00 \n" +
                        "07 80 \n" +
                        "07 c0 \n" +
                        "3f c0 \n" +
                        "ff 80 \n" +
                        "3f c0 \n" +
                        "07 c0 \n" +
                        "07 80 \n" +
                        "01 00 \n", null, "NULL");
    }
}
