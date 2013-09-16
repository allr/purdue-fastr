package r.builtins;

import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.runtime.*;

final class Im extends CallFactory {

    static final CallFactory _ = new Im("Im", new String[]{"x"}, null);

    private Im(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        return new Builtin.Builtin1(call, names, exprs) {

            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RComplex) {
                    final RComplex orig = (RComplex) arg;
                    return new View.RDoubleProxy<RComplex>(orig) {

                        @Override public double getDouble(int i) {
                            return orig.getImag(i);
                        }

                    };
                } else if (arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical) {
                    RArray arr = (RArray) arg;
                    int size = ((RArray) arg).size();
                    return RDouble.RDoubleFactory.getUninitializedArray(size, arr.dimensions(), arr.names(), arr.attributesRef());
                } else {
                    throw RError.getNonNumericArgumentFunction(ast);
                }
            }
        };
    }
}
