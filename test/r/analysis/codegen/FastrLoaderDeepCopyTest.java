package r.analysis.codegen;

import com.oracle.truffle.api.frame.Frame;
import org.junit.Test;
import r.nodes.truffle.RNode;

import static junit.framework.Assert.*;


class DCBase implements DeepCopyable {

    public DCBase() {
    }

    public DCBase(DCBase other, boolean move) {
    }

    @Override
    public DeepCopyable deepCopy() {
        return new DCBase(this, true);
    }
}

class DCTest extends DCBase { }

class DCSimpleField extends DCBase {
    int i = 2;
    DCSimpleField(int i) {
        this.i = i;
    }
}

class DCSimpleArray extends DCBase {
    int[] i;
    DCSimpleArray(int size) {
        i = new int[size];
        for (int j = 0; j < size; ++j)
            i[j] = j;
    }
}

class DCSharedArray extends DCBase {
    @Shared
    int[] i;
    DCSharedArray(int size) {
        i = new int[size];
        for (int j = 0; j < size; ++j)
            i[j] = j;
    }
}

class DCDeepCopyableField extends DCBase {
    DCSimpleArray i;
    DCDeepCopyableField(DCSimpleArray i) {
        this.i = i;
    }
}

class DCSharedDeepCopyableField extends DCBase {
    @Shared
    DeepCopyable i;
    DCSharedDeepCopyableField(DeepCopyable i) {
        this.i = i;
    }
}

class DCNode extends RNode {

    @Override
    public Object execute(Frame frame) {
        return null;
    }
}

class DCNodeField extends DCNode {

    DCNode node;
    DCNodeField(DCNode node) {
        this.node = adoptChild(node);
    }
}

class DCSimpleFieldNode extends DCNode {
    int i;
    public DCSimpleFieldNode(int i) {
        this.i = i;
    }
}

class DCNodeFieldNode extends DCNode {
    DCSimpleFieldNode node;
    DCNodeFieldNode(DCSimpleFieldNode node) {
        this.node = adoptChild(node);
    }
}

class DCNodeSharedNode extends DCNode {
    @Shared
    DCSimpleFieldNode node;
    DCNodeSharedNode(DCSimpleFieldNode node) {
        this.node = node;
    }
}

class DCNodeArrayNode extends DCNode {
    DCSimpleFieldNode[] nodes;
    DCNodeArrayNode(int size) {
        nodes = new DCSimpleFieldNode[size];
        for (int i = 0; i < size; ++i) {
            nodes[i] = adoptChild(new DCSimpleFieldNode(i));
        }
    }
}

class DCNodeSharedArrayNode extends DCNode {
    @Shared
    DCSimpleFieldNode[] nodes;
    DCNodeSharedArrayNode(int size) {
        nodes = new DCSimpleFieldNode[size];
        for (int i = 0; i < size; ++i) {
            nodes[i] = adoptChild(new DCSimpleFieldNode(i));
        }
    }

}


// ---------------------------------------------------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------------------------------------------------

public class FastrLoaderDeepCopyTest {

    @Test
    public void isLoaderPresent() {
        String loaderClass = Thread.currentThread().getContextClassLoader().getClass().getName();
        String expectedClass = FastrLoader.class.getName();
        assertEquals(expectedClass, loaderClass);
    }

    @Test
    public void deepCopyableObjectsHaveDeepCopyMethod() {
        try {
            assertNotNull(DCTest.class.getDeclaredMethod("deepCopy", new Class[] {}));
        } catch (NoSuchMethodException e) {
            assertTrue(false);
        }
    }

    @Test
    public void deepCopyableObjectsHaveDeepOrShallowConstructor() {
        try {
            assertNotNull(DCTest.class.getConstructor(new Class[] {DCTest.class, boolean.class}));
        } catch (NoSuchMethodException e) {
            assertTrue(false);
        }
    }

    @Test
    public void deepCopyWithSimpleField() {
        DCSimpleField dc1 = new DCSimpleField(42);
        DCSimpleField dc2 = (DCSimpleField) dc1.deepCopy();
        assertEquals(dc1.i, 42);
        assertEquals(dc2.i, 42);
    }

    @Test
    public void deepCopyWithSimpleArray() {
        DCSimpleArray da1 = new DCSimpleArray(10);
        DCSimpleArray da2 = (DCSimpleArray) da1.deepCopy();
        assertTrue(da1.i != da2.i);
        assertEquals(da1.i.length, da2.i.length);
        for (int i = 0; i < da1.i.length; ++i)
            assertEquals(da1.i[i], da2.i[i]);
    }

    @Test
    public void deepCopySharedArray() {
        DCSharedArray da1 = new DCSharedArray(10);
        DCSharedArray da2 = (DCSharedArray) da1.deepCopy();
        assertTrue(da1.i == da2.i);
    }

    @Test
    public void deepCopyDeepCopyableField() {
        DCDeepCopyableField dci1 = new DCDeepCopyableField(new DCSimpleArray(10));
        DCDeepCopyableField dci2 = (DCDeepCopyableField) dci1.deepCopy();
        assertTrue(dci1.i != dci2.i);
        assertTrue(dci1.i.i.length == dci2.i.i.length);
        for (int i = 0; i < dci1.i.i.length; ++i)
            assertEquals(dci1.i.i[i], dci2.i.i[i]);
    }

