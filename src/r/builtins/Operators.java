package r.builtins;

import com.oracle.truffle.runtime.*;

import r.*;
import r.data.*;
import r.errors.*;
import r.nodes.*;
import r.nodes.truffle.*;
import r.nodes.truffle.UnaryMinus;

public class Operators {

    public static RNode idempotentNumeric(ASTNode ast, RSymbol[] names, RNode[] exprs) {
        return new BuiltIn.BuiltIn1(ast, names, exprs) {

            @Override
            public RAny doBuiltIn(RContext context, Frame frame, RAny arg) {
                if (arg instanceof RDouble || arg instanceof RInt || arg instanceof RLogical) {
                    return arg;
                }
                throw RError.getInvalidArgTypeUnary(ast);
            }
        };
    }

    public static final CallFactory SUB_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 1) {
                return new UnaryMinus.NumericScalar(ast, exprs[0]);
            }
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.SUB);
        }
    };

    public static final CallFactory ADD_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            if (exprs.length == 1) {
                return idempotentNumeric(ast, names, exprs); // FIXME: should implement unary plus, anyway
            }
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.ADD);
        }
    };

    public static final CallFactory MULT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.MULT);
        }
    };

    public static final CallFactory DIV_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new Arithmetic(ast, exprs[0], exprs[1], Arithmetic.DIV);
        }
    };

    public static final CallFactory MAT_MULT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new MatrixOperation.MatrixProduct(ast, exprs[0], exprs[1]);
        }
    };

    public static final CallFactory OUTER_MULT_FACTORY = new CallFactory() {

        @Override
        public RNode create(ASTNode ast, RSymbol[] names, RNode[] exprs) {
            // exprs.length == 2
            return new MatrixOperation.OuterProduct(ast, exprs[0], exprs[1]);
        }
    };
}
