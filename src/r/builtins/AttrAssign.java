package r.builtins;

import r.*;
import r.data.*;
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

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        ArgumentInfo ai = check(call, names, exprs);
        final int posWhich = ai.position("which");
        final int posValue = ai.position("value");
        final int posX = ai.position("x");
        // FIXME: should specialize for constant attribute names
        // TODO: handle not-allowed lhs (non-language object, etc)
        return new Builtin(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny[] args) {
                RAny x = args[posX];
                RSymbol which = Attr.getNonEmptySymbol(args[posWhich], ast, "which");
                RAny value = args[posValue];
                if (which == RSymbol.NAMES_SYMBOL) { return NamesAssign.replaceNames(x, value, ast); }
                if (which == RSymbol.DIM_SYMBOL) { return DimAssign.replaceDims(x,  value,  ast); }
                // TODO: dimensions, other special attributes custom
                // attributes
                value.ref();
                // NOTE: when x is shared, attributes always have to be
                // copied no matter what is their reference count
                RAny.Attributes attr = x.attributes();
                if (!x.isShared() && attr != null && !attr.areShared()) {
                    attr.put(which, value);
                    return x;
                }
                x = x.isShared() ? Utils.copyAny(x) : x;
                // does not deep copy or mark attributes
                attr = attr == null ? new RAny.Attributes() : attr.copy();
                attr.put(which, value);
                return x.setAttributes(attr);
            }
        };
    }
}
