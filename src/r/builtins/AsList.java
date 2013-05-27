package r.builtins;

import r.data.*;
import r.nodes.*;

//FIXME: add all.names support
final class AsList extends AsBase {

    static final CallFactory _ = new AsList("as.list", new String[]{"x", "..."}, new String[]{});

    private AsList(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override RAny genericCast(ASTNode ast, RAny arg) {
        return genericAsList(ast, arg);
    }

    @Override
    RAny getEmpty() {
        return RList.EMPTY;
    }

}
