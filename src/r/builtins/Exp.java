package r.builtins;

import r.*;
import r.ext.*;
import r.nodes.ast.*;

/**
 * "exp"
 *
 * <pre>
 * x -- a numeric or complex vector.
 * </pre>
 */
//TODO: complex numbers
final class Exp extends MathBase {
  static final CallFactory _ = new Exp("exp");

  private Exp(String name) {
      super(name);
  }

  @Override
  double op(ASTNode ast, double value) {
      if (RContext.hasSystemLibs()) {
          return SystemLibs.exp(value);
      } else {
          return Math.exp(value);
      }
  }

  @Override
  void op(ASTNode ast, double[] x, double[] res) {
      if (RContext.hasSystemLibs()) {
          SystemLibs.exp(x, res, x.length);
      } else {
          for (int i = 0; i < x.length; i++) {
              res[i] = Math.exp(x[i]);
          }
      }
  }

}

//                } else if (arg instanceof RComplex) {
//                    VectorArithmetic vectorArit = Arithmetic.chooseVectorArithmetic(RComplex.BOXED_E, arg, Arithmetic.POW);
//                    return vectorArit.complexBinary(RComplex.BOXED_E, (RComplex) arg, Arithmetic.POW, ast);
