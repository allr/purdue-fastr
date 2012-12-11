package r.shootout.pidigits;

import org.junit.*;

import r.shootout.*;

public class TestPidigits extends ShootoutTestBase {
    @Test
    public void testPidigits() {
        assertShootout("pidigits", "pidigits", 10, "3141592653\t:10\n", null, "NULL");
        assertShootout("pidigits", "pidigits", 20, "3141592653\t:10\n5897932384\t:20\n", null, "NULL");
    }
}
