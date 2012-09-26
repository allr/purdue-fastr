package r.data;

import com.oracle.truffle.runtime.Frame;

public interface RClosure extends RAny {
    Frame environment();
    RFunction function();
}
