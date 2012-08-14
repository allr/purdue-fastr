package r.data.internal;

import r.*;
import r.data.*;

public class VectorImpl extends ArrayImpl implements RVector {
    RAny[] content;

    @Override
    public Object get(int i) {
        return content[i - 1];
    }

    public String pretty() {
        if (content.length == 0) {
            return RVector.TYPE_STRING + "(0)";
        } else if (content.length == 1) {
            return content[0].toString();
        } else {
            StringBuilder str = new StringBuilder();
            str.append(content[0].toString());
            for (int i = 1; i < content.length; i++) {
                str.append(", ");
                str.append(content[i].toString());
            }
            return str.toString();
        }
    }

    @Override
    public RInt asInt() {
        Utils.nyi();
        return null;
    }
}
