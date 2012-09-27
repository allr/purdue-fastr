package r.data;

import com.oracle.truffle.*;
import com.oracle.truffle.compiler.*;
import com.oracle.truffle.runtime.Frame;

public interface RClosure extends RAny, Compilable<Object[]>, FunctionProxy {
    Frame environment();
    RFunction function();
}
