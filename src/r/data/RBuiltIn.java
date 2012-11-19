package r.data;

import r.builtins.*;

public interface RBuiltIn extends RAny, RCallable {
    RSymbol name();
    CallFactory callFactory();
}
