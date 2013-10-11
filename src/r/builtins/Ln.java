package r.builtins;

import r.nodes.ast.*;


/**
 * "ln"
 *
 * <pre>
 * x -- a numeric or complex vector.
 * </pre>
 */
// TODO: complex numbers
final class Ln extends MathBase {

    static final CallFactory _ = new Ln("ln");

    private Ln(String name) {
        super(name);
    }

    @Override double op(ASTNode ast, double value) {
        return Math.log(value);
    }

    @Override void op(ASTNode ast, double[] x, double[] res) {
        for (int i = 0; i < x.length; i++) {
            res[i] = Math.log(x[i]);
        }
    }
}
