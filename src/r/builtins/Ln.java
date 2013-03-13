package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

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
        super(name, new Operation() {
            @Override public double op(ASTNode ast, double value) {
                return Math.log(value);
            }
        });
    }
}
