package r.data;

import r.Truffle.*;

public interface RClosure extends RAny, RCallable {

    String TYPE_STRING = "closure";

    Frame enclosingFrame();

    RFunction function();
}
