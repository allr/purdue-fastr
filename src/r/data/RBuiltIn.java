package r.data;

import r.builtins.*;

public interface RBuiltIn extends RAny, RCallable {

    String TYPE_STRING = "builtin";

    RSymbol name();
    CallFactory callFactory();
}
