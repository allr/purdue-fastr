package r.builtins;

import r.nodes.ast.*;

/**
 * "log10"
 *
 * <pre>
 * x -- a numeric or complex vector.
 * </pre>
 */
// TODO: complex numbers
final class Log10 extends MathBase {

    static final CallFactory _ = new Log10("log10");

    private Log10(String name) {
        super(name);
    }

    @Override double op(ASTNode ast, double value) {
        return Math.log10(value);
    }

    @Override void op(ASTNode ast, double[] x, double[] res) {
        for (int i = 0; i < x.length; i++) {
            res[i] = Math.log10(x[i]);
        }
    }

}
