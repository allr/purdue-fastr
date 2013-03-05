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
    public void testSpectralNormAlt2() {
        assertShootout("spectralnorm", "spectralnorm-alt2", 10, "1.2718440192507243 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-alt2", 15, "1.273394737237051 \n", "", "NULL");
    }

    @Test
    public void testSpectralNormAlt3() {
        assertShootout("spectralnorm", "spectralnorm-alt3", 10, "1.2718440192507248 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-alt3", 15, "1.273394737237051 \n", "", "NULL");
    }

    @Test
    public void testSpectralNormMath() {
        assertShootout("spectralnorm", "spectralnorm-math", 10, "1.2718440192507243 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-math", 15, "1.2733947372370509 \n", "", "NULL");
    }

    @Test
    public void testSpectralNormAlt4() {
        assertShootout("spectralnorm", "spectralnorm-alt4", 10, "1.2718440192507243 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-alt4", 15, "1.273394737237051 \n", "", "NULL");
    }

    @Test
    public void testSpectralNormNaive() {
        assertShootout("spectralnorm", "spectralnorm-naive", 10, "1.2718440192507248 \n", "", "NULL");
        assertShootout("spectralnorm", "spectralnorm-naive", 15, "1.273394737237051 \n", "", "NULL");
    }
}
