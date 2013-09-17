package r.builtins;

import r.nodes.ast.*;

//TODO: complex numbers
final class Floor extends MathBase {
    static final CallFactory _ = new Floor("floor");

    private Floor(String name) {
        super(name);
    }

    @Override double op(ASTNode ast, double value) {
        return Math.floor(value);
    }
}
