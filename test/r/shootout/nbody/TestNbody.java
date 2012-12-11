package r.shootout.nbody;

import org.junit.*;
import r.shootout.*;

public class TestNbody extends ShootoutTestBase {

    @Test
    public void testNbody() {
        assertShootout("nbody", "nbody", 5, "-0.1690751638285245 \n-0.16907410488299296 \n", "", "NULL");
        assertShootout("nbody", "nbody", 6, "-0.1690751638285245 \n-0.16907389015486243 \n", "", "NULL");
    }

}
