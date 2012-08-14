package r.data.internal;

import r.*;
import r.data.*;

public class IntImpl extends ArrayImpl implements RInt {
    int[] content;

    public IntImpl(int[] values) {
        content = new int[values.length];
        System.arraycopy(values, 0, content, 0, values.length);
    }

    public Object get(int i) {
        return content[i - 1];
    }

    public int getInt(int i) {
        return content[i - 1];
    }

    @Override
    public RInt asInt() {
        return this;
    }

    public String pretty() {
        if (content.length == 0) {
            return RInt.TYPE_STRING + "(0)";
        }
        String fst = Convert.int2string(content[0]);
        if (content.length == 1) {
            return fst;
        }
        StringBuilder str = new StringBuilder();
        str.append(fst);
        for (int i = 1; i < content.length; i++) {
            str.append(", ");
            str.append(Convert.int2string(content[i]));
        }
        return str.toString();
    }
}
