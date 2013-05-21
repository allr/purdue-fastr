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

    public static RAny missing(Frame frame, RSymbol symbol, ASTNode ast) {

        if (frame == null) {
            // top-level
            Object value = symbol.getValueNoForce();
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
        FrameSlot slot = RFrameHeader.findVariable(frame, symbol);

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

    @Override
    public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        // TODO: add support for missing(...)
        check(call, names, exprs);

        final RSymbol symbol = getAccessedSymbol(exprs[0].getAST());
        if (symbol != null) {
            return new BaseR(call) {

                @Override
                public Object execute(Frame frame) {
                    return missing(frame, symbol, ast);
                }

            };
        }

        return new Builtin.Builtin1(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny arg) {
                assert Utils.check(FunctionCall.PROMISES);

                RSymbol xSymbol = parseX(arg, ast);
                return missing(frame, xSymbol, ast);
            }
        };
    }

    public static boolean hasCycle(RPromise p) { // for top level
        try {
            if (!p.markMissingDirty()) {
                return true;
            }
            RSymbol symbol = getAccessedSymbol(p.expression().getAST());
            RPromise next = getPromiseNoForce(p.frame(), symbol);
            if (next != null) {
                return hasCycle(next);
            } else {
                return false;
            }
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
            RSymbol symbol = getAccessedSymbol(p.expression().getAST());
            RPromise next = getPromiseNoForce(p.frame(), symbol);
            if (next != null) {
                return isMissing(next);
            } else {
                return false;
            }
        } finally {
            p.markMissingClean();
        }
    }

    // returns the recursive promise, or null if not promise/not recursive/symbol does not exist

    private static RPromise getPromiseNoForce(Frame frame, RSymbol symbol) {
        Object value;
        if (frame != null) {
            FrameSlot slot = RFrameHeader.findVariable(frame, symbol);
            if (slot == null) {
                return null;
            }
            value = frame.getObject(slot);
        } else {
            value = symbol.getValueNoForce();
        }
        if (value != null && value instanceof RPromise) {
            return (RPromise) value;
        }
        return null;
    }

    private static RSymbol getAccessedSymbol(ASTNode ast) {
        if (ast instanceof r.nodes.SimpleAccessVariable) {
            RSymbol symbol = ((r.nodes.SimpleAccessVariable) ast).getSymbol();
            return symbol;
        } else {
            return null;
        }
    }
}
