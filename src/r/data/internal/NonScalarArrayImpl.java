package r.data.internal;

import java.util.*;

import r.*;
import r.data.*;

// children of this class can still implement a scalar value, it would only not be very fast if scalars of that type were frequently used
// fixme - perhaps rename the class
public abstract class NonScalarArrayImpl extends ArrayImpl implements RArray {

    protected int[] dimensions; // the content shall never be modified once set
    protected Names names;
    protected int refcount;
    protected Attributes attributes;

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

    @Override
    public Attributes attributes() {
        return attributes;
    }

    @Override
    public NonScalarArrayImpl setAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public Names names() {
        return names;
    }

    @Override
    public NonScalarArrayImpl setNames(Names names) {
        this.names = names;
        return this;
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

        Utils.strAppend(res, "", rowNamesWidth);
        for (int j = 0; j < n; j++) {
            Utils.strAppend(res, colNames[j], colWidth[j]);
        }
        res.append("\n");
        for (int i = 0; i < m; i++) {
            Utils.strAppend(res, rowNames[i], rowNamesWidth);
            for (int j = 0; j < n; j++) {
                Utils.strAppend(res, data[i][j], colWidth[j]);
            }
            if (i != m - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }
}
