package r.data;

import r.*;

import com.oracle.truffle.*;
import com.oracle.truffle.compiler.*;
import com.oracle.truffle.runtime.*;

public interface RClosure extends RAny, RCallable, Compilable<Object[]>, FunctionProxy {

    String TYPE_STRING = "closure";

    Frame environment();
    RFunction function();

    Object trivialCall(RContext context, Object arg0, Object arg1, Object arg2);
    Object trivialCall(RContext context, Object arg0, Object arg1);
    Object trivialCall(RContext context, Object arg0);
    Object trivialCall(RContext context);
}
