package r.data.internal;

import r.*;


public abstract class NonScalarArrayImpl extends ArrayImpl {

    protected int[] dimensions; // the content shall never be modified once set
    protected int refcount;

    @Override
    public final boolean isShared() {
        return refcount > 1;  // ==2
    }

    @Override
    public final void ref() {
        if (refcount == 0) {
            refcount = 1;
        } else if (refcount == 1) {
            refcount = 2;
        }
    }

    @Override
    public int[] dimensions() {
        return dimensions;
    }

    @Override
    public NonScalarArrayImpl setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    private static void strAppend(StringBuilder b, String s, int width) {
        int spaces = width - s.length();
        Utils.check(spaces >= 0);
        for (int i = 0; i < spaces; i++) {
            b.append(' ');
        }
        b.append(s);
    }

    protected String matrixPretty() {
        Utils.check(dimensions != null);
        Utils.check(dimensions.length == 2);
        final int m = dimensions[0];
        final int n = dimensions[1];

        if (m == 0 && n == 0) {
            return "<0 x 0 matrix>";
        }

        String[] colNames = new String[n];
        String[] rowNames = new String[m];
        String[][] data = new String[m][n];
        int[] colWidth = new int[n];
        int rowNamesWidth = -1;

        for (int j = 0; j < n; j++) {
            int maxWidth = -1;
            String cn = "[," + Integer.toString(j + 1) + "]";
            colNames[j] = cn;
            int clen = cn.length();
            if (clen > maxWidth) {
                maxWidth = clen;
            }
            for (int i = 0; i < m; i++) {
                String s = boxedGet(j * m + i).prettyMatrixElement();
                data[i][j] = s;
                int l = s.length();
                if (l > maxWidth) {
                    maxWidth = l;
                }
            }
            colWidth[j] = 1 + maxWidth;
        }

        for (int i = 0; i < m; i++) {
            String rn = "[" + Integer.toString(i + 1) + ",]";
            rowNames[i] = rn;
            int rlen = rn.length();
            if (rlen > rowNamesWidth) {
                rowNamesWidth = rlen;
            }
        }

        StringBuilder res = new StringBuilder();

        strAppend(res, "", rowNamesWidth);
        for (int j = 0; j < n; j++) {
            strAppend(res, colNames[j], colWidth[j]);
        }
        res.append("\n");
        for (int i = 0; i < m; i++) {
            strAppend(res, rowNames[i], rowNamesWidth);
            for (int j = 0; j < n; j++) {
                strAppend(res, data[i][j], colWidth[j]);
            }
            if (i != m - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }
}
