package r.builtins;

import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import r.Truffle.*;

class AttributesAssign extends CallFactory {

    static final CallFactory _ = new AttributesAssign("attributes<-", new String[]{"obj", "value"}, null);

    AttributesAssign(String name, String[] parameters, String[] required) {
        super(name, parameters, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin2(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny obj, RAny value) {
                RAny.Attributes attr = new RAny.Attributes();
                obj = obj.setAttributes(attr);
                if (value == RNull.getNull()) { return obj; }
                if (!(value instanceof RList)) { throw RError.getAttributesListOrNull(ast); }
                RList val = (RList) value;
                if (val.names() == null) { throw new Error("no names"); }
                RSymbol[] vnames = val.names().sequence();
                for (int i = 0; i < val.size(); i++) {
                    RAny v = val.getRAny(i);
                    RSymbol s = vnames[i];
                    if (s == RSymbol.NAMES_SYMBOL) {
                        NamesAssign.replaceNames(obj, v, ast);
                    } else if (s == RSymbol.DIM_SYMBOL) {
                        DimAssign.replaceDims(obj, v, ast);
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
