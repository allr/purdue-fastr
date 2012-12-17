package r.shootout.mandelbrot;

import org.junit.*;

import r.shootout.*;

// TODO: this test does not check its output, because the output is not correctly captured
// the benchmark runs "cat" in a pipe and via it prints binary data, this combination is not captured by ShootoutTestBase
public class TestMandelbrot extends ShootoutTestBase {
    @Test
    public void testMandelbrot() {
        assertShootout("mandelbrot", "mandelbrot", 100, null, null, "NULL"); // TODO: check the output !!!
    }
}
