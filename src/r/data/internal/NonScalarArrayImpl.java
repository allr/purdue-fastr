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
    public Attributes attributesRef() {
        if (attributes == null) {
            return null;
        } else {
            return attributes.markShared();
        }
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


    /** Increments the given array of integets within bounds specified by dim argument and returns true on overflow.
     */
    public static boolean increment(int[] idx, int[] dim) {
        return increment(idx, dim, 0);
    }

    /** Increments the given array of digits, with second argument being the maximum numbers for the specified digits.
     * Returns true if the operation overflows. Ignore digits can specify the most significant digits (starting with 0)
     * that will be reported as overflow when changed.
     */
    public static boolean increment(int[] idx, int[] dim, final int ignoreDigits) {
        for (int i = idx.length - 1; i >= ignoreDigits; --i) {
            ++idx[i];
            if (idx[i] > dim[i]) {
                idx[i] = 1;
            } else {
                return false;
            }
        }
        return true;
    }

    /** Array prettyprint.
     *
     */
    protected String arrayPretty() {
        assert (dimensions != null);
        switch (dimensions.length) {
            case 0:
                return "<zero dimension array>";
            case 1: // single dimension array, similar to a vector but no commas
            {
                StringBuilder sb = new StringBuilder();
                sb.append("[1] ");
                for (int i = 0; i < dimensions[0]; ++i) {
                    if (i != 0) {
                        sb.append(" ");
                    }
                    sb.append(boxedGet(i).prettyMatrixElement());
                }
                return sb.toString();
            }
            case 2: // matrix - special routine
                return matrixPretty();
            default: // 3 and more dimensional arrays, printed in 2D chunks
            {
                int[] m = new int[dimensions.length];
                for (int i = 0; i < m.length - 1; ++i) {
                    m[i] = 1;
                }
                int offset = 0;
                int msize = dimensions[0] * dimensions[1];
                StringBuilder sb = new StringBuilder();
                while (!increment(m, dimensions, 2)) {
                    if (offset != 0) {
                        sb.append("\n\n");
                    }
                    sb.append(", ");
                    for (int i = 2; i < m.length; ++i) {
                        sb.append(", " + m[i]);
                    }
                    sb.append("\n\n");
                    matrixPretty(sb, offset);
                    offset += msize;
                }
                return sb.toString();
            }
        }
    }

    /** Outputs matrix to the given string buffer. the fromOffset allows the two dimensions to be taken from a
     * different part of the vector, thus displaying different sections of a more than 2d array.
     */
    protected void matrixPretty(StringBuilder sb, int fromOffset) {
        final int m = dimensions[0];
        final int n = dimensions[1];

        if (m == 0 && n == 0) {
            sb.append("<0 x 0 matrix>");
        } else {

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
                    String s = boxedGet(fromOffset + j * m + i).prettyMatrixElement();
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

            Utils.strAppend(sb, "", rowNamesWidth);
            for (int j = 0; j < n; j++) {
                Utils.strAppend(sb, colNames[j], colWidth[j]);
            }
            sb.append("\n");
            for (int i = 0; i < m; i++) {
                Utils.strAppend(sb, rowNames[i], rowNamesWidth);
                for (int j = 0; j < n; j++) {
                    Utils.strAppend(sb, data[i][j], colWidth[j]);
                }
                if (i != m - 1) {
                    sb.append("\n");
                }
            }
        }
    }

    protected String matrixPretty() {
        Utils.check(dimensions != null);
        Utils.check(dimensions.length == 2);
        StringBuilder sb = new StringBuilder();
        matrixPretty(sb, 0);
        return sb.toString();
    }

    protected String attributesPretty() {
        StringBuilder str = new StringBuilder();
        Attributes a = attributes();
        if (a != null) {
            for (Map.Entry<RSymbol, RAny> e : a.map().entrySet()) {
                str.append("\n");
                str.append("attr(,\"");
                str.append(e.getKey().pretty());
                str.append("\")");
                str.append("\n");
                str.append(e.getValue().pretty());
            }
        }
        return str.toString();
    }
}
