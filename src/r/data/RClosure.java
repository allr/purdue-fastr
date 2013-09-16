package r.data;

import r.runtime.*;

public interface RClosure extends RAny, RCallable {

    String TYPE_STRING = "closure";

    Frame enclosingFrame();
    RFunction function();
    Frame createFrame();
}
