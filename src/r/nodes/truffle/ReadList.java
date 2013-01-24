package r.nodes.truffle;

import com.oracle.truffle.runtime.Frame;
import com.oracle.truffle.runtime.Stable;
import r.RContext;
import r.data.*;
import r.errors.RError;
import r.nodes.ASTNode;

/**
 * Read access to a list using the dollar selector.
 *
 * Works only on lists, fails otherwise (compatible with R >= 2.6). Because only string literals and symbols are
 * allowed, the symbol creation and lookup is precached and the field access only does the hashmap search in the names
 * property of the list.
 *
 * If the list is empty, null element is returned, as if when the desired field is not present.
 *
 * At the moment, no rewrites are performed yet as the specific case always looks into the names and returns them.
 */
public abstract class ReadList extends BaseR {

    @Stable
    RNode base;
    RSymbol index;

    protected ReadList(ASTNode orig, RNode list, String idx) {
        super(orig);
        this.base = updateParent(list);
        this.index = RSymbol.getSymbol(idx);
    }

    @Override
    public Object execute(RContext context, Frame frame) {
        RAny list = (RAny) base.execute(context, frame);
        return execute(context, index, list);
    }


    /** As per R reference, should fail if not list or pairlist, otherwise the element should be returned. */
    abstract RAny execute(RContext context, RSymbol index, RAny list);


    /**
     * The class performing the field lookup. Because the dollar selector is a very special case, no rewrites are yet
     * implemented, this is only to enable future's expansions.
     * TODO inline caching & object sealing & types?
     */
    public static class SimpleFieldSelect extends ReadList {

        protected SimpleFieldSelect(ASTNode parent, RNode list, String index) {
            super(parent, list, index);
        }

        @Override
        RAny execute(RContext context, RSymbol index, RAny base) {
            if (!(base instanceof RList)) {
                throw RError.getDollarSelectionRequiresRecursiveObject(this.base.getAST());
            }
            RList list = (RList) base;
            RArray.Names names = list.names();
            if (names == null) { // return null if we have no names
                return RNull.getNull();
            }
            int i = names.map(index);
            if (i == -1) {
                return RNull.getNull();
            }
            return list.getRAny(i); // list subscript does not preserve names
        }
    }

    public static SimpleFieldSelect simpleFieldSelect(ASTNode parent, RNode list, String index) {
        return new SimpleFieldSelect(parent, list, index);
    }

}
