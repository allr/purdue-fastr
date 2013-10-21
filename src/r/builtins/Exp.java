package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.RError;
import r.ext.*;
import r.nodes.ast.*;
import r.nodes.exec.*;
import r.nodes.exec.Arithmetic.*;
import r.runtime.*;

/**
 * "exp"
 *
 * <pre>
 * x -- a numeric or complex vector.
 * </pre>
 */
final class Exp extends CallFactory {
    static final CallFactory _ = new Exp("exp", new String[]{"x"}, new String[]{"x"});

    private Exp(String name, String[] params, String[] required) {
        super(name, params, required);
    }

    @Override public RNode create(ASTNode call, RSymbol[] names, RNode[] exprs) {
        check(call, names, exprs);
        return new Builtin.Builtin1(call, names, exprs) {
            @Override public RAny doBuiltIn(Frame frame, RAny arg) {
                if (arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical) {
                    return TracingView.ViewTrace.trace(new View.RDoubleProxy<RDouble>(arg.asDouble()) {
                        @Override public double getDouble(int i) {
                            double d = orig.getDouble(i);
                            if (RDouble.RDoubleUtils.isNAorNaN(d)) { return RDouble.NA; }
                            double res;
                            if (RContext.hasSystemLibs()) {
                                res = SystemLibs.exp(d);
                            } else {
                                res = Math.exp(d);
                            }
                            if (RDouble.RDoubleUtils.isNAorNaN(res)) {
                                RContext.warning(ast, RError.NAN_PRODUCED);
                            }
                            return res;
                        }

                        @Override
                        public void accept(ValueVisitor v) {
                            v.visit(this);
                        }
                    });
                } else if (arg instanceof RComplex) {
                    VectorArithmetic vectorArit = Arithmetic.chooseVectorArithmetic(RComplex.BOXED_E, arg, Arithmetic.POW);
                    return vectorArit.complexBinary(RComplex.BOXED_E, (RComplex) arg, Arithmetic.POW, ast);
                } else {
                    throw RError.getNonNumericMath(ast);
                }
            }
        };
    }
}
