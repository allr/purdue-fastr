package r.shootout.spectralnorm;

import org.junit.Test;
import r.shootout.ShootoutTestBase;

public class TestSpectralNorm extends ShootoutTestBase {
    @Test
    public void testSpectralNorm() {
        assertShootout("spectralnorm", "spectralnorm", 10, "1.2718440192507248 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm", 15, "1.273394737237051 \n", "", "NULL");
    }

    @Test
    public void testSpectralNormAlt3() {
        assertShootout("spectralnorm", "spectralnorm-alt3", 10, "1.2718440192507248 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-alt3", 15, "1.273394737237051 \n", "", "NULL");
    }

}
