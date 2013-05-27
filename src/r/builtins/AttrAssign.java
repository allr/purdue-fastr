package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "attr<-" Arguments are:
 *
 * <pre>
 *  x -- an object whose attributes are to be accessed.
 *  which -- a non-empty character string specifying which attribute is to be accessed.
 *  value -- an object, the new value of the attribute, or NULL to remove the attribute.
 * </pre>
 *
 * The function only uses exact matches. The function does not allow missing values of which.
 */
final class AttrAssign extends CallFactory {

    static final CallFactory _ = new AttrAssign("attr<-", new String[]{"x", "which", "value"}, null);

    private AttrAssign(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    static RSymbol parseWhich(RAny arg, ASTNode ast) {
        if (arg instanceof RString) {
            RString astr = (RString) arg;
            if (astr.size() == 1) {
                String str = astr.getString(0);
                if (str != RString.NA) {
                    return RSymbol.getSymbol(str);
                }
            }

        }
        throw RError.getMustBeNonNullString(ast, "which");
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int posWhich = ai.position("which");
        final int posValue = ai.position("value");
        final int posX = ai.position("x");
        // FIXME: should specialize for constant attribute names
        // TODO: handle not-allowed lhs (non-language object, etc)
        return new Builtin(call, names, exprs) {

            @Override
            public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny x = args[posX];
                RSymbol which = parseWhich(args[posWhich], ast);
                RAny value = args[posValue];
                if (which == RSymbol.NAMES_SYMBOL) { return NamesAssign.replaceNames(x, value, ast); }
                if (which == RSymbol.DIM_SYMBOL) { return DimAssign.replaceDims(x,  value,  ast); }

                value.ref();
                // NOTE: when x is shared, attributes always have to be copied no matter what is their reference count
                RAny.Attributes attr = x.attributes();
                if (!x.isShared() && attr != null && !attr.areShared()) {
                    putOrRemove(attr, which, value);
                    return x;
                }

                x = x.isShared() ? Utils.copyAny(x) : x;
                // does not deep copy or mark attributes
                attr = attr == null ? new RAny.Attributes() : attr.copy();

                putOrRemove(attr, which, value); // FIXME: could optimize for faster removal of non-existent attributes
                return x.setAttributes(attr);
            }
        };
    }

    public static void putOrRemove(RAny.Attributes attr, RSymbol name, RAny value) {
        if (value instanceof RNull) {
            attr.remove(name);
        } else {
            attr.put(name, value);
        }
    }
}
