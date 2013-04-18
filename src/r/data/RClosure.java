package r.data;

import r.Truffle.*;

public interface RClosure extends RAny, RCallable {

    String TYPE_STRING = "closure";

    MaterializedFrame enclosingFrame();

    RFunction function();
}
