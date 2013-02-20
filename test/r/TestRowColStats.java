package r;

import org.junit.Test;

public class TestRowColStats extends TestBase {

    @Test
    public void testColStatsMatrix() {
        assertTrue("# colSums on matrix drop dimension\n" +
                "a = colSums(matrix(1:12,3,4))\n" +
                "is.null(dim(a))");

        assertTrue("# colSums on matrix have correct length \n" +
                "a = colSums(matrix(1:12,3,4))\n" +
                "length(a) == 4");

        assertTrue("# colSums on matrix have correct values\n" +
                "a = colSums(matrix(1:12,3,4))\n" +
                "a[1] == 6 && a[2] == 15 && a[3] == 24 && a[4] == 33");
    }

    @Test
    public void testColStatsArray() {
        assertTrue("# colSums on array have correct dimension\n" +
                "a = colSums(array(1:24,c(2,3,4)))\n" +
                "d = dim(a)\n" +
                "d[1] == 3 && d[2] == 4");

        assertTrue("# colSums on array have correct length \n" +
                "a = colSums(array(1:24,c(2,3,4)))\n" +
                "length(a) == 12");

        assertTrue("# colSums on array have correct values\n" +
                "a = colSums(array(1:24,c(2,3,4)))\n" +
                "a[1,1] == 3 && a[2,2] == 19 && a[3,3] == 35 && a[3,4] == 47");
    }

    @Test
    public void testRowStats() {
        assertTrue("# rowSums on matrix drop dimension\n" +
                "a = rowSums(matrix(1:12,3,4))\n" +
                "is.null(dim(a))");

        assertTrue("# rowSums on matrix have correct length \n" +
                "a = rowSums(matrix(1:12,3,4))\n" +
                "length(a) == 3");

        assertTrue("# rowSums on matrix have correct values\n" +
                "a = rowSums(matrix(1:12,3,4))\n" +
                "a[1] == 22 && a[2] == 26 && a[3] == 30");
    }

    @Test
    public void testRowStatsArray() {
        assertTrue("# rowSums on array have no dimension\n" +
                "a = rowSums(array(1:24,c(2,3,4)))\n" +
                "is.null(dim(a))\n");

        assertTrue("# row on array have correct length \n" +
                "a = rowSums(array(1:24,c(2,3,4)))\n" +
                "length(a) == 2");

        assertTrue("# rowSums on array have correct values\n" +
                "a = rowSums(array(1:24,c(2,3,4)))\n" +
                "a[1] == 144 && a[2] == 156");
    }

}
