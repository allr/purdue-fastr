package r.shootout.spectralnorm;

import org.junit.*;

import r.shootout.*;

public class TestSpectralNorm extends ShootoutTestBase {
    @Test
    public void testSpectralNorm() {
        assertShootout("spectralnorm", "spectralnorm", 10, "1.2718440192507248 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm", 15, "1.273394737237051 \n", "", "NULL");
    }
}
