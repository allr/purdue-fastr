package r.builtins;

import com.oracle.truffle.runtime.Frame;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.nodes.*;
import r.nodes.truffle.*;

// FIXME: Truffle can't inline BuiltIn.BuiltIn1, so using BuiltIn
public class IsNA {
    public static final CallFactory FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
            return new BuiltIn(call, names, exprs) {

                @Override
                public final RAny doBuiltIn(RContext context, Frame frame, RAny[] args) {
                    RAny arg = args[0];
                    if (arg instanceof RArray) {
                        final RArray a = (RArray) arg;
                        final int asize = a.size();
                        if (asize == 1) {
                            return a.isNAorNaN(0) ? RLogical.BOXED_TRUE : RLogical.BOXED_FALSE;
                        }
                        if (asize > 1) {
                            return new View.RLogicalView() {

                                @Override
                                public int size() {
                                    return asize;
                                }

                                @Override
                                public int getLogical(int i) {
                                    return a.isNAorNaN(i) ? RLogical.TRUE : RLogical.FALSE;
                                }
                            };
                        }
                        // asize == 0
                        return RLogical.EMPTY;
                    }
                    Utils.nyi("unsupported argument");
                    return null;
                }

            };
        }
    };
}

