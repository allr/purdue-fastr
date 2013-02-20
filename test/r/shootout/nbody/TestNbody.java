package r.shootout.nbody;

import org.junit.*;
import r.shootout.*;

public class TestNbody extends ShootoutTestBase {

    @Test
    public void testNbody() {
        assertShootout("nbody", "nbody", 5, "-0.1690751638285245 \n-0.16907410488299296 \n", "", "NULL");
        assertShootout("nbody", "nbody", 6, "-0.1690751638285245 \n-0.16907389015486243 \n", "", "NULL");
    }

    @Test
    public void testNbodyNaive() {
        assertShootout("nbody", "nbody-naive", 5, "-0.1690751638285245 \n-0.1690741048829929 \n", "", "NULL");
        assertShootout("nbody", "nbody-naive", 6, "-0.1690751638285245 \n-0.16907389015486235 \n", "", "NULL");
    }

    @Test
    public void testNbodyNaive2() {
        assertShootout("nbody", "nbody-naive2", 5, "-0.1690751638285245 \n-0.1690741048829929 \n", "", "NULL");
        assertShootout("nbody", "nbody-naive2", 6, "-0.1690751638285245 \n-0.16907389015486235 \n", "", "NULL");
    }

    @Test
    public void testNbody3() {
        assertShootout("nbody", "nbody-3", 5, "-0.1690751638285245 \n-0.16907410488299296 \n", "", "NULL");
        assertShootout("nbody", "nbody-3", 6, "-0.1690751638285245 \n-0.16907389015486243 \n", "", "NULL");
    }

}
