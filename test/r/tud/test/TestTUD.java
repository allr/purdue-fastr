package r.tud.test;

import org.junit.*;

public class TestTUD extends TUDTestBase {

    @Test
    public void testGibbs() {
        assertTUD("gibbs", "130.35648");
    }
}
