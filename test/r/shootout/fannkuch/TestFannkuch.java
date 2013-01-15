package r.shootout.fannkuch;

import org.junit.*;

import r.shootout.*;


public class TestFannkuch extends ShootoutTestBase {

    @Test
    public void testFannkuch() {
        assertShootout("fannkuch", "fannkuchredux", 5, "11\nPfannkuchen(5) = 7\n", "", "NULL");
    }
}
