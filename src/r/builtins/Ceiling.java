package r.builtins;

import r.nodes.ast.*;

//TODO: complex numbers
final class Ceiling extends MathBase {
  static final CallFactory _ = new Ceiling("ceiling");

  private Ceiling(String name) {
      super(name);
  }

  @Override double op(ASTNode ast, double value) {
      return Math.ceil(value);
  }

  @Override void op(ASTNode ast, double[] x, double[] res) {
      for (int i = 0; i < x.length; i++) {
          res[i] = Math.ceil(x[i]);
      }
  }
}
