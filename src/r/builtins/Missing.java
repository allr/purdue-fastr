package r.builtins;

import com.oracle.truffle.api.frame.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.FunctionCall;

final class Missing extends CallFactory {

    static final CallFactory _ = new Missing("missing", new String[]{"x"}, null);

    private Missing(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static RSymbol parseX(Object arg, ASTNode ast) { // TODO: accept symbols (names) when they're supported by fastr
        if (arg instanceof RString) {
            RString sarg = (RString) arg;
            if (sarg.size() == 1) {
                return RSymbol.getSymbol(sarg.getString(0));
            }
        }
        throw RError.getInvalidUse(ast, "missing");
    }

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);

        return new Builtin.Builtin1(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny arg) {
                assert Utils.check(FunctionCall.PROMISES);

                RSymbol xSymbol = parseX(arg, ast);
                if (frame == null) {
                    // top-level
                    Object value = xSymbol.getValueNoForce();
                    if (value == null) {
                        throw RError.getMissingArguments(ast);
                    } else if (value instanceof RPromise) {
                        RPromise p = (RPromise) value;
                        return hasCycle(p) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    } else {
                        return RLogical.BOXED_FALSE;
                    }
                }
                // non top-level
                FrameSlot slot = RFrameHeader.findVariable(frame, xSymbol);

                if (slot == null) {
                    throw RError.getMissingArguments(ast);
                }
                Object value = frame.getObject(slot);
                if (value == null) {
                    throw RError.getMissingArguments(ast);
                }
                if (value instanceof RPromise) {
                    RPromise p = (RPromise) value;
                    if (p.isDefault()) {
                        return RLogical.BOXED_TRUE;
                    } else {
                        return isMissing(p) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                    }
                } else {
                    return RLogical.BOXED_FALSE;
                }
            }
        };
    }

    public static boolean hasCycle(RPromise p) { // for top level
        try {
            if (!p.markMissingDirty()) {
                return true;
            }
            RNode expr = p.expression();
            ASTNode ast = expr.getAST();

            if (ast instanceof r.nodes.SimpleAccessVariable) {
                RSymbol symbol = ((r.nodes.SimpleAccessVariable) ast).getSymbol();
                Frame frame = p.frame();
                FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
                if (slot != null) {
                    Object value = frame.getObject(slot);
                    if (value != null && value instanceof RPromise) {
                        return hasCycle((RPromise) value);
                    }
                }

            }
            return false;
        } finally {
            p.markMissingClean();
        }
    }

    public static boolean isMissing(RPromise p) {
        if (p.isMissing()) {
            return true;
        }
        try {
            if (!p.markMissingDirty()) {
                return true; // cycle means missing
            }
            RNode expr = p.expression();
            ASTNode ast = expr.getAST();

            if (ast instanceof r.nodes.SimpleAccessVariable) {
                RSymbol symbol = ((r.nodes.SimpleAccessVariable) ast).getSymbol();
                Frame frame = p.frame();
                FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
                if (slot != null) {
                    Object value = frame.getObject(slot);
                    if (value != null && value instanceof RPromise) {
                        return isMissing((RPromise) value);
                    }
                }

            }
            return false;
        } finally {
            p.markMissingClean();
        }
    }
}
