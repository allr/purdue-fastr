package r.data;

import com.oracle.truffle.api.frame.*;

public interface RClosure extends RAny, RCallable {

    String TYPE_STRING = "closure";

    MaterializedFrame enclosingFrame();
    RFunction function();
}
