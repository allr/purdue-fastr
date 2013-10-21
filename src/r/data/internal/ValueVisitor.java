package r.data.internal;

import r.data.*;

public interface ValueVisitor {
    void visit(RAny value);
}
