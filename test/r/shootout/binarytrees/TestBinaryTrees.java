package r.shootout.binarytrees;

import org.junit.*;
import r.shootout.*;

public class TestBinaryTrees extends ShootoutTestBase {

    @Test
    public void testBinaryTrees() {
        assertShootout("binarytrees", "binarytrees", 5,
"stretch tree of depth 7\t check: -1\n" +
"128\t trees of depth 4\t check: -128\n" +
"32\t trees of depth 6\t check: -32\n" +
"long lived tree of depth 6\t check: -1\n",
                        "", "NULL");
    }

}