    @Test
    public void deepCopyNullDeepCopyableField() {
        DCDeepCopyableField dci1 = new DCDeepCopyableField(null);
        DCDeepCopyableField dci2 = (DCDeepCopyableField) dci1.deepCopy();
        assertNull(dci2.i);
    }

    @Test
    public void deepCopySharedDeepCopyableField() {
        DCSharedDeepCopyableField dci1 = new DCSharedDeepCopyableField(new DCSimpleArray(10));
        DCSharedDeepCopyableField dci2 = (DCSharedDeepCopyableField) dci1.deepCopy();
        assertTrue(dci1.i == dci2.i);
    }

    @Test
    public void deepCopySharedNullDeepCopyableField() {
        DCSharedDeepCopyableField dci1 = new DCSharedDeepCopyableField(null);
        DCSharedDeepCopyableField dci2 = (DCSharedDeepCopyableField) dci1.deepCopy();
        assertNull(dci2.i);
    }

    @Test
    public void nodeField() {
        DCNodeField dn1 = new DCNodeField(new DCNode());
        DCNodeField dn2 = (DCNodeField) dn1.deepCopy();
        assertTrue(dn1 != dn2);
        assertTrue(dn1.node != dn2.node);
        assertTrue(dn1.node.getParent() == dn1);
        assertTrue(dn2.node.getParent() == dn2);
    }

    @Test
    public void nullNodeField() {
        DCNodeField dn1 = new DCNodeField(null);
        DCNodeField dn2 = (DCNodeField) dn1.deepCopy();
        assertTrue(dn1 != dn2);
        assertTrue(dn2.node == null);
    }

    @Test
    public void nodeFieldContents() {
        DCNodeFieldNode dn1 = new DCNodeFieldNode(new DCSimpleFieldNode(42));
        DCNodeFieldNode dn2 = (DCNodeFieldNode) dn1.deepCopy();
        assertTrue(dn1 != dn2);
        assertTrue(dn1.node != dn2.node);
        assertTrue(dn1.node.getParent() == dn1);
        assertTrue(dn2.node.getParent() == dn2);
        assertTrue(dn1.node.i == 42);
        assertTrue(dn2.node.i == 42);
    }

    @Test
    public void nodeSharedField() {
        DCNodeSharedNode ds1 = new DCNodeSharedNode(new DCSimpleFieldNode(42));
        DCNodeSharedNode ds2 = (DCNodeSharedNode) ds1.deepCopy();
        assertTrue(ds1 != ds2);
        assertTrue(ds1.node == ds2.node);
        assertTrue(ds1.node.i == 42);
    }

    @Test
    public void nodeArrayField() {
        DCNodeArrayNode da1 = new DCNodeArrayNode(10);
        DCNodeArrayNode da2 = (DCNodeArrayNode) da1.deepCopy();
        assertTrue(da1 != da2);
        assertTrue(da1.nodes != da2.nodes);
        assertTrue(da1.nodes.length == da2.nodes.length);
        for (int i = 0; i < 10; ++i) {
            assertTrue(da1.nodes[i] != da2.nodes[i]);
            assertTrue(da1.nodes[i].i == i);
            assertTrue(da2.nodes[i].i == i);
            assertTrue(da2.nodes[i].getParent() == da2);
        }
    }

    @Test
    public void nodeArrayFieldWithNulls() {
        DCNodeArrayNode da1 = new DCNodeArrayNode(10);
        da1.nodes[5] = null;
        DCNodeArrayNode da2 = (DCNodeArrayNode) da1.deepCopy();
        assertTrue(da1 != da2);
        assertTrue(da1.nodes != da2.nodes);
        assertTrue(da1.nodes.length == da2.nodes.length);
        for (int i = 0; i < 10; ++i) {
            if (i == 5) {
                assertTrue(da1.nodes[i] == null);
                assertTrue(da2.nodes[i] == null);
            } else {
                assertTrue(da1.nodes[i] != da2.nodes[i]);
                assertTrue(da1.nodes[i].i == i);
                assertTrue(da2.nodes[i].i == i);
                assertTrue(da2.nodes[i].getParent() == da2);
            }
        }
    }

    @Test
    public void nodeArrayFieldNull() {
        DCNodeArrayNode da1 = new DCNodeArrayNode(10);
        da1.nodes = null;
        DCNodeArrayNode da2 = (DCNodeArrayNode) da1.deepCopy();
        assertTrue(da1 != da2);
        assertTrue(da1.nodes == null);
        assertTrue(da2.nodes == null);
    }

    @Test
    public void nodeArrayFieldSharedWithNulls() {
        DCNodeSharedArrayNode da1 = new DCNodeSharedArrayNode(10);
        da1.nodes[5] = null;
        DCNodeSharedArrayNode da2 = (DCNodeSharedArrayNode) da1.deepCopy();
        assertTrue(da1 != da2);
        assertTrue(da1.nodes == da2.nodes);
        for (int i = 0; i < 10; ++i) {
            if (i == 5) {
                assertTrue(da1.nodes[i] == null);
            } else {
                assertTrue(da2.nodes[i].i == i);
                assertTrue(da2.nodes[i].getParent() == da1);
            }
        }
    }



}
