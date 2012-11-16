package r.data;

import r.builtins.*;

public interface RBuiltIn extends RAny {
    RSymbol name();
    CallFactory callFactory();
}
