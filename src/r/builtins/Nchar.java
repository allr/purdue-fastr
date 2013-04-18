package r.builtins;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

/**
 * "nchar"
 * 
 * <pre>
 * x -- character vector, or a vector to be coerced to a character vector. Giving a factor is an error.
 * type -- character string: partial matching to one of c("bytes", "chars", "width").
 * allowNA -- Logical: should NA be returned for invalid multibyte strings or "bytes"-encoded strings
 * (rather than throwing an error)?
 * </pre>
 */
// FIXME: only partial semantics
final class Nchar extends CallFactory {

    static final CallFactory _ = new Nchar("nchar", new String[]{"x", "type", "allowNA"}, new String[]{"x"});

    private Nchar(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    public static RInt nchar(RString s) {
        int size = s.size();
        int[] content = new int[size];
        for (int i = 0; i < size; i++) {
            content[i] = s.getString(i).length();
        }
        return RInt.RIntFactory.getFor(content, s.dimensions(), s.names());
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ia = check(call, names, exprs);
        if (ia.provided("type") || ia.provided("allowNA")) { throw Utils.nyi(); }
        if (names.length == 1) { return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny x) {
                return nchar(x.asString());
            }
        }; }
        throw Utils.nyi();
    }
}
