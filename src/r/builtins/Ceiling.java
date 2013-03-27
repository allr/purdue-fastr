package r.builtins;

import r.nodes.*;

//TODO: complex numbers
final class Ceiling extends MathBase {
  static final CallFactory _ = new Ceiling("ceiling");

  private Ceiling(String name) {
      super(name);
  }

  @Override double op(ASTNode ast, double value) {
      return Math.ceil(value);
  }
}
