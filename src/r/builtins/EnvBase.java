package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;

import com.oracle.truffle.api.frame.*;

final class EnvBase {

    static RSymbol parseX(RAny arg, ASTNode ast) {
        if (arg instanceof RString) {
            RString sarg = (RString) arg;
            int size = sarg.size();
            if (size > 0) {
                String s = sarg.getString(0);
                if (size > 1) {
                    RContext.warning(ast, RError.ONLY_FIRST_VARIABLE_NAME);
                }
                return RSymbol.getSymbol(s);
            }
        }
        throw RError.getInvalidFirstArgument(ast);
    }

    static boolean parseInherits(RAny arg, ASTNode ast) {
        RLogical larg = arg.asLogical();
        if (larg.size() == 0) { throw RError.getInvalidArgument(ast, "inherits"); }
        int v = larg.getLogical(0);
        if (v != RLogical.NA) { return v == RLogical.TRUE; }
        throw RError.getInvalidArgument(ast, "inherits");
    }

    static REnvironment parseEnvir(RAny arg, ASTNode ast) {
        if (arg instanceof REnvironment) { return (REnvironment) arg; }
        throw RError.getInvalidArgument(ast, "envir");
    }

    static REnvironment extractEnvironment(RAny envir, RAny pos, Frame frame, ASTNode ast) {
        if (envir != null) { return parseEnvir(envir, ast); }
        if (pos != null) { return asEnvironment(frame, ast, pos, true); }
        return frame == null ? REnvironment.GLOBAL : RFrameHeader.environment(frame);
    }

    // NOTE: get and assign have different failure modes for X
    static RSymbol parseXSilent(RAny arg, ASTNode ast) {
        if (!(arg instanceof RString)) { throw RError.getInvalidFirstArgument(ast); }
        RString sarg = (RString) arg;
        if (sarg.size() == 0) { throw RError.getInvalidFirstArgument(ast); }
        return RSymbol.getSymbol(sarg.getString(0));
    }

    static REnvironment asEnvironment(Frame frame, ASTNode ast, RAny arg) {
        return asEnvironment(frame, ast, arg, false);
    }

    static REnvironment asEnvironment(Frame frame, ASTNode ast, RAny arg, boolean fakePromise) {
        if (arg instanceof REnvironment) { return (REnvironment) arg; }
        if (!(arg instanceof RInt || arg instanceof RDouble)) { throw RError.getInvalidArgument(ast, "pos"); }
        RInt iarg = arg.asInt();
        int size = iarg.size();
        if (size == 0) { throw RError.getInvalidArgument(ast, "pos"); }
        if (size == 1) {
            int idx = iarg.getInt(0);
            if (idx == -1) {
                if (frame != null) {
                    if (fakePromise) { return RFrameHeader.environment(frame); }
                    Frame enclosingFrame = RFrameHeader.enclosingFrame(frame);
                    return enclosingFrame == null ? REnvironment.GLOBAL : RFrameHeader.environment(enclosingFrame);
                } else {
                    if (fakePromise) { return REnvironment.GLOBAL; }
                    throw RError.getNoEnclosingEnvironment(ast);
                }
            }
            if (idx == 1) { return REnvironment.GLOBAL; }
        }
        if (size > 1) { throw Utils.nyi("create a list..."); }
        // FIXME: add other environments when supported
        throw RError.getInvalidArgument(ast, "pos");
    }
}
