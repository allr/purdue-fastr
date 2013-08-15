package r.analysis.codegen;

import org.junit.Test;

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


public class FastrLoaderTest {

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


}
