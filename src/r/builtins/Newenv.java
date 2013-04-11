package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "new.env"
 *
 * <pre>
 * hash  -- a logical, if TRUE the environment will use a hash table.
 * parent -- an environment to be used as the enclosure of the environment created.
 * size -- an integer specifying the initial size for a hashed environment. An internal default value will be used if
 *         size is NA or zero. This argument is ignored if hash is FALSE.
 * </pre>
 */
final class Newenv extends CallFactory {
    static final CallFactory _ = new Newenv("new.env", new String[]{"hash", "parent", "size"}, new String[]{});

    private Newenv(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    private static final int DEFAULT_SIZE = 29;
    private static final boolean DEFAULT_HASH = true;

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        final int posHash = ia.position("hash");
        final int posParent = ia.position("parent");
        final int posSize = ia.position("size");
        return new Builtin(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                boolean hash = posHash != -1 ? parseHash(args[posHash], ast) : DEFAULT_HASH;
                REnvironment rootEnvironment = null;
                MaterializedFrame parentFrame = null;
                if (posParent != -1) {
                    REnvironment env = parseParent(args[posParent], ast);
                    parentFrame = env.frame();
                    if (parentFrame == null) {
                        rootEnvironment = env;
                    }
                } else {
                    parentFrame = frame.materialize();
                    if (parentFrame == null) {
                        rootEnvironment = REnvironment.GLOBAL;
                    }
                }
                int size = DEFAULT_SIZE;
                if (hash) {
                    if (posSize != -1) {
                        size = parseSize(args[posSize], ast);
                    }
                }
                return EnvironmentImpl.Custom.create(parentFrame, rootEnvironment, hash, size);
            }
        };
    }

    // FIXME: note that R coerces to int instead of logical that one could expect here
    static boolean parseHash(RAny arg, ASTNode ast) { // not exactly R semantics
        RInt i = Convert.coerceToIntWarning(arg, ast);
        if (i.size() > 0 && i.getInt(0) != 0) { return true; }
        return DEFAULT_HASH;
    }

    static int parseSize(RAny arg, ASTNode ast) {
        RInt i = Convert.coerceToIntWarning(arg, ast);
        if (i.size() > 0) {
            int v = i.getInt(0);
            if (v != RInt.NA) { return v; }
        }
        return DEFAULT_SIZE;
    }

    static REnvironment parseParent(RAny arg, ASTNode ast) {
        if (arg instanceof REnvironment) { return (REnvironment) arg; }
        throw RError.getMustBeEnviron(ast, "parent"); // GNU-R says "environ", but it is a bug
    }

}
