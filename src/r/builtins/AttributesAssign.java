package r.builtins;

import r.data.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

/**
 * "attributes(obj) <- value"
 * 
 * <pre>
 * obj -- an object
 * value -- an appropriate named list of attributes, or NULL.
 * </pre>
 * 
 * Unlike attr it is possible to set attributes on a NULL object: it will first be coerced to an empty list. Note that
 * some attributes (namely class, comment, dim, dimnames, names, row.names and tsp) are treated specially and have
 * restrictions on the values which can be set. (Note that this is not true of levels which should be set for factors
 * via the levels replacement function.) Attributes are not stored internally as a list and should be thought of as a
 * set and not a vector. They must have unique names (and NA is taken as "NA", not a missing value). Assigning
 * attributes first removes all attributes, then sets any dim attribute and then the remaining attributes in the order
 * given: this ensures that setting a dim attribute always precedes the dimnames attribute. The names of a pairlist are
 * not stored as attributes, but are reported as if they were (and can be set by the replacement form of attributes).
 */
class AttributesAssign extends CallFactory {

    static final CallFactory _ = new AttributesAssign("attributes<-", new String[]{"obj", "value"}, null);

    AttributesAssign(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.BuiltIn2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny obj, RAny value) {
                RAny.Attributes attr = new RAny.Attributes();
                obj = obj.setAttributes(attr);
                if (value == RNull.getNull()) { return obj; }
                if (!(value instanceof RList)) { throw new Error("FIXME"); }
                RList val = (RList) value;
                if (val.names() == null) { throw new Error("no names"); }
                RSymbol[] vnames = val.names().sequence();
                for (int i = 0; i < val.size(); i++) {
                    RAny v = val.getRAny(i);
                    RSymbol s = vnames[i];
                    if (s == RSymbol.NAMES_SYMBOL) {
                        NamesAssign.replaceNames(obj, v, ast);
                    } else if (s == RSymbol.DIM_SYMBOL) {
                        throw new Error("NY");
                    } else {
                        v.ref();
                        attr.put(s, v);
                    }
                }
                return obj;
            }
        };
    }
}
