package r.builtins;

import r.*;
import r.data.*;
import r.data.internal.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;

import com.oracle.truffle.api.frame.*;

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
        super(name, new Operation() {
            @Override public double op(ASTNode ast, double value) {
                return Math.log10(value);
            }
        });
    }
}
