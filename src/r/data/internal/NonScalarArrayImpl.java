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
    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
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

        for (int i = 0; i < n; i++) {
            int maxWidth = -1;
            String cn = "[," + Integer.toString(i + 1) + "]";
            colNames[i] = cn;
            int clen = cn.length();
            if (clen > maxWidth) {
                maxWidth = clen;
            }
            for (int j = 0; j < m; j++) {
                String s = boxedGet(i * m + j).prettyMatrixElement();
                data[j][i] = s;
                int l = s.length();
                if (l > maxWidth) {
                    maxWidth = l;
                }
            }
            colWidth[i] = 1 + maxWidth;
        }

        for (int j = 0; j < m; j++) {
            String rn = "[" + Integer.toString(j + 1) + ",]";
            rowNames[j] = rn;
            int rlen = rn.length();
            if (rlen > rowNamesWidth) {
                rowNamesWidth = rlen;
            }
        }

        StringBuilder res = new StringBuilder();

        strAppend(res, "", rowNamesWidth);
        for (int i = 0; i < n; i++) {
            strAppend(res, colNames[i], colWidth[i]);
        }
        res.append("\n");
        for (int j = 0; j < m; j++) {
            strAppend(res, rowNames[j], rowNamesWidth);
            for (int i = 0; i < n; i++) {
                strAppend(res, data[j][i], colWidth[i]);
            }
            if (j != m - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }
}
