package r.builtins;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.ast.*;

abstract class ArrayConstructorBase extends CallFactory {

    ArrayConstructorBase(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    static int arrayLength(RAny arg0, ASTNode ast) {
        if (arg0 == null) { return 0; }
        if (arg0 instanceof RInt) {
            RInt ilen = (RInt) arg0;
            if (ilen.size() != 1) { throw RError.getInvalidLength(ast); }
            int len = ilen.getInt(0);
            // FIXME: could be optimized for common case, assuming NA < 0
            if (len < 0) { throw RError.getVectorSizeNegative(ast); }
            if (len == RInt.NA) { throw RError.getInvalidLength(ast); }
            return len;
        }
        if (arg0 instanceof RDouble) {
            RDouble dlen = (RDouble) arg0;
            if (dlen.size() != 1) { throw RError.getInvalidLength(ast); }
            int len = Convert.double2int(dlen.getDouble(0));
            // FIXME: could be optimized for common case, assuming NA < 0
            if (len < 0) { throw RError.getVectorSizeNegative(ast); }
            if (len == RInt.NA) { throw RError.getInvalidLength(ast); }
            return len;
        }
        throw RError.getInvalidLength(ast);
    }

}
